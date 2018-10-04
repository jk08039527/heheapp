package com.jerry.moneyapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

public class MyService extends Service {

    private static double win;//净胜
    private static double win2;//净胜2
    private static double win3;//净胜3
    private static final int LEFT = 12;//17
    private static final int RIGHT = 1068;//144
    private static final int TOP = 470;//610
    private static final int BOTTOM = 805;//1080
    public static final int ASSIABLEX = 990;//1320
    public static final int ASSIABLEY = 900;//1180
    private static final int NOTPLAYCOUNT = 10;

    private static int width;
    private static int height;
    private static int last;
    private static int last2;
    private static int last3;
    private static int currentType = 2;

    private int[] pointsX = new int[18];
    private int[] pointsY = new int[6];
    private LinkedList<Integer> data = new LinkedList<>();
    private LinkedList<Point> mPoints = new LinkedList<>();
    private volatile int length;
    private int notPlay;
    private boolean mBtnClickable;//点击生效

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
            if (mPoints.size() == 0 || ints.length == 0) {
                mPoints.clear();
                int last2 = GBData.VALUE_LONG;
                int last3 = GBData.VALUE_LONG;
                Point initP = new Point();
                initP.gudao2 = 0;
                initP.gudao3 = 0;
                initP.intention2 = last2;
                initP.intention3 = last3;
                initP.current = GBData.VALUE_NONE;
                mPoints.add(initP);
                for (int i = 0; i < ints.length; i++) {
                    Point point = CaluUtil.calulate(ints, i + 1);
                    point.current = ints[i];
                    mPoints.add(point);
                    if (last2 != 0) {
                        if (last2 == point.current) {
                            win2 += 9.7 * Math.abs(point.multiple2);
                        } else {
                            win2 -= 10 * Math.abs(point.multiple2);
                        }
                    }
                    if (last3 != 0) {
                        if (last3 == point.current) {
                            win3 += 9.7 * Math.abs(point.multiple3);
                        } else {
                            win3 -= 10 * Math.abs(point.multiple3);
                        }
                    }
                    last2 = point.intention2;
                    last3 = point.intention3;
                }
            } else {
                Point point = CaluUtil.calulate(ints, ints.length);
                point.current = ints[ints.length - 1];
                mPoints.add(point);
                if (last2 != 0) {
                    if (last2 == point.current) {
                        win2 += 9.7 * Math.abs(point.multiple2);
                    } else {
                        win2 -= 10 * Math.abs(point.multiple2);
                    }
                }
                if (last3 != 0) {
                    if (last3 == point.current) {
                        win3 += 9.7 * Math.abs(point.multiple3);
                    } else {
                        win3 -= 10 * Math.abs(point.multiple3);
                    }
                }
                if (last != 0) {
                    if (last == point.current) {
                        win += 9.7 * Math.abs(point.multiple2);
                    } else {
                        win -= 10 * Math.abs(point.multiple2);
                    }
                }
            }
            Point point = mPoints.get(mPoints.size() - 1);
            last2 = point.intention2;
            last3 = point.intention3;
            if (win2 > win3) {
                currentType = 2;
            } else {
                currentType = 3;
            }
            if (currentType == 2) {
                if (point.intention2 != GBData.VALUE_NONE) {
                    showJingsheng((point.intention2 == GBData.VALUE_LONG ? "  龙" : "  凤") + Math.abs(point.multiple2));
                    last = point.intention2;
                    if ((mBtnClickable && win2 > 0) || notPlay >= NOTPLAYCOUNT) {
                        notPlay = 0;
                        exeCall(point.intention2, point.multiple2);
                    } else {
                        notPlay++;
                    }
                } else {
                    notPlay++;
                    showJingsheng("孤岛太多:" + point.gudao2);
                }
            } else {
                if (point.intention3 != GBData.VALUE_NONE) {
                    showJingsheng((point.intention3 == GBData.VALUE_LONG ? "  龙" : "  凤") + Math.abs(point.multiple3));
                    last = point.intention3;
                    if ((mBtnClickable && win3 > 0) || notPlay >= NOTPLAYCOUNT) {
                        notPlay = 0;
                        exeCall(point.intention3, point.multiple3);
                    } else {
                        notPlay++;
                    }
                } else {
                    notPlay++;
                    showJingsheng("孤岛太多:" + point.gudao3);
                }
            }
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

    public void showJingsheng(String other) {
        StringBuilder sb = new StringBuilder();
        sb.append("净胜2：").append(DeviceUtil.m2(win2))
                .append("\n净胜3:").append(DeviceUtil.m2(win3)).append("\n净胜：").append(DeviceUtil.m2(win));
        if (!TextUtils.isEmpty(other)) {
            sb.append("\n").append(other);
        }
        Toast.makeText(MyService.this, sb.toString(), Toast.LENGTH_SHORT).show();
    }

    class PlayBinder extends Binder {

        MyService getPlayService() {
            return MyService.this;
        }
    }

    public void startExe() {
        mWeakHandler.sendEmptyMessage(0);
        showJingsheng("");
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
}