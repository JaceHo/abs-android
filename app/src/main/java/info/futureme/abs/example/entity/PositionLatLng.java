/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android app
 * Create Time: 16-2-16 下午6:38
 */

package info.futureme.abs.example.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/2.
 */
public class PositionLatLng implements Serializable {
    private double latitude;
    private double longitude;

    public PositionLatLng() {
        super();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "OrderCondition{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
