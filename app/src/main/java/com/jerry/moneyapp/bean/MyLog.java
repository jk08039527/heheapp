package com.jerry.moneyapp.bean;

import java.util.LinkedList;

import cn.bmob.v3.BmobObject;

/**
 * Created by wzl on 2018/8/25.
 *
 * @Description
 */
public class MyLog extends BmobObject {
    private String log;
    private String deviceId;
    private LinkedList<Integer> data;
    private LinkedList<Point> points;

    public String getLog() {
        return log;
    }

    public void setLog(final String log) {
        this.log = log;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public LinkedList<Integer> getData() {
        return data;
    }

    public void setData(LinkedList<Integer> data) {
        this.data = data;
    }

    public LinkedList<Point> getPoints() {
        return points;
    }

    public void setPoints(final LinkedList<Point> points) {
        this.points = points;
    }
}
