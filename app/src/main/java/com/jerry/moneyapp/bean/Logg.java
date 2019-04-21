package com.jerry.moneyapp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by wzl on 2018/8/25.
 *
 * @Description
 */
@Entity
public class Logg{
    private String deviceId;
    private String createTime;
    private String data;
    private int week;
    @Generated(hash = 392402273)
    public Logg(String deviceId, String createTime, String data, int week) {
        this.deviceId = deviceId;
        this.createTime = createTime;
        this.data = data;
        this.week = week;
    }
    @Generated(hash = 930501201)
    public Logg() {
    }
    public String getDeviceId() {
        return this.deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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
