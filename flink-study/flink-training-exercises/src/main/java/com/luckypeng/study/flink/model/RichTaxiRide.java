package com.luckypeng.study.flink.model;

import com.luckypeng.study.flink.util.GeoUtils;

/**
 * @author chenzhipeng
 * @date 2019/2/25 10:31
 */
public class RichTaxiRide extends TaxiRide {
    public int startCell;
    public int endCell;

    public RichTaxiRide() {}

    public RichTaxiRide(TaxiRide ride) {
        this.rideId = ride.rideId;
        this.isStart = ride.isStart;
        this.startTime = ride.startTime;
        this.endTime = ride.endTime;
        this.startLon = ride.startLon;
        this.startLat = ride.startLat;
        this.endLon = ride.endLon;
        this.endLat = ride.endLat;
        this.passengerCnt = ride.passengerCnt;
        this.taxiId = ride.taxiId;
        this.driverId = ride.driverId;

        this.startCell = GeoUtils.mapToGridCell(ride.startLon, ride.startLat);
        this.endCell = GeoUtils.mapToGridCell(ride.endLon, ride.endLat);
    }

    @Override
    public String toString() {
        return super.toString() + ","  + this.startCell + "," + this.endCell;
    }
}
