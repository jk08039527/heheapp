package com.jerry.moneyapp;

import cn.bmob.v3.BmobObject;

/**
 * Created by wzl on 2018/8/25.
 *
 * @Description
 */
public class MyLog extends BmobObject {
    private String log;
    private String deviceId;

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
}
