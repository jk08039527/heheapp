package com.jerry.moneyapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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

    private static final String TAG = "MyService";
    private static int win;//净胜
    private static final int LEFT = 9;
    private static final int RIGHT = 1071;
    private static final int TOP = 460;
    private static final int BOTTOM = 800;

    private static int width;
    private static final int CLICKY = 1700;

    private int[] pointsX = new int[18];
    private int[] pointsY = new int[6];
    private int tempSize;
    private int money;
    private static int last = GBData.VALUE_LONG;
    public static final int ASSIABLEX = 1000;
    public static final int ASSIABLEY = 870;
    private int count;

    protected WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == -1) {
                return false;
            }
            mWeakHandler.sendEmptyMessageDelayed(0, 9000);
            int[] data = GBData.getCurrentData(pointsX, pointsY, tempSize);
            if (data == null) {
                return false;
            }
            tempSize = data.length;
            if (last == data[tempSize - 1]) {
                win = win + money;
            } else {
                win = win - money;
            }
            if (win < -200) {
                stopSelf();
                mWeakHandler.sendEmptyMessage(-1);
            }
            int length = data.length;
            if (length > 2 && data[length - 1] != data[length - 2] && data[length - 3] != data[length - 2]) {
                Toast.makeText(MyService.this, "give up!", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if (data.length == 0) {
                    money = 10;
                    last = GBData.VALUE_LONG;
                } else {
                    money = 10;
                    if (data.length > 1 && data[length - 1] != data[length - 2]) {
                        money *= 2;
                        if (data.length > 2 && data[length - 2] != data[length - 3]) {
                            money *= 2;
                        }
                    }
                    last = data[length - 1];
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
            Toast.makeText(this, "execShellCmd Error" + money, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "close Error" + money, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void exeCall(int valueCode) {
        count++;
        int clickX;
        if (valueCode == GBData.VALUE_LONG) {
            clickX = (int) (width * 0.25);
        } else {
            clickX = (int) (width * 0.75);
        }

        new CountDownTimer(500 * (money / 10 + 1), 500) {

            @Override
            public void onTick(final long millisUntilFinished) {
                execShellCmd("input tap " + clickX + " " + CLICKY);
            }

            @Override
            public void onFinish() {
                execShellCmd("input tap " + ASSIABLEX + " " + ASSIABLEY);
            }
        }.start();
        Toast.makeText(this, valueCode == GBData.VALUE_LONG ? "long" : "feng" + money, Toast.LENGTH_SHORT).show();
    }

    public void exeLogin() {

        execShellCmd("input tap 180 1345");//w
        execShellCmd("input tap 230 1650");//z
        execShellCmd("input tap 980 1500");//l
        execShellCmd("input tap 200 1850");//123
        execShellCmd("input tap 800 1700");//9
        execShellCmd("input tap 500 1350");//2
        execShellCmd("input tap 500 1850");//0
        execShellCmd("input tap 500 1700");//8
        execShellCmd("input tap 500 1850");//0
        execShellCmd("input tap 800 1350");//8


        execShellCmd("input tap 750 960");
        mWeakHandler.postDelayed(() -> {
            execShellCmd("input tap 360 760");
            mWeakHandler.postDelayed(() -> {
                execShellCmd("input tap 180 1345");
            }, 100);
        }, 1000);
    }
}