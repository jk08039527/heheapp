package com.jerry.moneyapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.tencent.tinker.entry.DefaultApplicationLike;

import cn.bmob.v3.Bmob;

/**
 * Created by wzl on 2018/8/24.
 *
 * @Description
 */
public class MyApplication extends DefaultApplicationLike {

    @SuppressLint("StaticFieldLeak")
    private static Application mInstance;

    public MyApplication(final Application application, final int tinkerFlags, final boolean tinkerLoadVerifyFlag, final long
            applicationStartElapsedTime, final long applicationStartMillisTime, final Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    public static Application getInstances() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = getApplication();
        BuglyStrategy strategy = new BuglyStrategy();
        // 设置app渠道号.
        strategy.setAppChannel("main");
        Bugly.init(mInstance, BuildConfig.BUGLY_APP_ID, false, strategy);
        Bmob.initialize(mInstance, BuildConfig.BMOB_APPID);
    }
}
