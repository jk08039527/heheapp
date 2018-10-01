package com.jerry.moneyapp;

import java.util.ArrayList;

/**
 * Created by wzl on 2018/10/1.
 *
 * @Description
 */
public class CaluUtil {

    /**
     * @param ints 原始数据
     * @return 第一个参数表示投什么，第二个参数表示投多少
     */
    public static Point calulate(int[] ints, int position) {
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
            if (point.multiple2 > 0) {
                // 记录当前数到第几个
                int gd2 = 0;
                // 记录当前索引
                int gdIndex = 0;
                int min = Math.min(6, position);
                while (gd2 < min && gdIndex < paint.size()) {
                    if (paint.get(gdIndex) == 1) {
                        point.gudao2++;
                    }
                    gd2 += paint.get(gdIndex);
                    gdIndex++;
                }
                if (point.gudao2 > 1) {
                    point.type2 = GBData.VALUE_NONE;
                } else {
                    point.type2 = ints[position - 1];
                }
            } else {
                point.type2 = ints[position - 2];
            }

            if (point.multiple3 > 0) {
                // 记录当前数到第几个
                int gd3 = 0;
                // 记录当前记录的孤岛数
                // 记录当前索引
                int gdIndex = 0;
                int min = Math.min(8, position);
                while (gd3 < min && gdIndex < paint.size()) {
                    if (paint.get(gdIndex) == 1) {
                        point.gudao3++;
                    }
                    gd3 += paint.get(gdIndex);
                    gdIndex++;
                }
                if (point.gudao3 > 2) {
                    point.type3 = GBData.VALUE_NONE;
                } else {
                    point.type3 = ints[position - 1];
                }
            } else {
                point.type3 = ints[position - 2];
            }
        } else {
            point.type2 = GBData.VALUE_LONG;
            point.type3 = GBData.VALUE_LONG;
        }
        return point;
    }
}