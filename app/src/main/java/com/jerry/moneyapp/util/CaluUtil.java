package com.jerry.moneyapp.util;

import java.util.ArrayList;
import java.util.LinkedList;

import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.Point;

/**
 * Created by wzl on 2018/10/1.
 *
 * @Description
 */
public class CaluUtil {

    private static final int GUDAOCOUNT2 = 12;
    private static final int GUDAOCOUNT3 = 4;
    private static final int GUDAOLINIT2 = 2;
    private static final int GUDAOLINIT3 = 3;
    /**
     * @param ints 原始数据
     * @return 第一个参数表示投什么，第二个参数表示投多少
     */
    public static Point calulate(int[] ints, int position, LinkedList<Point> points) {
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
            boolean good = false;
            if (paint.size() > 1 && paint.get(0) > 1 && paint.get(1) > 1 && paint.get(0) + paint.get(1) > 5) {
                point.multiple2 = 2;
                point.multiple3 = 2;
                good = true;
            } else if (paint.size() > 2 && paint.get(0) > 1 && paint.get(1) > 1 && paint.get(2) > 1 && paint.get(0) + paint.get(1) +
                    paint.get(2) > 6) {
                point.multiple2 = 2;
                point.multiple3 = 2;
                good = true;
            } else if (paint.size() > 2 && paint.get(0) == 1 && paint.get(1) == 1 && paint.get(2) == 1) {
                point.multiple2 = -1;
                point.multiple3 = -1;
            }
            if (!good) {
                int paintSize = paint.size();
                if (paintSize > 1 && paint.get(0) == 1) {
                    if (paintSize > 2 && paint.get(1) > 1) {
                        point.multiple2 = 2;
                    } else if (paintSize == 2) {
                        point.multiple2 = 2;
                    }
                }
                if (paintSize > 2 && paint.get(0) == 1 && paint.get(1) == 1) {
                    if (paintSize > 3 && paint.get(2) > 1) {
                        point.multiple3 = 2;
                    } else if (paintSize == 3) {
                        point.multiple3 = 2;
                    }
                }
            }
            // 记录当前数到第几个
            int gd = 0;
            // 记录当前索引
            int gdIndex = 0;
            int min = Math.min(GUDAOCOUNT2, position);
            while (gd < min && gdIndex < paint.size() - 1) {
                if (paint.get(gdIndex) == 1) {
                    point.gudao2++;
                }
                gd += paint.get(gdIndex);
                gdIndex++;
            }
            // 记录当前数到第几个
            gd = 0;
            // 记录当前索引
            gdIndex = 0;
            while (gdIndex < paint.size() - 1) {
                gd += paint.get(gdIndex);
                gdIndex++;
            }
            if (point.multiple2 > 0) {
                if (point.gudao2 >= GUDAOLINIT2) {
                    point.intention2 = GBData.VALUE_NONE;
                } else {
                    point.intention2 = ints[position - 1];
                }
            } else {
                point.intention2 = ints[position - 2];
                point.multiple2 = -point.multiple2;
            }
            // 记录当前数到第几个
            gd = 0;
            // 记录当前索引
            gdIndex = 0;
            min = Math.min(GUDAOCOUNT3, position);
            while (gd < min && gdIndex < paint.size() - 1) {
                if (paint.get(gdIndex) == 1) {
                    point.gudao3++;
                }
                gd += paint.get(gdIndex);
                gdIndex++;
            }
            if (point.multiple3 > 0) {
                if (point.gudao3 >= GUDAOLINIT3) {
                    point.intention3 = GBData.VALUE_NONE;
                } else {
                    point.intention3 = ints[position - 1];
                }
            } else {
                point.intention3 = ints[position - 2];
                point.multiple3 = -point.multiple3;
            }
        } else {
            point.intention2 = GBData.VALUE_NONE;
            point.intention3 = GBData.VALUE_NONE;
        }
        return point;
    }

    public static double calu2(int[] ints) {
        Point lastP = null;
        LinkedList<Point> ps = new LinkedList<>();
        for (int j = 0; j < ints.length; j++) {
            Point point = CaluUtil.calulate(ints, j + 1, ps);
            point.current = ints[j];
            if (lastP != null) {
                if (lastP.intention2 != GBData.VALUE_NONE) {
                    if (lastP.intention2 == point.current) {
                        point.win2 = lastP.win2 + 9.7 * Math.abs(lastP.multiple2);
                    } else {
                        point.win2 = lastP.win2 - 10 * Math.abs(lastP.multiple2);
                    }
                } else {
                    point.win2 = lastP.win2;
                }
            }
            lastP = point;
            ps.add(point);
        }
        if (lastP == null) {
            return 0;
        }
        return lastP.win2;
    }

    public static double calu3(int[] ints) {
        Point lastP = null;
        LinkedList<Point> ps = new LinkedList<>();
        for (int j = 0; j < ints.length; j++) {
            Point point = CaluUtil.calulate(ints, j + 1, ps);
            point.current = ints[j];
            if (lastP != null) {
                if (lastP.intention3 != GBData.VALUE_NONE) {
                    if (lastP.intention3 == point.current) {
                        point.win3 = lastP.win3 + 9.7 * Math.abs(lastP.multiple3);
                    } else {
                        point.win3 = lastP.win3 - 10 * Math.abs(lastP.multiple3);
                    }
                } else {
                    point.win3 = lastP.win3;
                }
            }
            lastP = point;
            ps.add(point);
        }
        if (lastP == null) {
            return 0;
        }
        return lastP.win3;
    }
}
