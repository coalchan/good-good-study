package com.luckypeng.study.flink.training.processfunction;

import lombok.Data;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.flink.util.StringUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Process Function 是 Flink 提供的一个 low-level API，用于实现更高级的功能
 * @author coalchan
 * @date 2019/4/17 10:34
 */
public class KeyedProcessFunctionTest {
    /**
     * 这里定义了水位线，但是没有窗口的概念了，所有的动作的触发都由定时器来定义。所以这里不会丢弃任何数据。
     * 参考数据输入：
     *
     * 1,2019-01-01 10:00:00
     * 1,2019-01-01 10:00:06
     * 1,2019-01-01 10:00:09
     * 2,2019-01-01 10:00:12
     * 3,2019-01-01 10:00:09
     * 2,2019-01-01 10:00:15
     * 1,2019-01-01 10:00:01
     * 1,2019-01-01 10:00:02
     * 1,2019-01-01 10:00:03
     * 2,2019-01-01 10:00:18
     * 2,2019-01-01 10:00:19
     * 2,2019-01-01 10:00:22
     * 2,2019-01-01 10:00:23
     * 2,2019-01-01 10:00:24
     * 2,2019-01-01 10:00:25
     *
     * 这种情况下的 1,2019-01-01 10:00:01 及其后共3条数据都能被正常处理
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        dataStream
                .filter(str -> !StringUtils.isNullOrWhitespaceOnly(str))
                .map(new MapFunction<String, ClickEvent>() {
                    @Override
                    public ClickEvent map(String value) throws Exception {
                        String[] arr = value.split(",");
                        return ClickEvent.of(arr[0], arr[1]);
                    }
                })
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<ClickEvent>(Time.seconds(5)) {
                    @Override
                    public long extractTimestamp(ClickEvent element) {
                        try {
                            return format.parse(element.getEventTime()).getTime();
                        } catch (ParseException e) {
                            throw new RuntimeException("格式错误");
                        }
                    }
                })
                .keyBy(ClickEvent::getUserId)
                .process(new KeyedProcessFunction<String, ClickEvent, String>() {
                    private ListState<ClickEvent> listState;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        listState = getRuntimeContext().getListState(new ListStateDescriptor<>(
                                "listState-state",
                                ClickEvent.class));
                    }

                    /**
                     * 每获取一个元素，都会调用该方法
                     *
                     * 这里对于事件的处理采用了比较巧妙的方法：当
                     * 1. 事件正常到来时(即比水位线大)，则按照正常的处理速度，每隔 10s 处理一次
                     * 2. 事件延迟到达，则也是按照正常的处理速度，参考当前最新的水位线，在其基础上加 10s ，从而也达到 10s 处理一次的效果。
                     *
                     * 这样的处理对于下面这种数据流很有用：
                     * 某些数据（由 userId 标记分组，即某个用户的数据）总是可能会有延迟，而且是一批一批的延迟，
                     * 这有点像用户网络不好，经常断网，从而可能导致——每隔一定的时间才会上报之前的数据。
                     *
                     * 这样的处理方式的好处在于，对于大批量的延迟数据，不会被分开处理（即分别在不同的定时器 onTimer 进行处理）。
                     * @param value
                     * @param ctx
                     * @param out
                     * @throws Exception
                     */
                    @Override
                    public void processElement(ClickEvent value, Context ctx, Collector<String> out) throws Exception {
                        System.out.println("process value: " + value + ", watermark: " + format.format(ctx.timerService().currentWatermark()));

                        // 每隔 10s 钟处理一次
                        long duration = 10000;

                        // 这里的作用在于，当下面的 onTimer 方法还没有使用状态中存储的元素时（即listState不为空）
                        // 不注册额外的定时器
                        if (!listState.get().iterator().hasNext()) {
                            long eventTs = format.parse(value.getEventTime()).getTime();

                            if (eventTs < ctx.timerService().currentWatermark()) {
                                // 事件时间小于水位线，即延迟的数据
                                // 对于这样的数据，设置定时器为 当前的水位线 再加上 10s
                                long registerTime = ctx.timerService().currentWatermark() + duration;
                                ctx.timerService().registerEventTimeTimer(registerTime);
                                System.out.println("+++++++++ " + ctx.getCurrentKey() + " delayed register time: " + format.format(registerTime));
                            } else {
                                // 事件时间大于或等于水位线，即正常数据
                                // 对于这样的数据，设置定时为 该事件时间 再加上 10s
                                long registerTime = eventTs + duration;
                                ctx.timerService().registerEventTimeTimer(registerTime);
                                System.out.println("--------- " + ctx.getCurrentKey() + " register time: " + format.format(registerTime));
                            }
                        }

                        listState.add(value);
                    }

                    /**
                     * 这里定时器的触发有两个条件 1. 所在的 key 在 之前注册了定时器； 2. 水位线到达了（大于或等于）定时器的时间
                     * @param timestamp
                     * @param ctx
                     * @param out
                     * @throws Exception
                     */
                    @Override
                    public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                        System.out.println("on Timer start-------------- key = " + ctx.getCurrentKey());
                        for (ClickEvent event : listState.get()) {
                            System.out.println("get:" + event);
                        }
                        System.out.println("on Timer end-------------- key = " + ctx.getCurrentKey());
                        listState.clear();
                    }
                })
                .print();

        env.execute();
    }
}

@Data
class ClickEvent {
    private String userId;
    private String eventTime;

    public static ClickEvent of (String userId, String eventTime) {
        ClickEvent event = new ClickEvent();
        event.userId = userId;
        event.eventTime = eventTime;
        return event;
    }
}
