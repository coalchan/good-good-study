package com.luckypeng.study.flink.window;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Test;

/**
 * 触发器
 * @author chenzhipeng
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
