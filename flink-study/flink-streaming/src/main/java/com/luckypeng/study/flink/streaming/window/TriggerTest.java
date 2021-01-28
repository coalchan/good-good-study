package com.luckypeng.study.flink.streaming.window;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.StringUtils;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 触发器
 *
 * 要求安装NetCat
 * linux 和 Mac 上是 nc 命令: nc -kl 12348
 * windows 可以从 http://nmap.org/dist/ncat-portable-5.59BETA1.zip 下载解压使用 ncat.exe 命令: ncat -kl 12348
 * @author coalchan
 * @date 2019/3/4 14:42
 */
public class TriggerTest {
    @Test
    public void test() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream
                .map(Double::parseDouble)
                .timeWindowAll(Time.seconds(10))
                .trigger(new MyTrigger())
                .max(0)
                .print();
        env.execute();
    }

    /**
     * 每隔5秒钟统计一次当前所在一分钟(0-59s)的数据量
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream
                .map(str -> 1)
                .timeWindowAll(Time.minutes(1))
                .trigger(new RefreshTrigger(Time.seconds(5)))
                .sum(0)
                .print();
        env.execute();
    }

    /**
     * 每隔 10 秒或者 3 个元素即计算一批的数据
     * @throws Exception
     */
    @Test
    public void testTimeAndCount() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream
                .filter(str -> !StringUtils.isNullOrWhitespaceOnly(str))
                .map(new MapFunction<String, Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> map(String value) throws Exception {
                        String[] arr = value.split(",");
                        return Tuple2.of(arr[0], arr[1]);
                    }
                }).keyBy(0).timeWindow(Time.of(10, TimeUnit.SECONDS))
                .trigger(new MyCountTrigger(3))
                .reduce((ReduceFunction<Tuple2<String, String>>) (v1, v2) -> {
                    return new Tuple2<>(v1.f0, v1.f1 + v2.f1);
                }).print();
        env.execute();
    }
}

/**
 * 接收到 -1 或者到达处理时间周期，即出发窗口函数操作
 */
class MyTrigger extends Trigger<Double, TimeWindow> {
    @Override
    public TriggerResult onElement(Double element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
        System.out.println("current element: " + element);
        if (-1 == element) {
            return TriggerResult.FIRE;
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.FIRE;
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {

    }

    @Override
    public String toString() {
        return "MyTrigger()";
    }
}

/**
 * 每隔 interval 时间触发一次
 */
class RefreshTrigger extends Trigger<Integer, TimeWindow> {
    private long interval;

    public RefreshTrigger(Time interval) {
        this.interval = interval.toMilliseconds();
    }

    @Override
    public TriggerResult onElement(Integer element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
        long now = ctx.getCurrentProcessingTime();
        long next = now - now % this.interval + this.interval;
        ctx.registerProcessingTimeTimer(next);
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.FIRE;
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {

    }
}

/**
 * 计数触发器
 */
class MyCountTrigger extends Trigger<Tuple2<String, String>, TimeWindow> {
    private final long maxCount;

    /**
     * 用于储存窗口当前数据量的状态对象
     */
    private final ReducingStateDescriptor<Long> stateDesc =
            new ReducingStateDescriptor<>("count", new Sum(), LongSerializer.INSTANCE);

    public MyCountTrigger(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public TriggerResult onElement(Tuple2<String, String> element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
        ReducingState<Long> count = ctx.getPartitionedState(stateDesc);
        count.add(1L);
        if (count.get() >= maxCount) {
            count.clear();
            return TriggerResult.FIRE_AND_PURGE;
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        /**
         * 这里 TriggerResult.FIRE 和 TriggerResult.PURGE 结果一致，因为该 trigger 内没有特殊设置下次的触发时间（ctx.registerProcessingTimeTimer）
         */
        return TriggerResult.FIRE;
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
        ctx.getPartitionedState(stateDesc).clear();
    }

    @Override
    public boolean canMerge() {
        return true;
    }

    @Override
    public void onMerge(TimeWindow window, OnMergeContext ctx) throws Exception {
        ctx.mergePartitionedState(stateDesc);
    }

    @Override
    public String toString() {
        return "MyCountTrigger(" +  maxCount + ")";
    }

    /**
     * 计数函数
     */
    private static class Sum implements ReduceFunction<Long> {
        private static final long serialVersionUID = 1L;

        @Override
        public Long reduce(Long value1, Long value2) throws Exception {
            return value1 + value2;
        }

    }
}