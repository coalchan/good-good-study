package com.luckypeng.study.flink.training.transform;

import com.luckypeng.study.flink.model.RichTaxiRide;
import com.luckypeng.study.flink.model.TaxiRide;
import com.luckypeng.study.flink.source.TaxiRideSource;
import com.luckypeng.study.flink.util.ExerciseBase;
import com.luckypeng.study.flink.util.GeoUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static com.luckypeng.study.flink.util.ExerciseBase.rideSourceOrTest;

/**
 * map 转换
 * @author chenzhipeng
 * @date 2019/2/25 10:52
 */
public class MapDemo {
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

        DataStream<RichTaxiRide> richNYCRides = rides
                .filter(ride -> GeoUtils.isInNYC(ride.startLon, ride.startLat)
                        && GeoUtils.isInNYC(ride.endLon, ride.endLat))
                .map(RichTaxiRide::new);

        richNYCRides.print();

        env.execute();
    }
}
