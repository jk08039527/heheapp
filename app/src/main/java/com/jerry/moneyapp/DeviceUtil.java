package com.jerry.moneyapp;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by th on 16/5/17. 类说明:
 */
public class DeviceUtil {

    private static DecimalFormat df = new DecimalFormat("#.0");

    private DeviceUtil() {
    }

    /**
     * 获取设备ID,如果ID为空,再取Mac地址,都为空最后随机生成
     */
    @SuppressLint("hardwareIds")
    public static String getDeviceId() {
        Context context = MyApplication.getInstances().getApplicationContext();
        String deviceId = SharedPreferencesHelper.getString(SharedPreferencesHelper.getSettingSp(), "device_id", "");
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (tm == null) {
                deviceId = "";
            } else {
                deviceId = tm.getDeviceId();
            }
            deviceId = deviceId == null ? "" : deviceId;
        } catch (SecurityException e) {
            e.printStackTrace();
            deviceId = "";
        }

        //状态码权限被关闭:获得的DeviceId全部字符相同或者为空则判定为权限被关闭，使用UUID作为唯一标识
        if (TextUtils.isEmpty(deviceId) || Pattern.matches("(.)(\\1)*", deviceId)) {
            deviceId = UUID.randomUUID().toString();
        }

        if (isChinese(deviceId)) {
            deviceId = changeToUrlEncode(deviceId);
        }
        SharedPreferencesHelper.setPreference(SharedPreferencesHelper.getSettingSp(), "device_id", deviceId);
        return deviceId;
    }
    private static Pattern chinesePattern = Pattern.compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$");


    public static boolean isChinese(String string) {
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        for (int i = 0; i < string.length(); i++) {
            Matcher m = chinesePattern.matcher(string.charAt(i) + "");
            if (m.matches()) {
                return true;
            }
        }
        return false;
    }

    public static String changeToUrlEncode(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (Exception ex) {
            return str;
        }
    }

    public static String m2(double d) {
        return df.format(d);
    }

    public static String m2p(double doub) {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return nf.format(doub);
    }

}
