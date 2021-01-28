package com.luckypeng.study.flink.streaming.state;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import org.junit.Test;

/**
 * Managed Keyed State
 * @author coalchan
 * @date 2019/3/27 11:10
 */
public class KeyedValueStateTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Tuple2<Integer, Integer>> dataStream =
                env.fromElements(Tuple2.of(1, 3), Tuple2.of(1, 5), Tuple2.of(1, 7), Tuple2.of(1, 4), Tuple2.of(1, 2),
                        Tuple2.of(2, 2), Tuple2.of(2, 4));

        dataStream
                .keyBy(0)
                .flatMap(new CountWindowAverage())
                .print();

        env.execute();

    }

    static class CountWindowAverage extends RichFlatMapFunction<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> {
        private transient ValueState<Tuple2<Integer, Integer>> sumState;

        @Override
        public void open(Configuration parameters) throws Exception {
            ValueStateDescriptor<Tuple2<Integer, Integer>> descriptor =
                    new ValueStateDescriptor<>("avg", new TypeHint<Tuple2<Integer, Integer>>() {}.getTypeInfo());
            sumState = getRuntimeContext().getState(descriptor);
        }

        @Override
        public void flatMap(Tuple2<Integer, Integer> value, Collector<Tuple2<Integer, Integer>> out) throws Exception {
            // access the state value
            Tuple2<Integer, Integer> currentSum = sumState.value();

            if (currentSum == null) {
                currentSum = Tuple2.of(0, 0);
            }

            // update the count
            currentSum.f0 += 1;

            // add the second field of the input value
            currentSum.f1 += value.f1;

            // update the state
            sumState.update(currentSum);

            // if the count reaches 2, emit the average and clear the state
            if (currentSum.f0 >= 2) {
                sumState.clear();
                out.collect(new Tuple2<>(value.f0, currentSum.f1 / currentSum.f0));
            }
        }
    }

    /**
     * keyBy 后状态是独立的，不同 key 之间不会共享
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Tuple2<Integer, Integer>> dataStream =
                env.fromElements(Tuple2.of(1, 3), Tuple2.of(1, 5), Tuple2.of(1, 7), Tuple2.of(1, 4), Tuple2.of(1, 2),
                        Tuple2.of(2, 2), Tuple2.of(2, 4));

        dataStream
                .keyBy(0)
                .countWindow(2)
                .process(new MyProcessWindowFunction())
                .print();

        env.execute();
    }

    static class MyProcessWindowFunction extends ProcessWindowFunction<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>, Tuple, GlobalWindow> {
        private transient ValueState<Tuple2<Integer, Integer>> sumState;
        private ValueStateDescriptor<Tuple2<Integer, Integer>> descriptor =
                new ValueStateDescriptor<>("avg", new TypeHint<Tuple2<Integer, Integer>>() {}.getTypeInfo());


        @Override
        public void open(Configuration parameters) throws Exception {
            sumState = getRuntimeContext().getState(descriptor);
        }

        @Override
        public void process(Tuple tuple, Context context, Iterable<Tuple2<Integer, Integer>> elements, Collector<Tuple2<Integer, Integer>> out) throws Exception {
            // access the state value
            Tuple2<Integer, Integer> currentSum = sumState.value();

            if (currentSum == null) {
                currentSum = Tuple2.of(0, 0);
            }

            Tuple2<Integer, Integer> v = null;

            // add the second field of the input value
            for (Tuple2<Integer, Integer> value : elements) {
                // update the count
                currentSum.f0 += 1;
                currentSum.f1 += value.f1;
                v = value;
            }

            // update the state
            sumState.update(currentSum);

            out.collect(new Tuple2<>(v.f0, currentSum.f1 / currentSum.f0));
        }
    }
}
