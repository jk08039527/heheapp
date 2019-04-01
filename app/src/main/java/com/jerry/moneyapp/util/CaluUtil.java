package com.jerry.moneyapp.util;

import java.util.HashMap;

import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.Param;
import com.jerry.moneyapp.bean.Point;

import static com.jerry.moneyapp.bean.Param.RMAX;
import static com.jerry.moneyapp.bean.Param.RMIN;

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
        if (Param.START > 0 && position > Param.START) {
            StringBuilder list = new StringBuilder();
            for (int i = position - Param.START; i < position; i++) {
                list.append(ints[i]).append(",");
            }
            list.append(position % 6);
            AnalyzeBean analyzeBean = mMap.get(list.toString());
            if (analyzeBean != null && analyzeBean.total > 0) {
                double rate = (double) analyzeBean.same / analyzeBean.total;
                if (rate > RMAX / 100) {
                    point.intention = point.current;
                } else if (rate < RMIN / 100) {
                    point.intention = point.current == GBData.VALUE_FENG ? GBData.VALUE_LONG : GBData.VALUE_FENG;
                }
            }
        }
        return point;
    }

    public static HashMap<String, AnalyzeBean> mMap = new HashMap<>();

    public static void analyze(int[] ints) {
        if (Param.START < 1) {
            return;
        }
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < Param.START; i++) {
            list.append(ints[i]).append(",");
        }
        list.append(Param.START % 6);
        int last = ints[Param.START - 1];

        for (int i = Param.START + 1; i < ints.length; i++) {
            AnalyzeBean analyzeBean = mMap.get(list.toString());
            if (analyzeBean == null) {
                analyzeBean = new AnalyzeBean();
            }
            if (last == ints[i]) {
                analyzeBean.same++;
            }
            analyzeBean.total++;
            mMap.put(list.toString(), analyzeBean);
            last = ints[i];
            list.delete(list.length() - 1, list.length()).delete(0, 2)
                .append(ints[i]).append(",").append((i + 1) % 6);
        }
    }

    public static class AnalyzeBean {

        int same;
        int total;

    }
}
