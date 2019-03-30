package com.jerry.moneyapp.util;

import com.jerry.moneyapp.bean.Param;
import com.jerry.moneyapp.bean.Point;

/**
 * Created by wzl on 2018/10/1.
 *
 * @Description
 */
public class CaluUtil {

    /**
     * @param ints 原始数据
     */
    public static Point calulate(int[] ints, int position) {
        Point point = new Point();
        point.current = ints[position - 1];
        int hengCurrent = 0;
        if (position > Param.START) {
            int zongCount = 1;
            int hengCount = 1;
            int temp = position - 2;
            while (temp > 0) {
                if (point.current != ints[temp]) {
                    break;
                }
                temp--;
                zongCount++;
            }
            if (position > 6) {
                hengCurrent = ints[position - 6];
                temp = position - 12;
                while (temp > 0) {
                    if (hengCurrent != ints[temp]) {
                        break;
                    }
                    temp = temp - 6;
                    hengCount++;
                }
            }
            if (hengCurrent == point.current) {
                if (zongCount + hengCount < 6) {
                    point.intention = point.current;
                }
            } else {
                if (zongCount > 1 && zongCount < 5 && hengCount == 1) {
                    point.intention = point.current;
                } else if (hengCount > 3 && hengCount < 5 && zongCount == 1) {
                    point.intention = hengCurrent;
                }
            }
        }
        return point;
    }

    public static void analyze(int[] ints) {

    }
}
