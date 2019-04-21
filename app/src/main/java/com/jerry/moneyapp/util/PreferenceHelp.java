package com.jerry.moneyapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.jerry.moneyapp.BuildConfig;
import com.jerry.moneyapp.MyApplication;

/**
 * SharedPreference操作类
 */
public class PreferenceHelp {

    /**
     * 4G下播放开关：0：关，1：开
     */
    public static final String FIRST_INSTALL = "FIRST_INSTALL";
    /**
     * 首次安装设置通知
     */
    public static final String FIRST_SET_NOTIFICATION = "SET_NOTIFICATION";

    private PreferenceHelp() {
    }

    private static SharedPreferences sp = MyApplication.getInstances().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

    public static String getString(String strKey) {
        return sp.getString(strKey, "");
    }

    public static String getString(String strKey, String strDefault) {
        return sp.getString(strKey, strDefault);
    }

    public static void putString(String strKey, String strData) {
        sp.edit().putString(strKey, strData).apply();
    }

    public static int getInt(String strKey) {
        return sp.getInt(strKey, 0);
    }

    public static int getInt(String strKey, int strDefault) {
        return sp.getInt(strKey, strDefault);
    }

    public static void putInt(String strKey, int strData) {
        sp.edit().putInt(strKey, strData).apply();
    }

    public static boolean getBoolean(String strKey) {
        return sp.getBoolean(strKey, false);
    }

    public static boolean getBoolean(String strKey, boolean bDefault) {
        return sp.getBoolean(strKey, bDefault);
    }

    public static void putBoolean(String strKey, boolean bValue) {
        sp.edit().putBoolean(strKey, bValue).apply();
    }
}