package com.jerry.moneyapp;

import java.util.ArrayList;

/**
 * Created by wzl on 2018/10/1.
 *
 * @Description
 */
class CaluUtil {

    /**
     * @param ints 原始数据
     * @return 第一个参数表示投什么，第二个参数表示投多少
     */
    static Point calulate(int[] ints, int position) {
        Point point = new Point();
        if (position > ints.length) {
            return point;
        }
        if (position > 0) {
            // 判断是否加倍
            ArrayList<Integer> paint = new ArrayList<>();
            int index = position - 1;
            int tempSize = 1;
            while (index >= 0) {
                if (index == 0) {
                    paint.add(tempSize);
                } else {
                    if (ints[index] == ints[index - 1]) {
                        tempSize++;
                    } else {
                        paint.add(tempSize);
                        tempSize = 1;
                    }
                }
                index--;
            }

            if (paint.size() > 1 && paint.get(0) == 1 && paint.get(1) > 1) {
                point.multiple2 = 2;
            }
            if (paint.size() > 2 && paint.get(0) == 1 && paint.get(1) == 1 && paint.get(2) > 1) {
                point.multiple3 = 2;
            }
            if (paint.size() > 1 && paint.get(1) > 1 && paint.get(0) + paint.get(1) > 5) {
                point.multiple2 = 2;
                point.multiple3 = 2;
            } else if (paint.size() > 2 && paint.get(0) > 1 && paint.get(1) > 1 && paint.get(2) > 1 && paint.get(0) + paint.get(1) +
                    paint.get(2) > 6) {
                point.multiple2 = 2;
                point.multiple3 = 2;
            } else if (paint.size() > 2 && paint.get(0) == 1 && paint.get(1) == 1 && paint.get(2) == 1) {
                point.multiple2 = -1;
                point.multiple3 = -1;
            }
            // 记录当前数到第几个
            int gd = 0;
            // 记录当前索引
            int gdIndex = 0;
            int min = Math.min(6, position);
            while (gd < min && gdIndex < paint.size()) {
                if (paint.get(gdIndex) == 1) {
                    point.gudao2++;
                }
                gd += paint.get(gdIndex);
                gdIndex++;
            }
            if (point.multiple2 > 0) {
                if (point.gudao2 > 1) {
                    point.intention2 = GBData.VALUE_NONE;
                } else {
                    point.intention2 = ints[position - 1];
                }
            } else {
                point.intention2 = ints[position - 2];
            }
            // 记录当前数到第几个
            gd = 0;
            // 记录当前索引
            gdIndex = 0;
            min = Math.min(8, position);
            while (gd < min && gdIndex < paint.size()) {
                if (paint.get(gdIndex) == 1) {
                    point.gudao3++;
                }
                gd += paint.get(gdIndex);
                gdIndex++;
            }
            if (point.multiple3 > 0) {
                if (point.gudao3 > 2) {
                    point.intention3 = GBData.VALUE_NONE;
                } else {
                    point.intention3 = ints[position - 1];
                }
            } else {
                point.intention3 = ints[position - 2];
            }
        } else {
            point.intention2 = GBData.VALUE_LONG;
            point.intention3 = GBData.VALUE_LONG;
        }
        return point;
    }
}
