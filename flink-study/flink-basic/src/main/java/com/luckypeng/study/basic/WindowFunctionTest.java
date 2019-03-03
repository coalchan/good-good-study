package com.luckypeng.study.basic;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Test;

public class WindowFunctionTest {
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
}
