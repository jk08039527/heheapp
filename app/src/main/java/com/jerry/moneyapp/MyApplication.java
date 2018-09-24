package com.jerry.moneyapp;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import cn.bmob.v3.Bmob;

/**
 * Created by wzl on 2018/8/24.
 *
 * @Description
 */
public class MyApplication extends Application {

    private static final String BUGLY_CRASHREPORT = "7eb0529af0";
    private static final String BMOB_APPID = "7cdb7db7a6d99713798d4b9755d3c0f5";
    @SuppressLint("StaticFieldLeak")
    private static Context sInstances;

    public static Context getInstances() {
        return sInstances;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstances = this;
        BuglyStrategy strategy = new BuglyStrategy();
        // 设置app渠道号.
        strategy.setAppChannel("main");
        Bugly.init(this, BUGLY_CRASHREPORT, false, strategy);
        Bmob.initialize(this, BMOB_APPID);
    }
}
