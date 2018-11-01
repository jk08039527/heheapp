package com.jerry.moneyapp.util;

import com.jerry.moneyapp.MyApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by wzl on 2018/5/28.
 *
 * @Description SharedPreferences 帮助类
 */
public class SharedPreferencesHelper {

    private static final String SP_NAME_SETTING = "sp_name_setting";//配置相关的SP

    /**
     * 配置相关的Sp
     * 相关的key见
     */
    public static SharedPreferences getSettingSp() {
        return MyApplication.getInstances().getSharedPreferences(SP_NAME_SETTING, Context.MODE_PRIVATE);
    }

    /**
     * 用户相关Sp
     * 相关的key见
     */
    public static SharedPreferences getDefaultSp() {
        return PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstances());
    }

    /**
     * 具有默认值
     *
     * @param defValue 默认值
     */
    public static String getString(SharedPreferences sp, String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public static void setPreference(SharedPreferences sp, String key, String value) {
        sp.edit().putString(key, value).apply();
    }
}
