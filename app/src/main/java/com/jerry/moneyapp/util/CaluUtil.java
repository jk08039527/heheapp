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
            list.deleteCharAt(list.length() - 1);
            AnalyzeBean analyzeBean = mMap.get(list.toString());
            if (analyzeBean != null && analyzeBean.total > 0) {
                double rate = (double) analyzeBean.same / analyzeBean.total;
                if (rate > RMAX / 100 && analyzeBean.total > 50) {
                    point.intention = point.current;
                } else if (rate < RMIN / 100 && analyzeBean.total > 50) {
                    point.intention = point.current == GBData.VALUE_FENG ? GBData.VALUE_LONG : GBData.VALUE_FENG;
                }
            }
        }
        return point;
    }

    public static HashMap<String, AnalyzeBean> mMap = new HashMap<>();

    public static void analyze(int[] ints) {
        if (Param.START < 1 || Param.START > ints.length) {
            return;
        }
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < Param.START; i++) {
            list.append(ints[i]).append(",");
        }
        list.deleteCharAt(list.length() - 1);
        int last = ints[Param.START - 1];

        for (int i = Param.START + 1; i < ints.length; i++) {
            String string = list.toString();
            if (string.contains("0")) {
                StringBuilder sb = new StringBuilder();
                String key = string.replace("0", "1");
                AnalyzeBean analyzeBean = mMap.get(key);
                if (analyzeBean == null) {
                    analyzeBean = new AnalyzeBean();
                }
                if (last == ints[i]) {
                    analyzeBean.same++;
                }
                analyzeBean.total++;
                mMap.put(key, analyzeBean);

                sb.delete(0, sb.length());
                key = string.replace("0", "2");
                analyzeBean = mMap.get(key);
                if (analyzeBean == null) {
                    analyzeBean = new AnalyzeBean();
                }
                if (last == ints[i]) {
                    analyzeBean.same++;
                }
                analyzeBean.total++;
                mMap.put(key, analyzeBean);
            } else {
                AnalyzeBean analyzeBean = mMap.get(list.toString());
                if (analyzeBean == null) {
                    analyzeBean = new AnalyzeBean();
                }
                if (last == ints[i]) {
                    analyzeBean.same++;
                }
                analyzeBean.total++;
                mMap.put(string, analyzeBean);
            }
            last = ints[i];
            list.delete(0, 2).append(",").append(ints[i]);
        }
    }

    public static class AnalyzeBean {

        int same;
        int total;

    }
}
