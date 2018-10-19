package com.jerry.moneyapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

    private double win;//净胜
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
    private Point last;
    private int currentType = 2;

    private int[] pointsX = new int[18];
    private int[] pointsY = new int[6];
    private LinkedList<Integer> data = new LinkedList<>();
    private LinkedList<Point> mPoints = new LinkedList<>();
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
            int[] ints = new int[length];
            for (int i = 0; i < length; i++) {
                ints[i] = data.get(i);
            }
            if (mPoints.size() == 0 || ints.length == 0) {
                mPoints.clear();
                Point lastP = new Point();
                lastP.gudao2 = 0;
                lastP.gudao3 = 0;
                lastP.intention2 = GBData.VALUE_NONE;
                lastP.intention3 = GBData.VALUE_NONE;
                lastP.current = GBData.VALUE_NONE;
                mPoints.add(lastP);
                for (int i = 0; i < ints.length; i++) {
                    Point point = CaluUtil.calulate(ints, i + 1, mPoints);
                    point.current = ints[i];
                    if (lastP.intention2 != GBData.VALUE_NONE) {
                        if (lastP.intention2 == point.current) {
                            point.win2 = lastP.win2 + 9.7 * Math.abs(lastP.multiple2);
                        } else {
                            point.win2 = lastP.win2 - 10 * Math.abs(lastP.multiple2);
                        }
                    } else {
                        point.win2 = lastP.win2;
                    }
                    if (lastP.intention3 != GBData.VALUE_NONE) {
                        if (lastP.intention3 == point.current) {
                            point.win3 = lastP.win3 + 9.7 * Math.abs(lastP.multiple3);
                        } else {
                            point.win3 = lastP.win3 - 10 * Math.abs(lastP.multiple3);
                        }
                    } else {
                        point.win3 = lastP.win3;
                    }
                    if (mPoints.size() >= 15) {
                        point.award2 = point.win2 - mPoints.get(mPoints.size() - 15).win2;
                        point.award3 = point.win3 - mPoints.get(mPoints.size() - 15).win3;
                    } else {
                        point.award2 = point.win2;
                        point.award3 = point.win3;
                    }
                    mPoints.add(point);
                    lastP = point;
                    Log.d("win2", i + "：" + String.valueOf(lastP.win2));
                    Log.d("win3", i + "：" + String.valueOf(lastP.win3));
                }
            } else {
                Point point = CaluUtil.calulate(ints, ints.length, mPoints);
                point.current = ints[ints.length - 1];
                if (last != null) {
                    if (last.intention2 != GBData.VALUE_NONE) {
                        if (last.intention2 == point.current) {
                            point.win2 = last.win2 + 9.7 * Math.abs(last.multiple2);
                        } else {
                            point.win2 = last.win2 - 10 * Math.abs(last.multiple2);
                        }
                    } else {
                        point.win2 = last.win2;
                    }
                    if (last.intention3 != GBData.VALUE_NONE) {
                        if (last.intention3 == point.current) {
                            point.win3 = last.win3 + 9.7 * Math.abs(last.multiple3);
                        } else {
                            point.win3 = last.win3 - 10 * Math.abs(last.multiple3);
                        }
                    } else {
                        point.win3 = last.win3;
                    }
                    if (last.intention != GBData.VALUE_NONE) {
                        int mutiple = 1;
                        if (currentType == 2) {
                            mutiple = last.multiple2;
                        } else {
                            mutiple = last.multiple3;
                        }
                        if (last.intention == point.current) {
                            win += 9.7 * Math.abs(mutiple);
                        } else {
                            win -= 10 * Math.abs(mutiple);
                        }
                    }
                    if (mPoints.size() >= 15) {
                        point.award2 = point.win2 - mPoints.get(mPoints.size() - 15).win2;
                        point.award3 = point.win3 - mPoints.get(mPoints.size() - 15).win3;
                    } else {
                        point.award2 = point.win2;
                        point.award3 = point.win3;
                    }
                }
                Log.d("win2", ints.length - 1 + "：" + String.valueOf(point.win2));
                Log.d("win3", ints.length - 1 + "：" + String.valueOf(point.win3));
                mPoints.add(point);
            }
            last = mPoints.get(mPoints.size() - 1);
            if (last.award2 >= last.award3) {
                currentType = 2;
            } else {
                currentType = 3;
            }
            if (ints.length > 6 && last.award2 > 0 && last.award3 > 0 && last.win2 > 0 && last.win3 > 0) {
                if (currentType == 2 && (last.multiple2 < 0 || last.intention2 != GBData.VALUE_NONE)) {
                    last.intention = last.intention2;
                    showJingsheng((getIntentStr(last.intention2, last.multiple2)));
                    if (mBtnClickable) {
                        exeCall(last.intention2, last.multiple2);
                    }
                } else if (currentType == 3 && (last.multiple3 < 0 || last.intention3 != GBData.VALUE_NONE)) {
                    last.intention = last.intention3;
                    showJingsheng((getIntentStr(last.intention3, last.multiple3)));
                    if (mBtnClickable) {
                        exeCall(last.intention3, last.multiple3);
                    }
                }
            } else {
                last.intention = GBData.VALUE_NONE;
                showJingsheng("板不好");
            }

            if (data.size() >= 69) {
                Calendar now = Calendar.getInstance();
                sb.append(now.getTime()).append(":").append(win).append("元").append("\n");
                MyLog myLog = new MyLog();
                myLog.setLog(sb.toString());
                myLog.setData(data);
                myLog.setDeviceId(DeviceUtil.getDeviceId());
                myLog.save();
                sb.delete(0, sb.length());
            } else {
                Calendar now = Calendar.getInstance();
                sb.append(now.getTime()).append(":").append(win).append("元").append("\n");
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

    public void showJingsheng(String other) {
        if (last == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("净胜2：").append(DeviceUtil.m2(last.win2)).append("，")
                .append(DeviceUtil.m2(last.award2)).append("，")
                .append(getIntentStr(last.intention2, Math.abs(last.multiple2)))
                .append("\n净胜3：").append(DeviceUtil.m2(last.win3)).append("，")
                .append(DeviceUtil.m2(last.award3)).append("，")
                .append(getIntentStr(last.intention3, Math.abs(last.multiple3)))
                .append("\n净胜：").append(DeviceUtil.m2(win)).append("，").append(getIntentStr(last.intention, 0));
        if (!TextUtils.isEmpty(other)) {
            sb.append("\n").append(other);
        }
        Toast.makeText(MyService.this, sb.toString(), Toast.LENGTH_SHORT).show();
        mCallback.showText(sb.toString());
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


    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {

        void showText(String data);
    }
}