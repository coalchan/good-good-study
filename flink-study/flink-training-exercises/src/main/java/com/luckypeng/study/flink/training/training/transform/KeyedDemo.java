package com.luckypeng.study.flink.training.training.transform;

import com.luckypeng.study.flink.training.model.RichTaxiRide;
import com.luckypeng.study.flink.training.model.TaxiRide;
import com.luckypeng.study.flink.training.source.TaxiRideSource;
import com.luckypeng.study.flink.training.training.lab1.RideCleansing;
import com.luckypeng.study.flink.training.util.ExerciseBase;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.joda.time.Interval;
import org.joda.time.Minutes;

import static com.luckypeng.study.flink.training.util.ExerciseBase.rideSourceOrTest;

/**
 * Keyed Stream
 * @author chenzhipeng
 * @date 2019/2/25 13:59
 */
public class KeyedDemo {
    public static void main(String[] args) throws Exception {
        ParameterTool params = ParameterTool.fromArgs(args);
        final String input = params.get("input", ExerciseBase.pathToRideData);

        final int maxEventDelay = 60;       // events are out of order by max 60 seconds
        final int servingSpeedFactor = 600; // events of 10 minutes are served in 1 second

        // set up streaming execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(ExerciseBase.parallelism);

        // start the data generator
        DataStream<TaxiRide> rides = env.addSource(rideSourceOrTest(new TaxiRideSource(input, maxEventDelay, servingSpeedFactor)));

        rides.flatMap(new FlatMapFunction<TaxiRide, RichTaxiRide>() {
            @Override
            public void flatMap(TaxiRide ride, Collector<RichTaxiRide> out) throws Exception {
                FilterFunction<TaxiRide> valid = new RideCleansing.NYCFilter();
                if (valid.filter(ride)) {
                    out.collect(new RichTaxiRide(ride));
                }
            }
        }).flatMap(new FlatMapFunction<RichTaxiRide, Tuple2<Integer, Minutes>>() {
            @Override
            public void flatMap(RichTaxiRide ride, Collector<Tuple2<Integer, Minutes>> out) throws Exception {
                if (!ride.isStart) {
                    Interval rideInterval = new Interval(ride.startTime, ride.endTime);
                    Minutes duration = rideInterval.toDuration().toStandardMinutes();
                    out.collect(new Tuple2<>(ride.startCell, duration));
                }
            }
        }).keyBy(0).maxBy(1).print();

        env.execute();
    }
}
