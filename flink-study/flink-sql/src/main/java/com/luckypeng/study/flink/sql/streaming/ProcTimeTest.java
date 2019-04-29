package com.luckypeng.study.flink.sql.streaming;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Tumble;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sources.DefinedProctimeAttribute;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.types.Row;
import org.apache.flink.util.StringUtils;
import org.junit.Test;

import javax.annotation.Nullable;

/**
 * Processing time Table Test
 *
 * 要求安装 NetCat
 * linux 和 Mac 上是 nc 命令: nc -kl 12348
 * windows 可以从 http://nmap.org/dist/ncat-portable-5.59BETA1.zip 下载解压使用 ncat.exe 命令: ncat -kl 12348
 * @author coalchan
 * @see <a href="https://ci.apache.org/projects/flink/flink-docs-release-1.8/dev/table/streaming/time_attributes.html"></a>
 */
public class ProcTimeTest {
    /**
     * DataStream-to-Table
     * 输入元素样例：Jack
     * @throws Exception
     */
    @Test
    public void testStreamToTable() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        Table table = tableEnv.fromDataStream(dataStream, "userId, actionTime.proctime");

        Table result = table
                .window(Tumble.over("10.seconds").on("actionTime").as("w"))
                .groupBy("userId, w")
                .select("userId, w.start as windowStart");

        tableEnv.toAppendStream(result, Row.class).print();
        env.execute();
    }

    /**
     * 使用 TableSource
     * 输入元素样例：
     * 1,78
     * 2,89
     * 2,98
     * @throws Exception
     */
    @Test
    public void testTableSource() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        tableEnv.registerTableSource("userAction", new UserActionSource());

        Table result = tableEnv.scan("userAction").as("userId, score")
                .window(Tumble.over("10.seconds").on("actionTime").as("w"))
                .groupBy("userId, w")
                .select("userId, w.start as windowStart, max(score) as maxScore");

        tableEnv.toAppendStream(result, Row.class).print();
        env.execute();
    }
}

class UserActionSource implements StreamTableSource<Tuple2<Integer, Integer>>, DefinedProctimeAttribute {

    @Nullable
    @Override
    public String getProctimeAttribute() {
        return "actionTime";
    }

    @Override
    public DataStream<Tuple2<Integer, Integer>> getDataStream(StreamExecutionEnvironment execEnv) {
        DataStream<String> dataStream = execEnv.socketTextStream("localhost", 12348);
        return dataStream
                .filter(str -> !StringUtils.isNullOrWhitespaceOnly(str))
                .map(new MapFunction<String, Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<Integer, Integer> map(String value) throws Exception {
                        String[] array = value.split(",");
                        return Tuple2.of(Integer.parseInt(array[0]), Integer.parseInt(array[1]));
                    }
                });
    }

    @Override
    public TypeInformation<Tuple2<Integer, Integer>> getReturnType() {
        return Types.TUPLE(Types.INT, Types.INT);
    }

    @Override
    public TableSchema getTableSchema() {
        return TableSchema.builder()
                .field("f0", Types.INT)
                .field("f1", Types.INT)
                .field("actionTime", Types.SQL_TIMESTAMP)
                .build();
    }
}
