package com.luckypeng.study.flink.window;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;
import org.apache.flink.util.StringUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 允许延迟
 * allowedLateness 表示在水位线已经通过了窗口的末尾时，还可以再允许延迟在一定时间内的元素再次进入该窗口，并同时再次触发窗口的操作。
 *
 * 要求安装NetCat
 * linux 和 Mac 上是 nc 命令: nc -kl 12348
 * windows 可以从 http://nmap.org/dist/ncat-portable-5.59BETA1.zip 下载解压使用 ncat.exe 命令: ncat -kl 12348
 * @author coalchan
 * @date 2019/3/25 14:56
 */
public class LatenessTest {
    /**
     * 输入元素样例： 1,2019-01-01 10:00:01
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.setParallelism(1);

        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);

        final OutputTag<Tuple2<String, String>> lateLogTag = new OutputTag<Tuple2<String, String>>("late-data") {
            private static final long serialVersionUID = 8680270764740798729L;
        };

        SingleOutputStreamOperator<Tuple2<String, String>> result = dataStream
                .filter(str -> !StringUtils.isNullOrWhitespaceOnly(str))
                .map(new MapFunction<String, Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> map(String value) throws Exception {
                        String[] arr = value.split(",");
                        return Tuple2.of(arr[0], arr[1]);
                    }
                })
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Tuple2<String, String>>(Time.milliseconds(3500)) {
                    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    @Override
                    public long extractTimestamp(Tuple2<String, String> element) {
                        long timestamp = 0;
                        try {
                            timestamp = format.parse(element.f1).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        System.out.println("element: " + element + ", currentMaxTimestamp: " + format.format(getCurrentWatermark().getTimestamp()));
                        return timestamp;
                    }
                })
                .keyBy(0)
                .timeWindow(Time.seconds(10))
//                .allowedLateness(Time.seconds(2)) // 如果这里设置延迟的话，则延迟数据的判定将会再增加 2 秒
                .sideOutputLateData(lateLogTag)
                .reduce(new ReduceFunction<Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> reduce(Tuple2<String, String> value1, Tuple2<String, String> value2) throws Exception {
                        return Tuple2.of(value1.f0, value1.f1 + ", " + value2.f1);
                    }
                });

        result.print();

        result.getSideOutput(lateLogTag)
                .map(tuple2 -> "late: " + tuple2)
                .print();

        env.execute();
    }
}
