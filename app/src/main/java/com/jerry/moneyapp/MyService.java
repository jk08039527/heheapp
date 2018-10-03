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
    private static int last = -1;
    private static int currentType;

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
            if (mPoints.size() == 0) {
                int last2 = 0;
                int last3 = 0;
                for (int i = 0; i < ints.length - 1; i++) {
                    Point point = CaluUtil.calulate(ints, i);
                    mPoints.add(point);
                    if (mPoints.size() == 1) {
                        last2 = point.type2;
                        last3 = point.type3;
                    } else {
                        if (point.type2 != 0) {
                            if (point.type2 == last2) {
                                win2 += 9.7;
                            } else {
                                win2 -= 10;
                            }
                        }
                        if (point.type3 != 0) {
                            if (point.type3 == last3) {
                                win3 += 9.7;
                            } else {
                                win3 -= 10;
                            }
                        }
                    }
                }
            }
            if (last != -1 && mPoints.size() > 0) {
                Point point = mPoints.get(mPoints.size() - 1);
                if (point.type2 != 0) {
                    if (point.type2 == last) {
                        win2 += 9.7;
                        if (currentType == 2) {
                            win += 9.7;
                        }
                    } else {
                        win2 -= 10;
                        if (currentType == 2) {
                            win -= 10;
                        }
                    }
                }
                if (point.type3 != 0) {
                    if (point.type3 == last) {
                        win3 += 9.7;
                        if (currentType == 3) {
                            win += 9.7;
                        }
                    } else {
                        win3 -= 10;
                        if (currentType == 3) {
                            win -= 10;
                        }
                    }
                }
            }
            Point point = CaluUtil.calulate(ints, ints.length);
            mPoints.add(point);
            if (win2 > win3) {
                currentType = 2;
            } else {
                currentType = 3;
            }
            if (win2 > 0 || win3 > 0) {
                if (currentType == 2) {
                    if (point.type2 != GBData.VALUE_NONE) {
                        showJingsheng((point.type3 == GBData.VALUE_LONG ? "  龙" : "  凤") + Math.abs(point.multiple2));
                        last = point.type2;
                        if (mBtnClickable || notPlay >= NOTPLAYCOUNT) {
                            notPlay = 0;
                            exeCall(point.type2, point.multiple2);
                        } else {
                            notPlay++;
                        }
                    } else {
                        notPlay++;
                        showJingsheng("孤岛太多:" + point.gudao2);
                    }
                } else {
                    if (point.type3 != GBData.VALUE_NONE) {
                        showJingsheng((point.type3 == GBData.VALUE_LONG ? "  龙" : "  凤") + Math.abs(point.multiple3));
                        last = point.type3;
                        if (mBtnClickable || notPlay >= NOTPLAYCOUNT) {
                            notPlay = 0;
                            exeCall(point.type3, point.multiple3);
                        } else {
                            notPlay++;
                        }
                    } else {
                        notPlay++;
                        showJingsheng("孤岛太多:" + point.gudao3);
                    }
                }
            } else {
                notPlay++;
                showJingsheng("");
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
                .append("\n净胜3:").append(DeviceUtil.m2(win3)).append("\n净胜：").append(DeviceUtil.m2(win))
                .append(other);
        Toast.makeText(MyService.this, sb.toString(), Toast.LENGTH_SHORT).show();
    }

    public class PlayBinder extends Binder {

        public MyService getPlayService() {
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