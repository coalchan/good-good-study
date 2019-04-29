package com.luckypeng.study.flink.sql.streaming;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Tumble;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sources.DefinedRowtimeAttributes;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.table.sources.tsextractors.StreamRecordTimestamp;
import org.apache.flink.table.sources.wmstrategies.PreserveWatermarks;
import org.apache.flink.types.Row;
import org.apache.flink.util.StringUtils;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * Event time Table Test
 *
 * 要求安装 NetCat
 * linux 和 Mac 上是 nc 命令: nc -kl 12348
 * windows 可以从 http://nmap.org/dist/ncat-portable-5.59BETA1.zip 下载解压使用 ncat.exe 命令: ncat -kl 12348
 * @author coalchan
 * @see <a href="https://ci.apache.org/projects/flink/flink-docs-release-1.8/dev/table/streaming/time_attributes.html"></a>
 */
public class RowTimeTest {
    /**
     * DataStream-to-Table
     * 输入元素样例：
     * 1,55,2018-01-01 10:00:00
     * 1,60,2018-01-01 10:00:08
     * 1,66,2018-01-01 10:00:12
     * 1,59,2018-01-01 10:00:07
     * 1,65,2018-01-01 10:00:13
     * @throws Exception
     */
    @Test
    public void testStreamToTable() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        DataStream<Tuple3<Integer, Integer, Long>> dataStream = env.socketTextStream("localhost", 12348)
                .filter(str -> !StringUtils.isNullOrWhitespaceOnly(str))
                .map(new MapFunction<String, Tuple3<Integer, Integer, Long>>() {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    @Override
                    public Tuple3<Integer, Integer, Long> map(String value) throws Exception{
                        String[] array = value.split(",");
                        long timestamp = format.parse(array[2]).getTime();
                        return Tuple3.of(Integer.parseInt(array[0]), Integer.parseInt(array[1]), timestamp);
                    }
                })
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Tuple3<Integer, Integer, Long>>(Time.seconds(3)) {
                    @Override
                    public long extractTimestamp(Tuple3<Integer, Integer, Long> element) {
                        return element.f2;
                    }
                });

        Table table = tableEnv.fromDataStream(dataStream, "userId, score, actionTime.rowtime");

        Table result = table
                .window(Tumble.over("10.seconds").on("actionTime").as("w"))
                .groupBy("userId, w")
                .select("userId, max(score)");

        tableEnv.toAppendStream(result, Row.class).print();
        env.execute();
    }

    /**
     * 使用 TableSource
     * 输入元素样例：
     * 1,55,2018-01-01 10:00:00
     * 1,60,2018-01-01 10:00:08
     * 1,66,2018-01-01 10:00:12
     * 1,59,2018-01-01 10:00:07
     * 1,65,2018-01-01 10:00:13
     * @throws Exception
     */
    @Test
    public void testTableSource() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        tableEnv.registerTableSource("userAction", new UserSource());

        Table result = tableEnv.scan("userAction")
                .window(Tumble.over("10.seconds").on("actionTime").as("w"))
                .groupBy("userId, w")
                .select("userId, w.start as windowStart, max(score) as maxScore");

        tableEnv.toAppendStream(result, Row.class).print();
        env.execute();
    }
}

class UserSource implements StreamTableSource<Row>, DefinedRowtimeAttributes {

    @Override
    public List<RowtimeAttributeDescriptor> getRowtimeAttributeDescriptors() {
        RowtimeAttributeDescriptor descriptor = new RowtimeAttributeDescriptor(
                "actionTime", new StreamRecordTimestamp(), new PreserveWatermarks());
        return Collections.singletonList(descriptor);
    }

    @Override
    public DataStream<Row> getDataStream(StreamExecutionEnvironment execEnv) {
        return execEnv.socketTextStream("localhost", 12348)
                .filter(str -> !StringUtils.isNullOrWhitespaceOnly(str))
                .map(new MapFunction<String, Row>() {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    @Override
                    public Row map(String value) throws Exception {
                        String[] array = value.split(",");
                        long timestamp = format.parse(array[2]).getTime();
                        return Row.of(Integer.parseInt(array[0]), Integer.parseInt(array[1]), timestamp);
                    }
                })
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Row>(Time.seconds(3)) {
                    @Override
                    public long extractTimestamp(Row element) {
                        return (Long) element.getField(2);
                    }
                }).returns(getReturnType());
    }

    @Override
    public TypeInformation<Row> getReturnType() {
        return Types.ROW_NAMED(new String[]{"userId", "score", "actionTime"},
                new TypeInformation[]{Types.INT, Types.INT, Types.LONG});
    }

    @Override
    public TableSchema getTableSchema() {
        return TableSchema.builder()
                .field("userId", Types.INT)
                .field("score", Types.INT)
                .field("actionTime", Types.SQL_TIMESTAMP)
                .build();
    }
}
