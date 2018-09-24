package com.jerry.moneyapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

    private static final String TAG = "MyService";
    private static double win;//净胜
    private static final int LEFT = 12;//17
    private static final int RIGHT = 1068;//144
    private static final int TOP = 470;//610
    private static final int BOTTOM = 805;//1080
    public static final int ASSIABLEX = 990;//1320
    public static final int ASSIABLEY = 900;//1180
    private static final int GUDAO = 6;
    private static final int NOTPLAYCOUNT = 10;

    private static int width;
    private static int height;
    private static int last = GBData.VALUE_LONG;

    private int[] pointsX = new int[18];
    private int[] pointsY = new int[6];
    private LinkedList<Integer> data = new LinkedList<>();
    private volatile int length;
    private int money;
    private int notPlay;
    private boolean mBtnClickable;//点击生效
    private static final int LOGCOUNT = 20;
    private int logCount;
    private int multiple = 1;//倍数
    private ArrayList<Integer> paint = new ArrayList<>();
    private StringBuilder sb = new StringBuilder();

    protected WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == -1) {
                return false;
            }
            mWeakHandler.sendEmptyMessageDelayed(0, 12000);
            GBData.getCurrentData(pointsX, pointsY, data);
            if (data.size() == length) {
                return false;
            }
            if (data.size() == 0 && length < 68) {
                return false;
            }
            //点击一下空白处
            length = data.size();
            execShellCmd("input tap " + 400 + " " + 400);
            int[] ints = new int[length];
            for (int i = 0; i < length; i++) {
                ints[i] = data.get(i);
            }
            if (length > 0) {
                if (last == ints[length - 1]) {
                    win = win + money * 0.97;
                } else if (last != GBData.VALUE_NONE) {
                    win = win - money;
                }
                // 判断是否加倍
                paint.clear();
                int index = length - 1;
                int tempSize = 1;
                while (index > 0) {
                    index--;
                    if (ints[index] == ints[index + 1]) {
                        tempSize++;
                    } else {
                        paint.add(tempSize);
                        tempSize = 1;
                        if (paint.size() > 2) {
                            break;
                        }
                    }
                }
                multiple = 1;
                if (paint.size() > 1 && paint.get(1) > 1 && paint.get(0) + paint.get(1) > 5) {
                    multiple = 2;
                } else if (paint.size() > 2 && paint.get(0) > 1 && paint.get(1) > 1 && paint.get(2) > 1 && paint.get(0) + paint.get(1) +
                        paint.get(2) > 6) {
                    multiple = 2;
                } else if (paint.size() > 2 && paint.get(0) == 1 && paint.get(1) == 1 && paint.get(2) == 1) {
                    multiple = -1;
                }
            } else {
                multiple = 1;
            }

            if (data.size() >= 68) {
                Calendar now = Calendar.getInstance();
                sb.append(now.getTime()).append(":").append(win).append("元").append("\n");
                MyLog myLog = new MyLog();
                myLog.setLog(sb.toString());
                myLog.setData(data);
                myLog.setDeviceId(DeviceUtil.getDeviceId());
                myLog.save();
                sb.delete(0, sb.length());
            }
            logCount++;
            // 当前是否可玩儿
            // 3个连续则投递。最后5个中2个孤岛放弃
            if (multiple > 0) {
                int wanIndex = 0;
                for (int i = length - 1; i >= length - Math.min(GUDAO, length); i--) {
                    if (i == length - 1 && ints[i] != ints[i - 1]) {
                        wanIndex++;
                    } else if (i > 0 && ints[i] != ints[i - 1] && ints[i] != ints[i + 1]) {
                        wanIndex++;
                    }
                }
                Log.d(TAG, "gudao: " + wanIndex);
                if (wanIndex >= 2 && notPlay < NOTPLAYCOUNT) {
                    last = GBData.VALUE_NONE;
                    Toast.makeText(MyService.this, "孤岛太多!" + wanIndex, Toast.LENGTH_SHORT).show();
                    notPlay++;
                    return false;
                }
            }
            money = (!mBtnClickable && notPlay >= NOTPLAYCOUNT) ? 10 : 10 * Math.abs(multiple);
            if (length > 0) {
                if (multiple < 0) {
                    last = ints[length - 2];
                } else {
                    last = ints[length - 1];
                }
            } else {
                last = GBData.VALUE_LONG;
            }
            exeCall();
            return false;
        }
    });

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
        Toast.makeText(this, "净胜：" + DeviceUtil.m2(win), Toast.LENGTH_SHORT).show();
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

    private void exeCall() {
        int clickX;
        int clickY = (int) (height * 0.9);
        if (last == GBData.VALUE_LONG) {
            clickX = (int) (width * 0.25);
        } else {
            clickX = (int) (width * 0.75);
        }
        if (mBtnClickable || notPlay >= NOTPLAYCOUNT) {
            new CountDownTimer(500 * (money / 10 + 1), 500) {

                @Override
                public void onTick(final long millisUntilFinished) {
                    execShellCmd("input tap " + clickX + " " + clickY);
                }

                @Override
                public void onFinish() {
                    execShellCmd("input tap " + ASSIABLEX + " " + ASSIABLEY);
                }
            }.start();
            notPlay = 0;
        } else {
            notPlay++;
        }
        Toast.makeText(MyService.this, (last == GBData.VALUE_LONG ? "龙" : "凤") + money, Toast.LENGTH_SHORT).show();
    }
}