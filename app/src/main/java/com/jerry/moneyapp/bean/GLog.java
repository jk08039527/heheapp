package com.jerry.moneyapp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by wzl on 2019/1/23.
 *
 * @Description
 */
@Entity
public class GLog {
    private String log;
    private String deviceId;
    private String data;
    private int week;
    @Generated(hash = 1051956353)
    public GLog(String log, String deviceId, String data, int week) {
        this.log = log;
        this.deviceId = deviceId;
        this.data = data;
        this.week = week;
    }
    @Generated(hash = 1289144734)
    public GLog() {
    }
    public String getLog() {
        return this.log;
    }
    public void setLog(String log) {
        this.log = log;
    }
    public String getDeviceId() {
        return this.deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getData() {
        return this.data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public int getWeek() {
        return this.week;
    }
    public void setWeek(int week) {
        this.week = week;
    }
}
