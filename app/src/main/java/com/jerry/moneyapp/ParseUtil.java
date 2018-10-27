package com.jerry.moneyapp;

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
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
            return defaultInt;
        }

    }
}
