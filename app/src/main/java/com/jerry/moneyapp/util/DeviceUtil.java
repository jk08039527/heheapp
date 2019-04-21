package com.jerry.moneyapp.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.jerry.moneyapp.MyApplication;

/**
 * Created by th on 16/5/17. 类说明:
 */
public class DeviceUtil {

    private static DecimalFormat df = new DecimalFormat("#.0");
    private static final SimpleDateFormat FORMAT_DATE_TIME = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA);

    private DeviceUtil() {
    }

    /**
     * 获取泛型类的type
     *
     * @param raw  泛型类的class, 如BaseResponse4Object.class
     * @param args 泛型实参的class, LotteryBean.class
     * @return 泛型类的type
     */
    public static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return args;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }

            @Override
            public Type getRawType() {
                return raw;
            }
        };
    }

    /**
     * 获取手机屏幕的像素宽
     */
    public static int getDisplayWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
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


    private static boolean isChinese(String string) {
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

    private static String changeToUrlEncode(String str) {
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

    /**
     * 获取屏幕密度
     */
    private static float getDisplayDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(double dipValue) {
        return (int) (dipValue * getDisplayDensity() + 0.5f);
    }

    public static synchronized String getCurrentTime() {
        return FORMAT_DATE_TIME.format(Calendar.getInstance().getTime());
    }
}
