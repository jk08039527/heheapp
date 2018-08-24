package com.jerry.moneyapp;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;

import android.app.Application;

/**
 * Created by wzl on 2018/8/24.
 *
 * @Description
 */
public class MyApplication extends Application {

    private static final String BUGLY_CRASHREPORT = "c8d375f68d";

    @Override
    public void onCreate() {
        super.onCreate();
        BuglyStrategy strategy = new BuglyStrategy();
        // 设置app渠道号.
        strategy.setAppChannel("main");
        Bugly.init(this, BUGLY_CRASHREPORT, false, strategy);
    }
}
