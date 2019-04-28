package com.luckypeng.study.flink.training.watermark;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.StringUtils;
import org.junit.Test;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 水位线：了解水位线产生的时机以及何时触发窗口的操作
 *
 * 要求安装NetCat
 * linux 和 Mac 上是 nc 命令: nc -kl 12348
 * windows 可以从 http://nmap.org/dist/ncat-portable-5.59BETA1.zip 下载解压使用 ncat.exe 命令: ncat -kl 12348
 * @author coalchan
 * @date 2019/3/22 14:20
 */
@Slf4j
public class WatermarkTest {
    /**
     * 自动周期水位线：基于固定的周期来产生水位线
     * 输入元素样例：1,2019-01-01 10:00:00
     * @throws Exception
     */
    @Test
    public void testPeriodicWatermark() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        // 设置自动发射水位线间隔
        // env.getConfig().setAutoWatermarkInterval(3000);

        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);

        dataStream
                .filter(str -> !StringUtils.isNullOrWhitespaceOnly(str))
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
                     * 该函数会并发调用
                     * 通过该函数获取水位线，当获取到的水位线的时间戳大于窗口的结束时间戳，则出发窗口上的操作函数。
                     * @return
                     */
                    @Nullable
                    @Override
                    public Watermark getCurrentWatermark() {
                        Watermark watermark = new Watermark(currentMaxTimestamp - maxOutOfOrderness);
                        // 为了查看调用情况，可以将从下面的注释和上面的设置自动发射水位线间隔的注释打开
                        // log.info("getCurrentWatermark: " + format.format(watermark.getTimestamp()));
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
                    // 触发此操作的前提是水位线到达窗口的结尾，而这里的水位线取的是所有并发水位线中最小的那个
                    // 具体可以参考 https://ci.apache.org/projects/flink/flink-docs-release-1.7/dev/event_time.html#watermarks-in-parallel-streams
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

    /**
     * 自动周期水位线：基于固定的周期来产生水位线
     * 输入元素样例：1,2019-01-01 10:00:00
     * @throws Exception
     */
    @Test
    public void testPeriodicWatermarkWithReduce() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);

        dataStream
                .filter(str -> !StringUtils.isNullOrWhitespaceOnly(str))
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
                        log.info("element: " + element + ", currentMaxTimestamp: " + format.format(currentMaxTimestamp));
                        return timestamp;
                    }
                })
                .keyBy(0)
                .timeWindow(Time.seconds(10))
                .reduce(new ReduceFunction<Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> reduce(Tuple2<String, String> value1, Tuple2<String, String> value2) throws Exception {
                        int maxKey = Math.max(Integer.parseInt(value1.f0), Integer.parseInt(value2.f0));
                        String value = value1.f1 + ", " + value2.f1;
                        System.out.println("key = " + maxKey + ", value = " + value);
                        return Tuple2.of(String.valueOf(maxKey), value);
                    }
                })
                .print();

        env.execute();
    }

    /**
     * 带标记的水位线：根据某个特定的事件来产生水位线，如下面的例子中只有遇到秒数为7,17,27,37,47,57 才会产生水位线。
     * 输入元素样例：1,2019-01-01 10:00:00
     * @throws Exception
     */
    @Test
    public void testPunctuatedWatermark() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);

        dataStream
                .filter(str -> !StringUtils.isNullOrWhitespaceOnly(str))
                .map(new MapFunction<String, Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> map(String value) throws Exception {
                        String[] arr = value.split(",");
                        return Tuple2.of(arr[0], arr[1]);
                    }
                })
                .assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<Tuple2<String, String>>() {
                    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    @Nullable
                    @Override
                    public Watermark checkAndGetNextWatermark(Tuple2<String, String> lastElement, long extractedTimestamp) {
                        System.out.println("element: " + lastElement + ", ts: " + format.format(extractedTimestamp));
                        int length = String.valueOf(extractedTimestamp).length();
                        return String.valueOf(extractedTimestamp).substring(length-4).equals("7000") ? new Watermark(extractedTimestamp) : null;
                    }

                    @Override
                    public long extractTimestamp(Tuple2<String, String> element, long previousElementTimestamp) {
                        long timestamp = 0;
                        try {
                            timestamp = format.parse(element.f1).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return timestamp;
                    }
                })
                .keyBy(0)
                .timeWindow(Time.seconds(10))
                .reduce(new ReduceFunction<Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> reduce(Tuple2<String, String> value1, Tuple2<String, String> value2) throws Exception {
                        return Tuple2.of(value1.f0, value1.f1 + ", " + value2.f1);
                    }
                })
                .print();

        env.execute();
    }
}
