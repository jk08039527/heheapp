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
                int tempp = ints[position - 6];
                temp = position - 12;
                while (temp > 0) {
                    if (tempp != ints[temp]) {
                        break;
                    }
                    temp = temp - 6;
                    hengCount++;
                }
            }
            double v = Param.K1 * zongCount + Param.K2 * hengCount + Param.K3;
            if (position > 6) {
                if (v > 0) {
                    point.intention = ints[position - 1];
                } else if (v < 0) {
                    point.intention = ints[position - 6];
                }
            }
        }
        return point;
    }
}
