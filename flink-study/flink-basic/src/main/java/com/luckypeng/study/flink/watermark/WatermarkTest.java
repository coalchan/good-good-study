package com.luckypeng.study.flink.watermark;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.junit.Test;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author coalchan
 * @date 2019/3/22 14:20
 */
@Slf4j
public class WatermarkTest {
    /**
     * 自动周期水位线
     * 输入元素样例：1,2019-01-01 10:00:00
     * @throws Exception
     */
    @Test
    public void testPeriodWatermark() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        // 设置自动发射水印间隔
//        env.getConfig().setAutoWatermarkInterval(3000);

        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);

        dataStream
                .map(new MapFunction<String, Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> map(String value) throws Exception {
                        String[] arr = value.split(",");
                        return Tuple2.of(arr[0], arr[1]);
                    }
                })
                .assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks<Tuple2<String, String>>() {
                    // 这里参考了 org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
                    // 容纳了延迟 < maxOutOfOrderness 的数据

                    private long currentMaxTimestamp;
                    // 3.5 seconds
                    private final long maxOutOfOrderness = 3500;

                    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    /**
                     * 通过该函数获取水位线，当获取到的水位线的时间戳大于窗口的结束时间戳，则出发窗口上的操作函数。
                     * @return
                     */
                    @Nullable
                    @Override
                    public Watermark getCurrentWatermark() {
                        Watermark watermark = new Watermark(currentMaxTimestamp - maxOutOfOrderness);
                        return watermark;
                    }

                    @Override
                    public long extractTimestamp(Tuple2<String, String> element, long previousElementTimestamp) {
                        long timestamp = 0;
                        try {
                            timestamp = format.parse(element.f1).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        System.out.println("previous currentMaxTimestamp: " + format.format(currentMaxTimestamp));
                        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
                        System.out.println("element: " + element + ", currentMaxTimestamp: " + format.format(currentMaxTimestamp));
                        return timestamp;
                    }
                })
                .keyBy(0)
                .timeWindow(Time.seconds(10))
                .process(new ProcessWindowFunction<Tuple2<String,String>, String, Tuple, TimeWindow>() {
                    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    @Override
                    public void process(Tuple tuple, Context context, Iterable<Tuple2<String, String>> elements, Collector<String> out) throws Exception {
                        int count = 0;
                        String str = "";
                        for (Tuple2<String,String> tuple2: elements) {
                            count ++;
                            str += ",  " + tuple2.f0 + " | " + tuple2.f1;
                        }
                        String output = "Window: (" +
                                format.format(context.window().getStart()) + ", "  +
                                format.format(context.window().getEnd()) +
                                "), count: " + count + ", values: " + str;
                        out.collect(output);
                    }
                })
                .print();

        env.execute();
    }
}
