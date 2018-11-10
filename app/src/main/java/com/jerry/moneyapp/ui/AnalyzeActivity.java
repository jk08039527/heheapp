package com.jerry.moneyapp.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jerry.moneyapp.R;
import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.MyLog;
import com.jerry.moneyapp.bean.Point;
import com.jerry.moneyapp.ptrlib.widget.BaseRecyclerAdapter;
import com.jerry.moneyapp.ptrlib.widget.PtrRecyclerView;
import com.jerry.moneyapp.ptrlib.widget.RecyclerViewHolder;
import com.jerry.moneyapp.util.CaluUtil;
import com.jerry.moneyapp.util.DeviceUtil;
import com.jerry.moneyapp.util.MyTextWatcherListener;
import com.jerry.moneyapp.util.ParseUtil;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AnalyzeActivity extends AppCompatActivity {

    public static int START = 14;
    public static double WHOLEWIN2 = 4.8;
    public static double WHOLEWIN3 = 5.4;
    public static int LASTPOINTNUM2 = 13;
    public static double LASTWIN2 = -10.7;
    public static int LASTPOINTNUM3 = 19;
    public static double LASTWIN3 = -8;
    public static int OPPOSIT_COUNT = 10;
    public static int OPPOSIT_NUM = 7;

    private List<MyLog> mMyLogs = new ArrayList<>();
    private ArrayList<Record> pointss = new ArrayList<>();
    private BaseRecyclerAdapter<Record> mAdapter;
    private TextView text;
    private PtrRecyclerView mPtrRecyclerView;
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
        EditText lastNum2 = findViewById(R.id.last_num2);
        lastNum2.setText(String.valueOf(LASTPOINTNUM2));
        lastNum2.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTPOINTNUM2 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText lastWin2 = findViewById(R.id.last_win2);
        lastWin2.setText(String.valueOf(LASTWIN2));
        lastWin2.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTWIN2 = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText lastNum3 = findViewById(R.id.last_num3);
        lastNum3.setText(String.valueOf(LASTPOINTNUM3));
        lastNum3.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTPOINTNUM3 = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText lastWin3 = findViewById(R.id.last_win3);
        lastWin3.setText(String.valueOf(LASTWIN3));
        lastWin3.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LASTWIN3 = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText oppositCount = findViewById(R.id.opposit_count);
        oppositCount.setText(String.valueOf(OPPOSIT_COUNT));
        oppositCount.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                OPPOSIT_COUNT = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText oppositNum = findViewById(R.id.opposit_num);
        oppositNum.setText(String.valueOf(OPPOSIT_NUM));
        oppositNum.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                OPPOSIT_NUM = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        mPtrRecyclerView = findViewById(R.id.ptrRecyclerView);
        mAdapter = new BaseRecyclerAdapter<Record>(this, pointss) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final Record bean) {
                TextView date = holder.getView(R.id.date);
                TextView money = holder.getView(R.id.money);
                TextView daymoney = holder.getView(R.id.daymoney);
                Record record = pointss.get(position);
                date.setText(record.createTime);
                daymoney.setText(record.dayWin == 0 ? "" : DeviceUtil.m2(record.dayWin));
                double win = record.win;
                if (win > 0) {
                    money.setTextColor(ContextCompat.getColor(AnalyzeActivity.this, android.R.color.holo_red_light));
                } else if (win < 0) {
                    money.setTextColor(ContextCompat.getColor(AnalyzeActivity.this, android.R.color.holo_green_light));
                } else {
                    money.setTextColor(ContextCompat.getColor(AnalyzeActivity.this, android.R.color.black));
                }
                money.setText(DeviceUtil.m2(win));
            }
        };
        mPtrRecyclerView.setOnRefreshListener(this::getData);
        mPtrRecyclerView.setAdapter(mAdapter);
        getData();
    }

    private void getData() {
        BmobQuery<MyLog> query = new BmobQuery<>();
        query.setLimit(500).order("-updatedAt").findObjects(new FindListener<MyLog>() {
            @Override
            public void done(List<MyLog> list, BmobException e) {
                if (e != null) {
                    return;
                }
                mMyLogs.clear();
                mMyLogs.addAll(list);
                updateData();
                mPtrRecyclerView.onRefreshComplete();
            }
        });
    }

    private void updateData() {
        pointss.clear();
        double win = 0;
        double oneMax = -99999;
        double oneMin = 99999;
        double totalMax = -99999;
        double totalMin = 99999;
        int winCount = 0;//胜场数
        int defeatCount = 0;//负场数
        for (MyLog log : mMyLogs) {
            LinkedList<Integer> integers = log.getData();
            ArrayList<Integer> paint = new ArrayList<>(integers.size());
            int index = 0;
            int tempSize = 1;
            while (index < integers.size()) {
                if (index == integers.size() - 1) {
                    paint.add(tempSize);
                } else {
                    if (integers.get(index).intValue() == integers.get(index + 1).intValue()) {
                        tempSize++;
                    } else {
                        paint.add(tempSize);
                        tempSize = 1;
                    }
                }
                index++;
            }
            //0:找胜负，1：找孤岛，2：找连板
            int state = 0;
            double winn = 0;
            for (int i = 0; i < paint.size(); i++) {
                int current = paint.get(i);
                if (i + 1 < paint.size()) {
                    int next = paint.get(i + 1);
                    switch (state) {
                        case 1:
                            if (current == 1) {
                                state = 0;
                            }
                            break;
                        case 2:
                            if (current > 1) {
                                state = 0;
                            }
                            break;
                        default:
                            if (current > 1 && next > 1) {
                                winn += 9.7;
                                state = 1;
                            } else if (current > 1 && next == 1) {
                                winn -= 10;
                                state = 2;
                            }
                            break;
                    }
                }
                if (winn > totalMax) {
                    totalMax = winn;
                }
                if (winn < totalMin) {
                    totalMin = winn;
                }
            }
            win += winn;

            Record record = new Record();
            record.win = winn;
            record.createTime = log.getCreatedAt();
            pointss.add(record);

            win += record.win;
            if (record.win > 0) {
                winCount++;
            } else if (record.win < 0) {
                defeatCount++;
            }
            if (record.win > oneMax) {
                oneMax = record.win;
            }
            if (record.win < oneMin) {
                oneMin = record.win;
            }
        }

        double dayWin = 0;
        for (int i = pointss.size() - 1; i >= 0; i--) {
            Record record = pointss.get(i);
            if (i > 0) {
                Record last = pointss.get(i - 1);
                if (last.createTime.substring(0, 10).equals(record.createTime.substring(0, 10))) {
                    dayWin += last.win;
                } else {
                    record.dayWin = dayWin;
                    dayWin = 0;
                }
            } else {
                record.dayWin = dayWin;
            }
        }
        mAdapter.notifyDataSetChanged();

        double cart = 0;
        double winRate = 0;
        if (winCount > 0 || defeatCount > 0) {
            double avg = win / (winCount + defeatCount);
            int sum = 0;
            for (Record record : pointss) {
                double oneWin = record.win;
                if (oneWin == 0) {
                    continue;
                }
                sum += (avg - oneWin) * (avg - oneWin);
            }
            cart = Math.sqrt(sum / (winCount + defeatCount));
            winRate = (double) winCount / (winCount + defeatCount);
        }
        text.setText(new StringBuilder("总净胜：").append(DeviceUtil.m2(win))
                .append("，胜率：").append(DeviceUtil.m2p(winRate))
                .append("，最多胜：").append(DeviceUtil.m2(oneMax))
                .append("，最多输：").append(DeviceUtil.m2(oneMin))
                .append("，峰值：").append(DeviceUtil.m2(totalMax))
                .append("，谷值：").append(DeviceUtil.m2(totalMin))
                .append("，标准差：").append(DeviceUtil.m2(cart)));
    }

    double[] calu(int[] ints) {
        Point lastP = null;
        LinkedList<Point> ps = new LinkedList<>();
        for (int j = 0; j < ints.length; j++) {
            Point point = CaluUtil.calulate(ints, j + 1, ps);
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
            }
            lastP = point;
            ps.add(point);
        }
        return new double[]{lastP.win2, lastP.win3};
    }

    class Record {

        String createTime;
        double win;
        double dayWin;
    }
}
