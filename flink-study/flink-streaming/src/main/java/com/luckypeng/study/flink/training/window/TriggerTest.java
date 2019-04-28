package com.luckypeng.study.flink.training.window;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Test;

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