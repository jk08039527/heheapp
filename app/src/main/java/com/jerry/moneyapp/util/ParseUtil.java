package com.jerry.moneyapp.util;

import android.text.TextUtils;

/**
 * 基本数据类型解析工具类
 */
public class ParseUtil {

    private ParseUtil() {
    }

    /**
     * 解析以字符串表示的整数类型
     */
    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    /**
     * 解析以字符串表示的整数类型，如果发生异常则返回默认值
     */
    public static int parseInt(String s, int defaultInt) {
        if (TextUtils.isEmpty(s)) {
            return defaultInt;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultInt;
        }

    }

    /**
     * 解析以字符串表示的双精度浮点类型
     */
    public static double parseDouble(String s) {
        return parseDouble(s, 0.0);
    }

    /**
     * 解析以字符串表示的双精度浮点类型，如果发生异常则返回默认值
     */
    public static double parseDouble(String s, double defaultDouble) {
        if (TextUtils.isEmpty(s)) {
            return defaultDouble;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }
        return defaultDouble;
    }
}
