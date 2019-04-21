package com.jerry.moneyapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.jerry.moneyapp.bean.BaseDao;
import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.Logg;
import com.jerry.moneyapp.bean.MyLog;
import com.jerry.moneyapp.bean.Point;
import com.jerry.moneyapp.util.CaluUtil;
import com.jerry.moneyapp.util.DeviceUtil;
import com.jerry.moneyapp.util.WeakHandler;

public class MyService extends Service {

    /**
     * 边界
     */
    public static int LEFT = 14;
    public static int RIGHT = 1068;
    public static int TOP = 475;
    public static int BOTTOM = 810;
    /**
     * 确定按键的坐标
     */
    public static final int ASSIABLEX = 990;
    public static final int ASSIABLEY = 900;
    /**
     * 是否需要重进的y坐标
     */
    public static final int JUDGEY = 1300;

    /**
     * 屏幕宽高
     */
    public static int width;
    public static int height;

    private int[] pointsX = new int[18];
    private int[] pointsY = new int[6];
    private LinkedList<Integer> data = new LinkedList<>();
    private volatile int length;
    private boolean mBtnClickable;//点击生效
    private Callback mCallback;
    private Point lastP;

    protected WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == -1) {
                return false;
            }
            boolean enter = GBData.initPix(pointsX, pointsY, data);
            if (enter) {
                execShellCmd("input tap " + RIGHT / 2 + " " + ASSIABLEY);
                mWeakHandler.sendEmptyMessageDelayed(0, 2000);
                return false;
            }
            mWeakHandler.sendEmptyMessageDelayed(0, 12000);
            if (data.size() == length) {
                return false;
            }
            if (data.size() == 0 && length < 68) {
                return false;
            }
            //点击一下空白处
            length = data.size();
            execShellCmd("input tap " + RIGHT / 2 + " " + ASSIABLEY);
            LinkedList<Point> points = new LinkedList<>();
            int[] ints = new int[data.size()];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = data.get(i);
            }
            lastP = null;
            for (int j = 0; j < ints.length; j++) {
                Point point = CaluUtil.calulate(ints, j + 1);
                if (lastP != null) {
                    if (lastP.intention != GBData.VALUE_NONE) {
                        if (lastP.intention == point.current) {
                            point.win = lastP.win + 9.7;
                        } else {
                            point.win = lastP.win - 10;
                        }
                    } else {
                        point.win = lastP.win;
                    }
                }
                lastP = point;
                points.add(point);
            }
            if (lastP == null) {
                return false;
            }
            if (mBtnClickable && lastP.intention != GBData.VALUE_NONE && data.size() < 69) {
                exeCall(lastP.intention);
            }
            showJingsheng();
            if (data.size() >= 69) {
                Logg logg = new Logg();
                logg.setCreateTime(DeviceUtil.getCurrentTime());
                logg.setDeviceId(DeviceUtil.getDeviceId());
                logg.setData(JSON.toJSONString(data));
                logg.setWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
                if (BaseDao.getTjDb().insertObject(logg)) {
                    MyLog myLog = new MyLog();
                    myLog.setCreateTime(DeviceUtil.getCurrentTime());
                    myLog.setData(data);
                    myLog.setDeviceId(DeviceUtil.getDeviceId());
                    myLog.setWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
                    myLog.save();
                }
            }
            return false;
        }
    });

    private String getIntentStr(int intention) {
        if (intention == GBData.VALUE_NONE) {
            return " pass";
        }
        return (intention == GBData.VALUE_LONG ? "  龙" : "  凤");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public void setBtnClickable() {
        mBtnClickable = !mBtnClickable;
        Toast.makeText(this, mBtnClickable ? "点击生效！" : "点击取消!", Toast.LENGTH_SHORT).show();
    }

    public void showJingsheng() {
        if (lastP == null) {
            return;
        }
        mCallback.showText(new StringBuilder()
            .append("模拟净胜：").append(DeviceUtil.m2(lastP.win)).append("，")
            .append("\t下一局：").append(getIntentStr(lastP.intention)).toString());
    }

    public class PlayBinder extends Binder {

        public MyService getPlayService() {
            return MyService.this;
        }
    }

    public void startExe() {
        mWeakHandler.sendEmptyMessage(0);
        if (GBData.initPix()) {
            double eachX = (RIGHT - LEFT) / 18d;
            double eachY = (BOTTOM - TOP) / 6d;
            double initX = LEFT + eachX * 0.85d;
            double initY = TOP + eachY / 2d;
            for (int i = 0; i < pointsX.length; i++) {
                pointsX[i] = (int) (initX + i * eachX);
            }
            for (int i = 0; i < pointsY.length; i++) {
                pointsY[i] = (int) (initY + i * eachY);
            }
        }
        showJingsheng();
    }

    private void execShellCmd(String cmd) {
        OutputStream outputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            outputStream = process.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd + "\n");
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exeCall(int type) {
        int clickX = type == GBData.VALUE_LONG ? (int) (width * 0.25) : (int) (width * 0.75);
        int clickY = (int) (height * 0.9);
        execShellCmd("input tap " + clickX + " " + clickY);
        mWeakHandler.postDelayed(() -> execShellCmd("input tap " + ASSIABLEX + " " + ASSIABLEY), 2000);
    }


    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {

        void showText(String data);
    }
}