package com.luckypeng.study.flink.training.exercises.transform;

import com.luckypeng.study.flink.training.model.RichTaxiRide;
import com.luckypeng.study.flink.training.model.TaxiRide;
import com.luckypeng.study.flink.training.source.TaxiRideSource;
import com.luckypeng.study.flink.training.exercises.lab1.RideCleansing;
import com.luckypeng.study.flink.training.util.ExerciseBase;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static com.luckypeng.study.flink.training.util.ExerciseBase.rideSourceOrTest;

/**
 * FlatMap 转换
 * @author coalchan
 * @date 2019/2/25 11:08
 */
public class FlatMapDemo {
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

        DataStream<RichTaxiRide> richNYCRides = rides.flatMap((ride, out) -> {
            FilterFunction<TaxiRide> valid = new RideCleansing.NYCFilter();
            if (valid.filter(ride)) {
                out.collect(new RichTaxiRide(ride));
            }
        }).returns((TypeInformation) Types.GENERIC(RichTaxiRide.class));


        richNYCRides.print();

        env.execute();
    }
}
