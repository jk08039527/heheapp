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

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.MyLog;
import com.jerry.moneyapp.bean.Param;
import com.jerry.moneyapp.bean.Point;
import com.jerry.moneyapp.util.CaluUtil;
import com.jerry.moneyapp.util.DeviceUtil;
import com.jerry.moneyapp.util.WeakHandler;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyService extends Service {

    /**
     * 边界
     */
    private static final int LEFT = 12;//17
    private static final int RIGHT = 1068;//144
    private static final int TOP = 470;//610
    private static final int BOTTOM = 805;//1080
    /**
     * 确定按键的坐标
     */
    public static final int ASSIABLEX = 990;//1320
    public static final int ASSIABLEY = 900;//1180

    /**
     * 重进一次
     */
    public static final int OUTX = 100;
    public static final int OUTY = 1080;
    public static final int MIDDELX = 500;//1180
    /**
     * 空白处点击一下激活活动区的y坐标
     */
    public static final int ENTERY = 930;//1180
    /**
     * 是否需要重进的y坐标
     */
    public static final int JUDGEY = 1240;//1180

    /**
     * 屏幕宽高
     */
    private int width;
    private int height;
    /**
     * 界面卡死
     */
    private int indexOut;

    private int[] pointsX = new int[18];
    private int[] pointsY = new int[6];
    private LinkedList<Integer> data = new LinkedList<>();
    private volatile int length;
    private boolean mBtnClickable;//点击生效
    private Callback mCallback;
    private Point lastP;
    private Param weekend = new Param(Param.STATE_WEEKEND);
    private Param weekday = new Param(Param.STATE_WEEKDAY);

    protected WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == -1) {
                return false;
            } else if (msg.what == 1) {
                execShellCmd("input tap " + MIDDELX + " " + OUTY);
                mWeakHandler.sendEmptyMessageDelayed(2, 5000);
                return false;
            } else if (msg.what == 2) {
                execShellCmd("input tap " + OUTX + " " + OUTY);
                mWeakHandler.sendEmptyMessage(0);
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
                indexOut++;
                if (indexOut > 7) {
                    execShellCmd("input tap " + OUTX + " " + OUTY);
                    mWeakHandler.sendEmptyMessageDelayed(1, 800);
                }
                return false;
            }
            if (data.size() == 0 && length < 68) {
                return false;
            }
            indexOut = 0;
            //点击一下空白处
            length = data.size();
            execShellCmd("input tap " + 400 + " " + 400);
            LinkedList<Integer> paint = new LinkedList<>();
            LinkedList<Point> points = new LinkedList<>();
            int[] ints = new int[data.size()];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = data.get(i);
            }
            lastP = null;
            int stopCount = 0;
            int firstwin = 0;//0未玩，1：赢，2：输
            int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            Param currentWeek;
            if (week > 0 && week < 5) {
                currentWeek = weekday;
            } else {
                currentWeek = weekend;
            }
            for (int j = 0; j < ints.length; j++) {
                Point point = CaluUtil.calulate(ints, j + 1);
                point.current = ints[j];
                if (lastP != null) {
                    if (lastP.current == point.current && paint.size() > 0) {
                        int temp = paint.getLast();
                        paint.removeLast();
                        paint.addLast(++temp);
                    } else {
                        paint.add(1);
                    }
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
                    if (lastP.intention != GBData.VALUE_NONE) {
                        if (lastP.intention == point.current) {
                            point.win = lastP.win + 9.7 * Math.abs(lastP.multiple);
                            stopCount = 0;
                            if (firstwin == 0) {
                                firstwin = 1;
                            }
                        } else {
                            point.win = lastP.win - 10 * Math.abs(lastP.multiple);
                            stopCount++;
                            if (firstwin == 0) {
                                firstwin = 2;
                            }
                        }
                    } else {
                        point.win = lastP.win;
                    }
                } else {
                    paint.add(1);
                }
                if (firstwin < 2 && point.win > currentWeek.giveupcount && stopCount < Param.STOPCOUNT) {
                    if (currentWeek.lastpointnum2 > 0 && points.size() >= currentWeek.lastpointnum2) {
                        point.award2 = point.win2 - points.get(points.size() - currentWeek.lastpointnum2).win2;
                    } else {
                        point.award2 = point.win2;
                    }
                    if (currentWeek.lastpointnum3 > 0 && points.size() >= currentWeek.lastpointnum3) {
                        point.award3 = point.win3 - points.get(points.size() - currentWeek.lastpointnum3).win3;
                    } else {
                        point.award3 = point.win3;
                    }
                    if (point.award2 >= point.award3) {
                        point.currentType = 2;
                    } else {
                        point.currentType = 3;
                    }
                    if (lastP != null) {
                        if (firstwin < 2 && (j > currentWeek.start && point.award2 >= currentWeek.lastwin2 && point.award3 >= currentWeek.lastwin3
                            && point.win2 > currentWeek.wholewin2 && point.win3 > currentWeek.wholewin3)) {
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
                        if (point.multiple > 1 && point.win - 10 * point.multiple < currentWeek.giveupcount) {
                            point.multiple = 1;
                        }
                    }
                }
                lastP = point;
                points.add(point);
            }
            if (lastP == null) {
                return false;
            }
            if (mBtnClickable && lastP.intention != GBData.VALUE_NONE && data.size() < 69) {
                exeCall(lastP.intention, lastP.multiple);
            }
            showJingsheng();
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
                                String lateDate = list.get(0).getCreateTime();
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
                        MyLog myLog = new MyLog();
                        myLog.setCreateTime(DeviceUtil.getCurrentTime());
                        myLog.setData(data);
                        myLog.setDeviceId(DeviceUtil.getDeviceId());
                        myLog.setWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
                        myLog.save();
                    }
                });
            }
            return false;
        }
    });

    private String getIntentStr(int intention, int mutiple) {
        if (intention == GBData.VALUE_NONE) {
            return " pass";
        }
        return (intention == GBData.VALUE_LONG ? "  龙" : "  凤") + String.valueOf(mutiple);
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
        if (lastP == null) {
            return;
        }
        mCallback.showText(new StringBuilder()
            .append("模拟净胜：").append(DeviceUtil.m2(lastP.win)).append("，")
            .append("\t下一局：").append(getIntentStr(lastP.intention, lastP.multiple)).toString());
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
        for (int i = 0; i < mutiple; i++) {
            execShellCmd("input tap " + clickX + " " + clickY);
        }
        mWeakHandler.postDelayed(() -> execShellCmd("input tap " + ASSIABLEX + " " + ASSIABLEY), 2000);
    }


    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {

        void showText(String data);
    }
}