package com.jerry.moneyapp.bean;

import java.util.LinkedList;

import cn.bmob.v3.BmobObject;

/**
 * Created by wzl on 2018/8/25.
 *
 * @Description
 */
public class MyLog extends BmobObject {
    private String createTime;
    private LinkedList<Integer> data;
    private int week;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final String createTime) {
        this.createTime = createTime;
    }

    public LinkedList<Integer> getData() {
        return data;
    }

    public void setData(LinkedList<Integer> data) {
        this.data = data;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(final int week) {
        this.week = week;
    }
}
