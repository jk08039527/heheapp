package com.jerry.moneyapp.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jerry.moneyapp.R;
import com.jerry.moneyapp.bean.BaseDao;
import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.Logg;
import com.jerry.moneyapp.bean.Point;
import com.jerry.moneyapp.bean.Record;
import com.jerry.moneyapp.ptrlib.widget.BaseRecyclerAdapter;
import com.jerry.moneyapp.ptrlib.widget.PtrRecyclerView;
import com.jerry.moneyapp.ptrlib.widget.RecyclerViewHolder;
import com.jerry.moneyapp.util.CaluUtil;
import com.jerry.moneyapp.util.DeviceUtil;
import com.jerry.moneyapp.util.MyTextWatcherListener;
import com.jerry.moneyapp.util.ParseUtil;
import com.jerry.moneyapp.util.asyctask.AsycTask;

import static com.jerry.moneyapp.bean.Param.RMAX;
import static com.jerry.moneyapp.bean.Param.RMIN;
import static com.jerry.moneyapp.bean.Param.START;
import static com.jerry.moneyapp.bean.Param.STOPCOUNT;

public class AnalyzeActivity extends AppCompatActivity {

    private List<Logg> mMyLogs = new ArrayList<>();
    private ArrayList<Record> records = new ArrayList<>();
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
        EditText etRmax = findViewById(R.id.et_rmax);
        etRmax.setText(String.valueOf(RMAX));
        etRmax.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                RMAX = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText etRmin = findViewById(R.id.et_rmin);
        etRmin.setText(String.valueOf(RMIN));
        etRmin.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                RMIN = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText etK = findViewById(R.id.et_stop);
        etK.setText(String.valueOf(STOPCOUNT));
        etK.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                STOPCOUNT = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        mPtrRecyclerView = findViewById(R.id.ptrRecyclerView);
        mAdapter = new BaseRecyclerAdapter<Record>(this, records) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final Record bean) {
                TextView date = holder.getView(R.id.date);
                TextView money = holder.getView(R.id.money);
                TextView daymoney = holder.getView(R.id.daymoney);
                FrameLayout content = holder.getView(R.id.info_content);
                content.removeAllViews();
                if (bean.visible) {
                    DetailView detailView = new DetailView(AnalyzeActivity.this, bean.count, bean.points);
                    detailView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                        .LayoutParams.MATCH_PARENT));
                    content.addView(detailView);
                    content.setVisibility(View.VISIBLE);
                } else {
                    content.setVisibility(View.GONE);
                }
                date.setText(bean.createTime);
                daymoney.setText(bean.dayWin == 0 ? "" : DeviceUtil.m2(bean.dayWin));
                double win = bean.win;
                if (win > 0) {
                    money.setTextColor(ContextCompat.getColor(AnalyzeActivity.this, android.R.color.holo_red_light));
                } else if (win < 0) {
                    money.setTextColor(ContextCompat.getColor(AnalyzeActivity.this, android.R.color.holo_green_light));
                } else {
                    money.setTextColor(ContextCompat.getColor(AnalyzeActivity.this, android.R.color.black));
                }
                money.setText(DeviceUtil.m2(win));
                holder.getView(R.id.root).setOnClickListener(v -> {
                    if (content.getVisibility() == View.GONE) {
                        if (content.getChildCount() == 0) {
                            DetailView detailView = new DetailView(AnalyzeActivity.this, bean.count, bean.points);
                            detailView.setLayoutParams(
                                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            content.addView(detailView);
                        }
                        content.setVisibility(View.VISIBLE);
                        bean.visible = true;
                    } else {
                        content.setVisibility(View.GONE);
                        bean.visible = false;
                    }
                });
            }
        };
        mPtrRecyclerView.setOnRefreshListener(this::getData);
        mPtrRecyclerView.setAdapter(mAdapter);
        getData();
    }

    private void getData() {
        mMyLogs.clear();
        AsycTask.with(this).assign(() -> {
            List<Logg> loggs = BaseDao.getTjDb().queryAll(Logg.class);
            mMyLogs.addAll(loggs);
            return true;
        }).whenDone(result -> {
            updateData();
            mPtrRecyclerView.onRefreshComplete();
        }).execute();
    }

    private void updateData() {
        CaluUtil.mMap.clear();
        List<Record> tempR = new ArrayList<>();
        double win = 0;
        double oneMax = -99999;
        double oneMin = 99999;
        double totalMax = -99999;
        double totalMin = 99999;
        int winCount = 0;//胜场数
        int defeatCount = 0;//负场数
        int dayWinCount = 0;//负场数
        int dayDefeatCount = 0;//负场数
        for (int k = mMyLogs.size() - 1; k >= 0; k--) {
            Logg log = mMyLogs.get(k);
            LinkedList<Integer> integers = JSON.parseObject(log.getData(), DeviceUtil.type(LinkedList.class, Integer.class));
            LinkedList<Integer> paint = new LinkedList<>();
            LinkedList<Point> points = new LinkedList<>();
            int[] ints = new int[integers.size()];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = integers.get(i);
            }
            if (k > 500) {
                CaluUtil.analyze(ints);
                continue;
            }
            Point lastP = null;
            for (int j = 0; j < ints.length; j++) {
                Point point = CaluUtil.calulate(ints, j + 1);
                if (lastP != null) {
                    if (lastP.current == point.current && paint.size() > 0) {
                        int temp = paint.getLast();
                        paint.removeLast();
                        paint.addLast(++temp);
                    } else {
                        paint.add(1);
                    }
                    if (lastP.win > -STOPCOUNT && lastP.intention != GBData.VALUE_NONE && point.current != GBData.VALUE_NONE) {
                        if (lastP.intention == point.current) {
                            point.win = lastP.win + 9.7;
                            winCount++;
                        } else {
                            point.win = lastP.win - 10;
                            defeatCount++;
                        }
                    } else {
                        point.win = lastP.win;
                    }
                } else {
                    paint.add(1);
                }
                lastP = point;
                points.add(point);
            }

            Record record = new Record();
            record.win = lastP.win;
            record.createTime = log.getCreateTime();
            record.points = points;
            record.count = paint.size();
            tempR.add(record);

            win += record.win;
            if (record.win > oneMax) {
                oneMax = record.win;
            }
            if (record.win < oneMin) {
                oneMin = record.win;
            }
            for (Point point : points) {
                if (point.win > totalMax) {
                    totalMax = point.win;
                }
                if (point.win < totalMin) {
                    totalMin = point.win;
                }
            }
            CaluUtil.analyze(ints);
        }
        Log.d("dd", CaluUtil.mMap.toString());
        records.clear();
        for (int i = tempR.size() - 1; i >= 0; i--) {
            records.add(tempR.get(i));
        }

        double dayWin = 0;
        for (int i = records.size() - 1; i >= 0; i--) {
            Record record = records.get(i);
            dayWin += record.win;
            if (i > 0) {
                Record last = records.get(i - 1);
                if (!last.createTime.substring(0, 10).equals(record.createTime.substring(0, 10))) {
                    record.dayWin = dayWin;
                    if (record.dayWin > 0) {
                        dayWinCount++;
                    } else if (record.dayWin < 0) {
                        dayDefeatCount++;
                    }
                    dayWin = 0;
                }
            } else {
                record.dayWin = dayWin;
                if (record.dayWin > 0) {
                    dayWinCount++;
                } else if (record.dayWin < 0) {
                    dayDefeatCount++;
                }
            }
        }
        mAdapter.notifyDataSetChanged();

        double cart = 0;
        double winRate = 0;
        double dayWinRate = 0;
        if (winCount > 0 || defeatCount > 0) {
            double avg = win / (winCount + defeatCount);
            int sum = 0;
            for (Record record : records) {
                double oneWin = record.win;
                if (oneWin == 0) {
                    continue;
                }
                sum += (avg - oneWin) * (avg - oneWin);
            }
            cart = Math.sqrt(sum / (winCount + defeatCount));
            winRate = (double) winCount / (winCount + defeatCount);
        }
        if (dayWinCount > 0 || dayDefeatCount > 0) {
            dayWinRate = (double) dayWinCount / (dayWinCount + dayDefeatCount);
        }
        text.setText(new StringBuilder("总净胜：").append(DeviceUtil.m2(win))
            .append("，胜率：").append(DeviceUtil.m2p(winRate))
            .append("，日胜率：").append(DeviceUtil.m2p(dayWinRate))
            .append("，最多胜：").append(DeviceUtil.m2(oneMax))
            .append("，最多输：").append(DeviceUtil.m2(oneMin))
            .append("，峰值：").append(DeviceUtil.m2(totalMax))
            .append("，谷值：").append(DeviceUtil.m2(totalMin))
            .append("，标准差：").append(DeviceUtil.m2(cart)));
    }
}
