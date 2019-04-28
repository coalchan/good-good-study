package com.luckypeng.study.flink.training.window;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Test;

import java.text.SimpleDateFormat;

/**
 * 分配器
 * 计算最大值（字符串比较）
 * 时间特征: 处理时间 {@link org.apache.flink.streaming.api.TimeCharacteristic.ProcessingTime}
 *
 * 要求安装NetCat
 * linux 和 Mac 上是 nc 命令: nc -kl 12348
 * windows 可以从 http://nmap.org/dist/ncat-portable-5.59BETA1.zip 下载解压使用 ncat.exe 命令: ncat -kl 12348
 * @author coalchan
 * @date 2019/2/27 10:49
 */
public class AssignerTest {
    /**
     * 窗口类型: 滚动时间窗口
     * @throws Exception
     */
    @Test
    public void tumblingTimeWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream.timeWindowAll(Time.seconds(10)).max(0).print();
        env.execute();
    }

    /**
     * 窗口类型: 滚动计数窗口
     * @throws Exception
     */
    @Test
    public void tumblingCountWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream.countWindowAll(3).max(0).print();
        env.execute();
    }

    /**
     * 窗口类型: 滑动时间窗口
     * @throws Exception
     */
    @Test
    public void slidingTimeWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream.timeWindowAll(Time.seconds(10), Time.seconds(3)).max(0).print();
        env.execute();
    }

    /**
     * 窗口类型: 滑动计数窗口
     * @throws Exception
     */
    @Test
    public void slidingCountWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream.countWindowAll(10, 3).max(0).print();
        env.execute();
    }

    /**
     * 窗口类型：事件时间会话窗口
     * @throws Exception
     */
    @Test
    public void eventTimeSessionWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        DataStream<Tuple3<String, String, Long>> dataStream = env.addSource(
                new RichParallelSourceFunction<Tuple3<String, String, Long>> () {
                    private volatile boolean running = true;

                    @Override
                    public void run(SourceContext<Tuple3<String, String, Long>> ctx) throws Exception {
                        Tuple3[] elements = new Tuple3[]{
                                Tuple3.of("a", "1", 1000000050000L),
                                Tuple3.of("a", "2", 1000000054000L),
                                Tuple3.of("a", "3", 1000000079900L),
                                Tuple3.of("a", "4", 1000000115000L),
                                Tuple3.of("b", "5", 1000000100000L),
                                Tuple3.of("b", "6", 1000000108000L)
                        };

                        int count = 0;
                        while (running && count < elements.length) {
                            ctx.collect(new Tuple3<>((String) elements[count].f0, (String) elements[count].f1, (Long) elements[count].f2));
                            count++;
                            Thread.sleep(1000);
                        }
                    }

                    @Override
                    public void cancel() {
                        running = false;
                    }
                });


        long delay = 5L;

        // 这种情况下，第5条数据，会由于第4条数据后的 watermark - gap >= 第5条的 watermark ， 因而该记录会被丢弃
        long windowGap = 10L;

        // 这种情况下，第5条数据，会由于第4条数据后的 watermark - gap < 第5条的 watermark ， 因而该记录会被保留
        windowGap = 11L;

        dataStream.assignTimestampsAndWatermarks(
                new BoundedOutOfOrdernessTimestampExtractor<Tuple3<String, String, Long>>(Time.seconds(delay)) {
            @Override
            public long extractTimestamp(Tuple3<String, String, Long> element) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                System.out.println(element.f0 + "\t" + element.f1 +
                        " watermark -> " + format.format(getCurrentWatermark().getTimestamp()) +
                        " timestamp -> " + format.format(element.f2));
                return element.f2;
            }
        })
                .keyBy(0)
                .window(EventTimeSessionWindows.withGap(Time.seconds(windowGap)))
                .reduce(new ReduceFunction<Tuple3<String, String, Long>>() {
                    @Override
                    public Tuple3<String, String, Long> reduce(Tuple3<String, String, Long> value1,
                                                               Tuple3<String, String, Long> value2) throws Exception {
                        return Tuple3.of(value1.f0, value1.f1 + "" + value2.f1, 1L);
                    }
                })
                .print();

        env.execute();
    }
}
