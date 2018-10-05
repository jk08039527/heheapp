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
    private double win2;//净胜2
    private double win3;//净胜3
    private double award2;//净胜2，近10局
    private double award3;//净胜3，近10局
    private static final int LEFT = 12;//17
    private static final int RIGHT = 1068;//144
    private static final int TOP = 470;//610
    private static final int BOTTOM = 805;//1080
    public static final int ASSIABLEX = 990;//1320
    public static final int ASSIABLEY = 900;//1180
    private static final int NOTPLAYCOUNT = 10;

    private int width;
    private int height;
    private Point last;
    private int currentType = 2;

    private int[] pointsX = new int[18];
    private int[] pointsY = new int[6];
    private LinkedList<Integer> data = new LinkedList<>();
    private LinkedList<Point> mPoints = new LinkedList<>();
    private volatile int length;
    private int notPlay;
    private boolean mBtnClickable;//点击生效
    private Callback mCallback;

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
                Point lastP = new Point();
                lastP.gudao2 = 0;
                lastP.gudao3 = 0;
                lastP.intention2 = GBData.VALUE_LONG;
                lastP.intention3 = GBData.VALUE_LONG;
                lastP.current = GBData.VALUE_NONE;
                mPoints.add(lastP);
                for (int i = 0; i < ints.length; i++) {
                    Point point = CaluUtil.calulate(ints, i + 1);
                    point.current = ints[i];
                    mPoints.add(point);
                    if (lastP.intention2 != 0) {
                        if (lastP.intention2 == point.current) {
                            win2 += 9.7 * Math.abs(lastP.multiple2);
                        } else {
                            win2 -= 10 * Math.abs(lastP.multiple2);
                        }
                    }
                    if (lastP.intention3 != 0) {
                        if (lastP.intention3 == point.current) {
                            win3 += 9.7 * Math.abs(lastP.multiple3);
                        } else {
                            win3 -= 10 * Math.abs(lastP.multiple3);
                        }
                    }
                    lastP = point;
                    Log.d("win2", i + "：" + String.valueOf(win2));
                    Log.d("win3", i + "：" + String.valueOf(win3));
                }
            } else {
                Point point = CaluUtil.calulate(ints, ints.length);
                point.current = ints[ints.length - 1];
                mPoints.add(point);
                if (last != null) {
                    if (last.intention2 != GBData.VALUE_NONE) {
                        if (last.intention2 == point.current) {
                            win2 += 9.7 * Math.abs(last.multiple2);
                        } else {
                            win2 -= 10 * Math.abs(last.multiple2);
                        }
                        if (currentType == 2 && award2 > -10) {
                            if (last.intention2 == point.current) {
                                win += 9.7 * Math.abs(last.multiple2);
                            } else {
                                win -= 10 * Math.abs(last.multiple2);
                            }
                        }
                    }
                    if (last.intention3 != GBData.VALUE_NONE) {
                        if (last.intention3 == point.current) {
                            win3 += 9.7 * Math.abs(last.multiple3);
                        } else {
                            win3 -= 10 * Math.abs(last.multiple3);
                        }
                        if (currentType == 3 && award3 > -10) {
                            if (last.intention3 == point.current) {
                                win += 9.7 * Math.abs(last.multiple3);
                            } else {
                                win -= 10 * Math.abs(last.multiple3);
                            }
                        }
                    }
                }
                Log.d("win2", ints.length - 1 + "：" + String.valueOf(win2));
                Log.d("win3", ints.length - 1 + "：" + String.valueOf(win3));
            }
            int size = Math.min(mPoints.size(), 10);
            award2 = 0;
            award3 = 0;
            Point llp = mPoints.get(mPoints.size() - size);
            for (int i = mPoints.size() - size + 1; i < mPoints.size(); i++) {
                Point point = mPoints.get(i);
                if (llp.intention2 != GBData.VALUE_NONE) {
                    if (llp.intention2 == point.current) {
                        award2 += 9.7 * Math.abs(llp.multiple2);
                    } else {
                        award2 -= 10 * Math.abs(llp.multiple2);
                    }
                }
                if (llp.intention3 != GBData.VALUE_NONE) {
                    if (llp.intention3 == point.current) {
                        award3 += 9.7 * Math.abs(llp.multiple3);
                    } else {
                        award3 -= 10 * Math.abs(llp.multiple3);
                    }
                }
                llp = point;
            }
            if (award2 > award3) {
                currentType = 2;
            } else {
                currentType = 3;
            }
            last = mPoints.get(mPoints.size() - 1);
            if (currentType == 2) {
                if (last.intention2 != GBData.VALUE_NONE && award2 > -10) {
                    showJingsheng((last.intention2 == GBData.VALUE_LONG ? "  龙" : "  凤") + Math.abs(last.multiple2));
                    if (mBtnClickable || notPlay >= NOTPLAYCOUNT) {
                        notPlay = 0;
                        exeCall(last.intention2, last.multiple2);
                    } else {
                        notPlay++;
                    }
                } else {
                    notPlay++;
                    if (last.intention2 == GBData.VALUE_NONE) {
                        showJingsheng("孤岛太多:" + last.gudao2);
                    } else {
                        showJingsheng("板不好");
                    }
                }
            } else {
                if (last.intention3 != GBData.VALUE_NONE && award3 > -10) {
                    showJingsheng((last.intention3 == GBData.VALUE_LONG ? "  龙" : "  凤") + Math.abs(last.multiple3));
                    if (mBtnClickable || notPlay >= NOTPLAYCOUNT) {
                        notPlay = 0;
                        exeCall(last.intention3, last.multiple3);
                    } else {
                        notPlay++;
                    }
                } else {
                    notPlay++;
                    if (last.intention3 == GBData.VALUE_NONE) {
                        showJingsheng("孤岛太多:" + last.gudao3);
                    } else {
                        showJingsheng("板不好");
                    }
                }
            }
            if (data.size() >= 68) {
                Calendar now = Calendar.getInstance();
                StringBuilder sb = new StringBuilder();
                sb.append(now.getTime()).append(":").append(win).append("元").append("\n");
                MyLog myLog = new MyLog();
                myLog.setLog(sb.toString());
                myLog.setData(data);
                myLog.setDeviceId(DeviceUtil.getDeviceId());
                myLog.save();
                sb.delete(0, sb.length());
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
        sb.append("净胜2：").append(DeviceUtil.m2(win2)).append(",").append(DeviceUtil.m2(award2))
                .append("\n净胜3：").append(DeviceUtil.m2(win3)).append(",").append(DeviceUtil.m2(award3))
                .append("\n净胜：").append(DeviceUtil.m2(win));
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