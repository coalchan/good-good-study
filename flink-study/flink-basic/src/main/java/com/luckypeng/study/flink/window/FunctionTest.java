package com.luckypeng.study.flink.window;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.junit.Test;

/**
 * window 函数操作
 * 要求安装NetCat
 * linux 和 Mac 上是 nc 命令: nc -kl 12348
 * windows 可以从 http://nmap.org/dist/ncat-portable-5.59BETA1.zip 下载解压使用 ncat.exe 命令: ncat -kl 12348
 * @author coalchan
 * @date 2019/3/4
 */
public class FunctionTest {
    /**
     * ReduceFunction
     * @throws Exception
     */
    @Test
    public void testReduce() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream
                .timeWindowAll(Time.seconds(10))
                .reduce((ReduceFunction<String>) (value1, value2) -> value1 + value2).print();
        env.execute();
    }

    /**
     * 计算平均数
     */
    private class AverageAggregate implements AggregateFunction<Double, Tuple2<Integer, Double>, Double> {

        @Override
        public Tuple2<Integer, Double> createAccumulator() {
            return Tuple2.of(0, 0d);
        }

        @Override
        public Tuple2<Integer, Double> add(Double value, Tuple2<Integer, Double> accumulator) {
            return Tuple2.of(accumulator.f0 + 1, accumulator.f1 + value);
        }

        @Override
        public Double getResult(Tuple2<Integer, Double> accumulator) {
            return accumulator.f1 / accumulator.f0;
        }

        @Override
        public Tuple2<Integer, Double> merge(Tuple2<Integer, Double> a, Tuple2<Integer, Double> b) {
            return Tuple2.of(a.f0 + b.f0, a.f1 + b.f1);
        }
    }

    /**
     * AggregateFunction
     * @throws Exception
     */
    @Test
    public void testAggregate() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream
                .map(Double::parseDouble)
                .timeWindowAll(Time.seconds(10))
                .aggregate(new AverageAggregate())
                .print();
        env.execute();
    }

    /**
     * ProcessAllWindowFunction
     * 该函数包含了一个迭代器，其中有当前窗口的所有元素，另外有一个具有访问时间和状态信息的上下文对象。
     * 所以该函数与其他函数相比提供了更多灵活性，但这是以牺牲性能和消耗资源为代价的。
     * @throws Exception
     */
    @Test
    public void testProcess() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream
                .timeWindowAll(Time.seconds(10))
                .process(new ProcessAllWindowFunction<String, String, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<String> elements, Collector<String> out) throws Exception {
                        int count = 0;
                        for (String str: elements) {
                            count ++;
                        }
                        out.collect("Window: " + context.window() + ", count: " + count);
                    }
                }).print();
        env.execute();
    }


    private class MyReduceFunction implements ReduceFunction<String> {
        @Override
        public String reduce(String value1, String value2) throws Exception {
            return value1.length() > value2.length() ? value1 : value2;
        }
    }

    /**
     * 结合 ReduceFunction 的 ProcessAllWindowFunction
     * @throws Exception
     */
    @Test
    public void testProcessWithReduce() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream
                .timeWindowAll(Time.seconds(10))
                .reduce(new MyReduceFunction(), new ProcessAllWindowFunction<String, String, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<String> elements, Collector<String> out) throws Exception {
                        int count = 0;
                        String last = "";
                        for (String str: elements) {
                            count ++;
                            last = str;
                        }
                        // 这里的 count 一定为1，因为经过了 MyReduceFunction 的聚合
                        out.collect("Window: " + context.window() + ", count: " + count + ", last: " + last);
                    }
                }).print();
        env.execute();
    }

    /**
     * 结合 AggregateFunction 的 ProcessAllWindowFunction
     * @throws Exception
     */
    @Test
    public void testProcessWithAggregate() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream("localhost", 12348);
        dataStream
                .map(Double::parseDouble)
                .timeWindowAll(Time.seconds(10))
                .aggregate(new AverageAggregate(), new ProcessAllWindowFunction<Double, String, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<Double> elements, Collector<String> out) throws Exception {
                        int count = 0;
                        Double last = 0d;
                        for (Double d: elements) {
                            count ++;
                            last = d;
                        }
                        // 这里的 count 一定为1，因为经过了 AverageAggregate 的聚合
                        out.collect("Window: " + context.window() + ", count: " + count + ", last: " + last);
                    }
                }).print();
        env.execute();
    }

}
