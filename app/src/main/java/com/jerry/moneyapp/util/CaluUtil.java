package com.jerry.moneyapp.util;

import com.alibaba.fastjson.JSON;
import com.jerry.moneyapp.bean.BaseDao;
import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.Logg;
import com.jerry.moneyapp.bean.Param;
import com.jerry.moneyapp.bean.Point;
import com.jerry.moneyapp.greendao.gen.LoggDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.jerry.moneyapp.bean.Param.RMAX;
import static com.jerry.moneyapp.bean.Param.RMIN;

/**
 * Created by wzl on 2018/10/1.
 *
 * @Description
 */
public class CaluUtil {


    public static void initMap() {
        List<Logg> loggs = BaseDao.getTjDb().queryAll(Logg.class, LoggDao.Properties.CreateTime);
        for (Logg log : loggs) {
            LinkedList<Integer> integers = JSON.parseObject(log.getData(), DeviceUtil.type(LinkedList.class, Integer.class));
            LinkedList<Integer> paint = new LinkedList<>();
            int[] ints = new int[integers.size()];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = integers.get(i);
            }
            CaluUtil.analyze(ints);
        }
    }

    public static void addMap(int[] ints) {
        CaluUtil.analyze(ints);
    }

    public static Point calulate(int[] ints, int position) {
        Point point = new Point();
        point.current = ints[position - 1];
        if (Param.START > 0 && position > Param.START) {
            StringBuilder list = new StringBuilder();
            for (int i = position - Param.START; i < position; i++) {
                list.append(ints[i]).append(",");
            }
            list.deleteCharAt(list.length() - 1);
            AnalyzeBean analyzeBean = posMap.get(list.toString());
            if (analyzeBean != null && analyzeBean.total > 0) {
                double rate = (double) analyzeBean.same / analyzeBean.total;
                if (rate > RMAX / 100 && analyzeBean.total > 50) {
                    point.intention = point.current;
                }
            }
            analyzeBean = negMap.get(list.toString());
            if (analyzeBean != null && analyzeBean.total > 0) {
                double rate = (double) analyzeBean.same / analyzeBean.total;
                if (rate > RMIN / 100 && analyzeBean.total > 50) {
                    point.intention = point.current == GBData.VALUE_FENG ? GBData.VALUE_LONG : GBData.VALUE_FENG;
                }
            }
        }
        return point;
    }

    public static ArrayList<HashMap<String, AnalyzeBean>> mapArrayList = new ArrayList<>();
    public static HashMap<String, AnalyzeBean> mMap = new HashMap<>();
    public static HashMap<String, AnalyzeBean> posMap = new HashMap<>();
    public static HashMap<String, AnalyzeBean> negMap = new HashMap<>();

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
        HashMap<String, AnalyzeBean> tempMap = new HashMap<>();

        for (int i = Param.START + 1; i < ints.length; i++) {
            String string = list.toString();
            if (string.contains("0")) {
                StringBuilder sb = new StringBuilder();
                String key = string.replace("0", "1");
                AnalyzeBean analyzeBean = tempMap.get(key);
                if (analyzeBean == null) {
                    analyzeBean = new AnalyzeBean();
                }
                if (last == ints[i]) {
                    analyzeBean.same++;
                }
                analyzeBean.total++;
                tempMap.put(key, analyzeBean);

                sb.delete(0, sb.length());
                key = string.replace("0", "2");
                analyzeBean = tempMap.get(key);
                if (analyzeBean == null) {
                    analyzeBean = new AnalyzeBean();
                }
                if (last == ints[i]) {
                    analyzeBean.same++;
                }
                analyzeBean.total++;
                tempMap.put(key, analyzeBean);
            } else {
                AnalyzeBean analyzeBean = tempMap.get(list.toString());
                if (analyzeBean == null) {
                    analyzeBean = new AnalyzeBean();
                }
                if (last == ints[i]) {
                    analyzeBean.same++;
                }
                analyzeBean.total++;
                tempMap.put(string, analyzeBean);
            }
            last = ints[i];
            list.delete(0, 2).append(",").append(ints[i]);
        }
        mapArrayList.add(tempMap);
        cMap();
    }

    private static void cMap() {
        int count = 500;
        if (mapArrayList.size() < count) {
            return;
        }
        if (mapArrayList.size() == count) {
            for (int i = 0; i < count; i++) {
                HashMap<String, AnalyzeBean> subMap = mapArrayList.get(i);
                for (String s : subMap.keySet()) {
                    AnalyzeBean analyzeBean = mMap.get(s);
                    AnalyzeBean tempAnalyzeBean = subMap.get(s);
                    if (tempAnalyzeBean != null) {
                        if (analyzeBean == null) {
                            analyzeBean = new AnalyzeBean();
                            analyzeBean.total = tempAnalyzeBean.total;
                            analyzeBean.same = tempAnalyzeBean.same;
                        } else {
                            analyzeBean.same += tempAnalyzeBean.same;
                            analyzeBean.total += tempAnalyzeBean.total;
                        }
                        mMap.put(s, analyzeBean);

                    }
                }
            }
            for (String s : mMap.keySet()) {
                AnalyzeBean analyzeBean = mMap.get(s);
                if (analyzeBean != null) {
                    double rate = (double) analyzeBean.same / analyzeBean.total;
                    if (rate > RMAX / 100 && analyzeBean.total > 20) {
                        posMap.put(s, analyzeBean);
                    }
                    if (rate < RMIN / 100 && analyzeBean.total > 20) {
                        negMap.put(s, analyzeBean);
                    }
                }
            }
        } else {
            HashMap<String, AnalyzeBean> subMap = mapArrayList.get(mapArrayList.size() - count);
            for (String s : subMap.keySet()) {
                AnalyzeBean analyzeBean = mMap.get(s);
                AnalyzeBean tempAnalyzeBean = subMap.get(s);
                if (tempAnalyzeBean != null) {
                    if (analyzeBean == null) {
                        analyzeBean = new AnalyzeBean();
                        analyzeBean.same = tempAnalyzeBean.same;
                        analyzeBean.total = tempAnalyzeBean.total;
                    } else {
                        analyzeBean.same -= tempAnalyzeBean.same;
                        analyzeBean.total -= tempAnalyzeBean.total;
                    }
                    mMap.put(s, analyzeBean);
                }
            }
            for (String s : mMap.keySet()) {
                AnalyzeBean analyzeBean = mMap.get(s);
                if (analyzeBean != null) {
                    double rate = (double) analyzeBean.same / analyzeBean.total;
                    if (rate < RMAX / 100 && rate > RMIN / 100) {
                        posMap.remove(s);
                        negMap.remove(s);
                    }
                }
            }
        }

    }

    public static class AnalyzeBean {

        int same;
        int total;

    }
}
