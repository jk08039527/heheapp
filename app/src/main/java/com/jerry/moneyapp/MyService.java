package com.jerry.moneyapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.MyLog;
import com.jerry.moneyapp.bean.Point;
import com.jerry.moneyapp.ui.AnalyzeActivity;
import com.jerry.moneyapp.util.CaluUtil;
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
                sb.delete(0, sb.length());
                last = null;
                for (int j = 0; j < ints.length; j++) {
                    Point point = CaluUtil.calulate(ints, j + 1, mPoints);
                    point.current = ints[j];
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
                            if (last.currentType == 2) {
                                if (last.intention == point.current) {
                                    point.win = last.win + 9.7 * Math.abs(last.multiple2);
                                } else {
                                    point.win = last.win - 10 * Math.abs(last.multiple2);
                                }
                            } else if (last.currentType == 3) {
                                if (last.intention == point.current) {
                                    point.win = last.win + 9.7 * Math.abs(last.multiple3);
                                } else {
                                    point.win = last.win - 10 * Math.abs(last.multiple3);
                                }
                            }
                        } else {
                            point.win = last.win;
                        }
                    }
                    if (AnalyzeActivity.LASTAWARDNUM2 > 0 && mPoints.size() >= AnalyzeActivity.LASTAWARDNUM2) {
                        point.award2 = point.win2 - mPoints.get(mPoints.size() - AnalyzeActivity.LASTAWARDNUM2).win2;
                    } else {
                        point.award2 = point.win2;
                    }
                    if (AnalyzeActivity.LASTAWARDNUM3 > 0 && mPoints.size() >= AnalyzeActivity.LASTAWARDNUM3) {
                        point.award3 = point.win3 - mPoints.get(mPoints.size() - AnalyzeActivity.LASTAWARDNUM3).win3;
                    } else {
                        point.award3 = point.win3;
                    }
                    if (point.award2 >= point.award3) {
                        point.currentType = 2;
                    } else {
                        point.currentType = 3;
                    }
                    if (mPoints.size() >= AnalyzeActivity.LASTWINNUM2) {
                        int[] tempInts = new int[AnalyzeActivity.LASTWINNUM2];
                        for (int i = 0; i < tempInts.length; i++) {
                            tempInts[i] = ints[mPoints.size() - AnalyzeActivity.LASTWINNUM2 + i];
                        }
                        point.lastwin2 = CaluUtil.calu2(tempInts);
                    }
                    if (mPoints.size() >= AnalyzeActivity.LASTWINNUM3) {
                        int[] tempInts = new int[AnalyzeActivity.LASTWINNUM3];
                        for (int i = 0; i < tempInts.length; i++) {
                            tempInts[i] = ints[mPoints.size() - AnalyzeActivity.LASTWINNUM3 + i];
                        }
                        point.lastwin3 = CaluUtil.calu3(tempInts);
                    }

                    if (j > AnalyzeActivity.START
                            && AnalyzeActivity.K21 * point.award2 * point.lastwin2 + AnalyzeActivity.K22 * point.award2 + AnalyzeActivity
                            .K23 * point.lastwin2 + AnalyzeActivity.K24 >= 0
                            && AnalyzeActivity.K31 * point.award3 * point.lastwin3 + AnalyzeActivity.K32 * point.award3 + AnalyzeActivity
                            .K33 * point.lastwin3 + AnalyzeActivity.K34 >= 0
                            && point.win2 > AnalyzeActivity.WHOLEWIN2
                            && point.win3 > AnalyzeActivity.WHOLEWIN3) {
                        if (point.currentType == 2 && point.intention2 != GBData.VALUE_NONE) {
                            point.intention = point.intention2;
                            point.multiple = point.multiple2;
                        } else if (point.currentType == 3 && point.intention3 != GBData.VALUE_NONE) {
                            point.intention = point.intention3;
                            point.multiple = point.multiple3;
                        } else {
                            point.intention = GBData.VALUE_NONE;
                        }
                    } else {
                        point.intention = GBData.VALUE_NONE;
                    }
                    mPoints.add(point);
                    last = point;
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
                        if (last.currentType == 2) {
                            if (last.intention == point.current) {
                                point.win = last.win + 9.7 * Math.abs(last.multiple2);
                                if (mBtnClickable) {
                                    win += 9.7 * Math.abs(last.multiple2);
                                }
                            } else {
                                point.win = last.win - 10 * Math.abs(last.multiple2);
                                if (mBtnClickable) {
                                    win -= 10 * Math.abs(last.multiple3);
                                }
                            }
                        } else if (last.currentType == 3) {
                            if (last.intention == point.current) {
                                point.win = last.win + 9.7 * Math.abs(last.multiple3);
                                if (mBtnClickable) {
                                    win += 9.7 * Math.abs(last.multiple3);
                                }
                            } else {
                                point.win = last.win - 10 * Math.abs(last.multiple3);
                                if (mBtnClickable) {
                                    win -= 10 * Math.abs(last.multiple3);
                                }
                            }
                        }
                    } else {
                        point.win = last.win;
                    }
                    if (AnalyzeActivity.LASTAWARDNUM2 > 0 && mPoints.size() >= AnalyzeActivity.LASTAWARDNUM2) {
                        point.award2 = point.win2 - mPoints.get(mPoints.size() - AnalyzeActivity.LASTAWARDNUM2).win2;
                    } else {
                        point.award2 = point.win2;
                    }
                    if (AnalyzeActivity.LASTAWARDNUM3 > 0 && mPoints.size() >= AnalyzeActivity.LASTAWARDNUM3) {
                        point.award3 = point.win3 - mPoints.get(mPoints.size() - AnalyzeActivity.LASTAWARDNUM3).win3;
                    } else {
                        point.award3 = point.win3;
                    }
                    if (point.award2 >= point.award3) {
                        point.currentType = 2;
                    } else {
                        point.currentType = 3;
                    }
                    if (mPoints.size() >= AnalyzeActivity.LASTWINNUM2) {
                        int[] tempInts = new int[AnalyzeActivity.LASTWINNUM2];
                        for (int i = 0; i < tempInts.length; i++) {
                            tempInts[i] = ints[mPoints.size() - AnalyzeActivity.LASTWINNUM2 + i];
                        }
                        point.lastwin2 = CaluUtil.calu2(tempInts);
                    }
                    if (mPoints.size() >= AnalyzeActivity.LASTWINNUM3) {
                        int[] tempInts = new int[AnalyzeActivity.LASTWINNUM3];
                        for (int i = 0; i < tempInts.length; i++) {
                            tempInts[i] = ints[mPoints.size() - AnalyzeActivity.LASTWINNUM3 + i];
                        }
                        point.lastwin3 = CaluUtil.calu3(tempInts);
                    }
                }
                last = point;
                mPoints.add(point);
            }
            if (last == null) {
                return false;
            }
            if (ints.length >= AnalyzeActivity.START
                    && AnalyzeActivity.K21 * last.award2 * last.lastwin2 + AnalyzeActivity.K22 * last.award2 + AnalyzeActivity
                    .K23 * last.lastwin2 + AnalyzeActivity.K24 >= 0
                    && AnalyzeActivity.K31 * last.award3 * last.lastwin3 + AnalyzeActivity.K32 * last.award3 + AnalyzeActivity
                    .K33 * last.lastwin3 + AnalyzeActivity.K34 >= 0
                    && last.win2 > AnalyzeActivity.WHOLEWIN2
                    && last.win3 > AnalyzeActivity.WHOLEWIN3) {
                if (last.currentType == 2 && last.intention2 != GBData.VALUE_NONE) {
                    last.intention = last.intention2;
                    last.multiple = last.multiple2;
                    showJingsheng();
                    if (mBtnClickable) {
                        exeCall(last.intention2, last.multiple2);
                    }
                } else if (last.currentType == 3 && last.intention3 != GBData.VALUE_NONE) {
                    last.intention = last.intention3;
                    last.multiple = last.multiple3;
                    showJingsheng();
                    if (mBtnClickable) {
                        exeCall(last.intention3, last.multiple3);
                    }
                } else {
                    last.intention = GBData.VALUE_NONE;
                    showJingsheng();
                }
            } else {
                last.intention = GBData.VALUE_NONE;
                showJingsheng();
            }

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
                        sb.append(now.getTime()).append(":").append(last.win).append("元").append("\n");
                        MyLog myLog = new MyLog();
                        myLog.setLog(sb.toString());
                        myLog.setData(data);
                        myLog.setDeviceId(DeviceUtil.getDeviceId());
                        myLog.setPoints(mPoints);
                        myLog.save();
                        sb.delete(0, sb.length());
                    }
                });
            } else {
                Calendar now = Calendar.getInstance();
                sb.append(now.getTime()).append(":").append(last.win).append("元").append("\n");
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
                .append("\n模拟净胜：").append(DeviceUtil.m2(last.win)).append("，").append(getIntentStr(last.intention, last.multiple))
                .append("\n实净胜：").append(DeviceUtil.m2(win));
        mCallback.showText(sb.toString());
    }

    public class PlayBinder extends Binder {

        public MyService getPlayService() {
            return MyService.this;
        }
    }

    public void startExe() {
        mWeakHandler.sendEmptyMessage(0);
        mPoints.clear();
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