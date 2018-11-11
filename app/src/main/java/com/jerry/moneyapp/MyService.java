package com.jerry.moneyapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.MyLog;
import com.jerry.moneyapp.util.DeviceUtil;
import com.jerry.moneyapp.util.WeakHandler;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyService extends Service {

    private double winn;//模拟净胜
    private double win;//净胜
    private int intention;//净胜
    private static final int LEFT = 12;//17
    private static final int RIGHT = 1068;//144
    private static final int TOP = 470;//610
    private static final int BOTTOM = 805;//1080
    public static final int ASSIABLEX = 990;//1320
    public static final int ASSIABLEY = 900;//1180

    public static final int MIDDELX = 500;//1180
    public static final int ENTERY = 930;//1180
    public static final int JUDGEY = 1240;//1180

    private int width;
    private int height;

    private int[] pointsX = new int[18];
    private int[] pointsY = new int[6];
    private LinkedList<Integer> data = new LinkedList<>();
    private volatile int length;
    private boolean mBtnClickable;//点击生效
    private Callback mCallback;
    private StringBuilder sb = new StringBuilder();

    protected WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == -1) {
                return false;
            }
            boolean enter = GBData.getCurrentData(pointsX, pointsY, data);
            if (enter) {
                execShellCmd("input tap " + MIDDELX + " " + ENTERY);
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
            execShellCmd("input tap " + 400 + " " + 400);
            ArrayList<Integer> paint = new ArrayList<>(data.size());
            int index = 0;
            int tempSize = 1;
            while (index < data.size()) {
                if (index == data.size() - 1) {
                    paint.add(tempSize);
                } else {
                    if (data.get(index).intValue() == data.get(index + 1).intValue()) {
                        tempSize++;
                    } else {
                        paint.add(tempSize);
                        tempSize = 1;
                    }
                }
                index++;
            }
            //0:找胜负，1：找孤岛，2：找连板
            int state = 0;
            winn = 0;
            for (int i = 0; i < paint.size(); i++) {
                int current = paint.get(i);
                if (i + 1 < paint.size()) {
                    int next = paint.get(i + 1);
                    switch (state) {
                        case 1:
                            if (current == 1) {
                                state = 0;
                            }
                            break;
                        case 2:
                            if (current > 1) {
                                state = 0;
                            }
                            break;
                        default:
                            if (current > 1 && next > 1) {
                                winn += 9.7;
                                state = 1;
                            } else if (current > 1 && next == 1) {
                                winn -= 10;
                                state = 2;
                            }
                            break;
                    }
                }
            }
            if (intention != GBData.VALUE_NONE && mBtnClickable) {
                if (intention == data.getLast()) {
                    win += 9.7;
                } else {
                    win -= 10;
                }
            }
            if (state == 0 && paint.size() > 0 && paint.get(paint.size() - 1) > 1) {
                intention = data.getLast();
                showJingsheng();
                if (mBtnClickable) {
                    exeCall(intention, 1);
                }
            } else {
                intention = GBData.VALUE_NONE;
            }

            double finalWinn = winn;
            if (data.size() >= 69) {
                BmobQuery<MyLog> query = new BmobQuery<>();
                query.setLimit(1).order("-updatedAt").findObjects(new FindListener<MyLog>() {
                    @Override
                    public void done(List<MyLog> list, BmobException e) {
                        if (e != null) {
                            return;
                        }
                        if (list.size() > 0) {
                            long lastTime = 0;
                            try {
                                String lateDate = list.get(0).getCreatedAt();
                                lastTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).parse(lateDate).getTime();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            if (lastTime > 0) {
                                long second = (System.currentTimeMillis() - lastTime) / 1000;
                                if (second < 200) {
                                    return;
                                }
                            }
                        }
                        Calendar now = Calendar.getInstance();
                        sb.append(now.getTime()).append(":").append(finalWinn).append("元").append("\n");
                        MyLog myLog = new MyLog();
                        myLog.setLog(sb.toString());
                        myLog.setData(data);
                        myLog.setDeviceId(DeviceUtil.getDeviceId());
                        myLog.save();
                        sb.delete(0, sb.length());
                    }
                });
            } else {
                Calendar now = Calendar.getInstance();
                sb.append(now.getTime()).append(":").append(finalWinn).append("元").append("\n");
            }
            return false;
        }
    });

    private String getIntentStr(int intent, int mutiple) {
        if (intent == GBData.VALUE_NONE) {
            return " pass";
        }
        return (intent == GBData.VALUE_LONG ? "  龙" : "  凤") + String.valueOf(mutiple);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        width = Resources.getSystem().getDisplayMetrics().widthPixels;
        height = Resources.getSystem().getDisplayMetrics().heightPixels;
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

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public void setBtnClickable() {
        mBtnClickable = !mBtnClickable;
        Toast.makeText(this, mBtnClickable ? "点击生效！" : "点击取消!", Toast.LENGTH_SHORT).show();
    }

    public void showJingsheng() {
        mCallback.showText(new StringBuilder().append("\n模拟净胜：").append(DeviceUtil.m2(winn)).append("，")
                .append("\n实净胜：").append(DeviceUtil.m2(win)).toString());
    }

    public class PlayBinder extends Binder {

        public MyService getPlayService() {
            return MyService.this;
        }
    }

    public void startExe() {
        mWeakHandler.sendEmptyMessage(0);
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

    private void exeCall(int type, int mutiple) {
        int clickX = type == GBData.VALUE_LONG ? (int) (width * 0.25) : (int) (width * 0.75);
        int clickY = (int) (height * 0.9);
        new CountDownTimer(500 * (Math.abs(mutiple) + 1), 500) {

            @Override
            public void onTick(final long millisUntilFinished) {
                execShellCmd("input tap " + clickX + " " + clickY);
            }

            @Override
            public void onFinish() {
                execShellCmd("input tap " + ASSIABLEX + " " + ASSIABLEY);
            }
        }.start();
    }


    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {

        void showText(String data);
    }
}