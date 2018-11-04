package com.jerry.moneyapp.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jerry.moneyapp.R;
import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.MyLog;
import com.jerry.moneyapp.bean.Point;
import com.jerry.moneyapp.util.CaluUtil;
import com.jerry.moneyapp.util.DeviceUtil;
import com.jerry.moneyapp.util.MyTextWatcherListener;
import com.jerry.moneyapp.util.ParseUtil;
import com.jerry.moneyapp.util.ViewHolder;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AnalyzeActivity extends AppCompatActivity {

    public static int START = 5;
    public static double WHOLEWIN2 = 4.3;
    public static double WHOLEWIN3 = 8;
    public static int LASTAWARDNUM2 = 12;
    public static int LASTAWARDNUM3 = 19;
    public static int LASTWINNUM2 = 9;
    public static int LASTWINNUM3 = 7;

    public static double K21 = -0.06;
    public static double K22 = 15;
    public static double K31 = -0.38;
    public static double K32 = 9;

    private List<MyLog> mMyLogs = new ArrayList<>();
    private ArrayList<LinkedList<Point>> pointss = new ArrayList<>();
    private CommonAdapter<LinkedList<Point>> mAdapter;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        text = findViewById(R.id.text);
        EditText etStart = findViewById(R.id.et_start);
        etStart.setText(String.valueOf(START));
        etStart.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                START = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText etTotlewin2 = findViewById(R.id.et_totlewin2);
        etTotlewin2.setText(String.valueOf(WHOLEWIN2));
        etTotlewin2.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                WHOLEWIN2 = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText etTotlewin3 = findViewById(R.id.et_totlewin3);
        etTotlewin3.setText(String.valueOf(WHOLEWIN3));
        etTotlewin3.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                WHOLEWIN3 = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText awardNum2 = findViewById(R.id.award_num2);
        awardNum2.setText(String.valueOf(LASTAWARDNUM2));
        awardNum2.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTAWARDNUM2 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText k21Et = findViewById(R.id.k21);
        k21Et.setText(String.valueOf(K21));
        k21Et.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                K21 = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText k22Et = findViewById(R.id.k22);
        k22Et.setText(String.valueOf(K22));
        k22Et.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                K22 = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText awardNum3 = findViewById(R.id.award_num3);
        awardNum3.setText(String.valueOf(LASTAWARDNUM3));
        awardNum3.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTAWARDNUM3 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText k31Et = findViewById(R.id.k31);
        k31Et.setText(String.valueOf(K31));
        k31Et.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                K31 = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText k32Et = findViewById(R.id.k32);
        k32Et.setText(String.valueOf(K32));
        k32Et.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                K32 = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText lastNum2 = findViewById(R.id.last_num2);
        lastNum2.setText(String.valueOf(LASTWINNUM2));
        lastNum2.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTWINNUM2 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText lastNum3 = findViewById(R.id.last_num3);
        lastNum3.setText(String.valueOf(LASTWINNUM3));
        lastNum3.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTWINNUM3 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        ListView listView = findViewById(R.id.listView);
        mAdapter = new CommonAdapter<LinkedList<Point>>(this, pointss) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = ViewHolder.get(mContext, convertView, R.layout.item_text);
                TextView date = holder.getView(R.id.date);
                TextView money = holder.getView(R.id.money);
                Point record = pointss.get(position).getLast();
                date.setText(mMyLogs.get(position).getCreatedAt());
                double win = pointss.get(position).getLast().win;
                if (win > 0) {
                    money.setTextColor(ContextCompat.getColor(AnalyzeActivity.this, android.R.color.holo_red_light));
                } else if (win < 0) {
                    money.setTextColor(ContextCompat.getColor(AnalyzeActivity.this, android.R.color.holo_green_light));
                } else {
                    money.setTextColor(ContextCompat.getColor(AnalyzeActivity.this, android.R.color.black));
                }
                money.setText(DeviceUtil.m2(win));
                return holder.getConvertView();
            }
        };
        listView.setAdapter(mAdapter);
        BmobQuery<MyLog> query = new BmobQuery<>();
        query.setLimit(200).order("-updatedAt").findObjects(new FindListener<MyLog>() {
            @Override
            public void done(List<MyLog> list, BmobException e) {
                if (e != null) {
                    return;
                }
                mMyLogs.clear();
                mMyLogs.addAll(list);
                updateData();
            }
        });
    }

    private void updateData() {
        pointss.clear();
        for (MyLog log : mMyLogs) {
            LinkedList<Point> points = new LinkedList<>();
            LinkedList<Integer> integers = log.getData();
            int[] ints = new int[integers.size()];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = integers.get(i);
            }
            Point lastP = null;
            for (int j = 0; j < ints.length; j++) {
                Point point = CaluUtil.calulate(ints, j + 1, points);
                point.current = ints[j];
                if (lastP != null) {
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
                        if (lastP.currentType == 2) {
                            if (lastP.intention == point.current) {
                                point.win = lastP.win + 9.7 * Math.abs(lastP.multiple2);
                            } else {
                                point.win = lastP.win - 10 * Math.abs(lastP.multiple2);
                            }
                        } else if (lastP.currentType == 3) {
                            if (lastP.intention == point.current) {
                                point.win = lastP.win + 9.7 * Math.abs(lastP.multiple3);
                            } else {
                                point.win = lastP.win - 10 * Math.abs(lastP.multiple3);
                            }
                        }
                    } else {
                        point.win = lastP.win;
                    }
                }
                if (LASTAWARDNUM2 > 0 && points.size() >= LASTAWARDNUM2) {
                    point.award2 = point.win2 - points.get(points.size() - LASTAWARDNUM2).win2;
                } else {
                    point.award2 = point.win2;
                }
                if (LASTAWARDNUM3 > 0 && points.size() >= LASTAWARDNUM3) {
                    point.award3 = point.win3 - points.get(points.size() - LASTAWARDNUM3).win3;
                } else {
                    point.award3 = point.win3;
                }
                if (point.award2 >= point.award3) {
                    point.currentType = 2;
                } else {
                    point.currentType = 3;
                }
                if (points.size() >= LASTWINNUM2) {
                    int[] tempInts = new int[LASTWINNUM2];
                    for (int i = 0; i < tempInts.length; i++) {
                        tempInts[i] = ints[points.size() - LASTWINNUM2 + i];
                    }
                    point.lastwin2 = CaluUtil.calu2(tempInts);
                }
                if (points.size() >= LASTWINNUM3) {
                    int[] tempInts = new int[LASTWINNUM3];
                    for (int i = 0; i < tempInts.length; i++) {
                        tempInts[i] = ints[points.size() - LASTWINNUM3 + i];
                    }
                    point.lastwin3 = CaluUtil.calu3(tempInts);
                }
                if (lastP != null) {
                    if (j > START && point.award2 + K21 * point.lastwin2 + K22 >= 0 && point.award3 + K31 * point.lastwin3 + K32 >= 0
                            && point.win2 > WHOLEWIN2 && point.win3 > WHOLEWIN3) {
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
                }

                lastP = point;
                points.add(point);
            }
            LinkedList<Point> fsf = log.getPoints();
            pointss.add(points);
        }
        mAdapter.notifyDataSetChanged();
        double win = 0;
        double oneMax = -99999;
        double oneMin = 99999;
        double totalMax = -99999;
        double totalMin = 99999;
        int winCount = 0;//胜场数
        int defeatCount = 0;//负场数
        for (LinkedList<Point> points : pointss) {
            double oneWin = points.getLast().win;
            win += oneWin;
            if (oneWin > 0) {
                winCount++;
            } else if (oneWin < 0) {
                defeatCount++;
            }
            if (oneWin > oneMax) {
                oneMax = oneWin;
            }
            if (oneWin < oneMin) {
                oneMin = oneWin;
            }
            for (Point point : points) {
                if (point.win > totalMax) {
                    totalMax = point.win;
                }
                if (point.win < totalMin) {
                    totalMin = point.win;
                }
            }
        }
        double winRate = (double) winCount / (winCount + defeatCount);
        text.setText(new StringBuilder("总净胜：").append(DeviceUtil.m2(win))
                .append("，胜率：").append(DeviceUtil.m2p(winRate))
                .append("，最多胜：").append(DeviceUtil.m2(oneMax))
                .append("，最多输：").append(DeviceUtil.m2(oneMin))
                .append("，峰值：").append(DeviceUtil.m2(totalMax))
                .append("，谷值：").append(DeviceUtil.m2(totalMin)));
    }
}
