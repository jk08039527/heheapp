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
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

    private static final String TAG = "MyService";
    private static int win;//净胜
    private static final int LEFT = 9;//17
    private static final int RIGHT = 1071;//144
    private static final int TOP = 460;//610
    private static final int BOTTOM = 800;//1080
    public static final int ASSIABLEX = 1000;//1320
    public static final int ASSIABLEY = 870;//1180
    public static final int COUNTY = 6;

    private static int width;
    private static int height;
    private static int last = GBData.VALUE_LONG;

    private int[] pointsX = new int[18];
    private int[] pointsY = new int[6];
    private LinkedList<Integer> data = new LinkedList<>();
    private int length;
    private int money;
    private int count;

    protected WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == -1) {
                return false;
            }
            mWeakHandler.sendEmptyMessageDelayed(0, 9000);
            GBData.getCurrentData(pointsX, pointsY, data);
            if (data.size() == length) {
                return false;
            }
            length = data.size();
            int[] ints = new int[length];
            for (int i = 0; i < length; i++) {
                ints[i] = data.get(i);
            }
            if (length > 0) {
                if (last == ints[length - 1]) {
                    win = win + money;
                } else {
                    win = win - money;
                }
            }
            Log.d(TAG, "handleMessage: " + win);
            // 当前是否可玩儿
            // 3个连续则投递。最后10个中3个孤岛放弃
            if (length > 0) {
                int wanIndex = 0;
                int wan = Math.min(length, 10);
                boolean keep = true;
                int temp = ints[length - 1];
                for (int i = length - 1; i > length - wan; i--) {
                    if (i == length - 1) {
                        if (length > 1 && ints[i] != ints[i - 1]) {
                            wanIndex++;
                        }
                    } else {
                        if (ints[i] != ints[i - 1] && ints[i] != ints[i + 1]) {
                            wanIndex++;
                        }
                        if (keep && i > length - wan - 3 && ints[i] != temp) {
                            keep = false;
                        }
                    }
                }
                if (!keep && (double) wanIndex / wan >= 0.3) {
                    Toast.makeText(MyService.this, "孤岛太多!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (length > 2 && ints[length - 1] != ints[length - 2] && ints[length - 3] != ints[length - 2]) {
                Toast.makeText(MyService.this, "本局放弃!", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if (length == 0) {
                    money = 10;
                    last = GBData.VALUE_LONG;
                } else {
                    money = 10;
                    if (length > 1 && ints[length - 1] != ints[length - 2]) {
                        money *= 2;
                        if (length > 2 && ints[length - 2] != ints[length - 3]) {
                            money *= 2;
                        }
                    }
                    last = ints[length - 1];
                }
                exeCall(last);
            }
            return false;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        width = Resources.getSystem().getDisplayMetrics().widthPixels;
        height = Resources.getSystem().getDisplayMetrics().heightPixels;
        double eachX = (RIGHT - LEFT) / 18;
        double eachY = (BOTTOM - TOP) / 6;
        double initX = LEFT + eachX * 0.875;
        double initY = TOP + eachY / 2;
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

    public class PlayBinder extends Binder {

        public MyService getPlayService() {
            return MyService.this;
        }
    }

    public void startExe() {
        mWeakHandler.sendEmptyMessage(0);
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

    private void exeCall(int valueCode) {
        count++;
        int clickX;
        int clickY = (int) (height * 0.9);
        if (valueCode == GBData.VALUE_LONG) {
            clickX = (int) (width * 0.25);
        } else {
            clickX = (int) (width * 0.75);
        }

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
        Toast.makeText(this, (valueCode == GBData.VALUE_LONG ? "龙" : "凤") + money, Toast.LENGTH_SHORT).show();
    }
}