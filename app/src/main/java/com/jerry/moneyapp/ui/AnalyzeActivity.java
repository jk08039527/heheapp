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
    public static int WHOLEWIN2 = -1;
    public static int WHOLEWIN3 = -3;
    public static int GUDAOCOUNT2 = 12;
    public static int GUDAOCOUNT3 = 5;
    public static int GUDAOLINIT2 = 2;
    public static int GUDAOLINIT3 = 3;
    public static int LASTPOINTNUM = 14;
    public static int LASTWIN2 = -12;
    public static int LASTWIN3 = -14;

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
                WHOLEWIN2 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText etTotlewin3 = findViewById(R.id.et_totlewin3);
        etTotlewin3.setText(String.valueOf(WHOLEWIN3));
        etTotlewin3.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                WHOLEWIN3 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText etGdz2 = findViewById(R.id.et_gdz2);
        etGdz2.setText(String.valueOf(GUDAOCOUNT2));
        etGdz2.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GUDAOCOUNT2 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText etGdz3 = findViewById(R.id.et_gdz3);
        etGdz3.setText(String.valueOf(GUDAOCOUNT3));
        etGdz3.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GUDAOCOUNT3 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText etGd2 = findViewById(R.id.et_gd2);
        etGd2.setText(String.valueOf(GUDAOLINIT2));
        etGd2.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GUDAOLINIT2 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText etGd3 = findViewById(R.id.et_gd3);
        etGd3.setText(String.valueOf(GUDAOLINIT3));
        etGd3.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GUDAOLINIT3 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText lastNum = findViewById(R.id.last_num);
        lastNum.setText(String.valueOf(LASTPOINTNUM));
        lastNum.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTPOINTNUM = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText lastWin2 = findViewById(R.id.last_win2);
        lastWin2.setText(String.valueOf(LASTWIN2));
        lastWin2.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTWIN2 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText lastWin3 = findViewById(R.id.last_win3);
        lastWin3.setText(String.valueOf(LASTWIN3));
        lastWin3.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTWIN3 = ParseUtil.parseInt(s.toString());
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
                if (LASTPOINTNUM > 0 && points.size() >= LASTPOINTNUM) {
                    point.award2 = point.win2 - points.get(points.size() - LASTPOINTNUM).win2;
                    point.award3 = point.win3 - points.get(points.size() - LASTPOINTNUM).win3;
                } else {
                    point.award2 = point.win2;
                    point.award3 = point.win3;
                }
                if (point.award2 >= point.award3) {
                    point.currentType = 2;
                } else {
                    point.currentType = 3;
                }
                if (lastP != null) {
                    double qiwang = j * 0.8;
                    if (j > START && point.award2 >= LASTWIN2 && point.award3 >= LASTWIN3
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
