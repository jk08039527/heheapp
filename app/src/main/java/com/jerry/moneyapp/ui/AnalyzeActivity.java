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
    public static int LASTPOINTNUM2 = 14;
    public static double LASTWIN2 = -10.7;
    public static int LASTPOINTNUM3 = 19;
    public static double LASTWIN3 = -8;
    public static double GIVEUPCOUNT = -42;
    public static double GIVEUPCOUNTX = -42;
    public static double GIVEUPCOUNTS = -61;
    public static int STOPCOUNT = 4;
    public static int STOPCOUNTX = 5;

    private List<MyLog> mMyLogs = new ArrayList<>();
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
        EditText giveUpCount = findViewById(R.id.give_up_count);
        giveUpCount.setText(String.valueOf(GIVEUPCOUNT));
        giveUpCount.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GIVEUPCOUNT = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText giveUpCountX = findViewById(R.id.give_up_countx);
        giveUpCountX.setText(String.valueOf(GIVEUPCOUNTX));
        giveUpCountX.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GIVEUPCOUNTX = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText giveUpCountS = findViewById(R.id.give_up_counts);
        giveUpCountS.setText(String.valueOf(GIVEUPCOUNTS));
        giveUpCountS.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GIVEUPCOUNTS = ParseUtil.parseDouble(s.toString());
                updateData();
            }
        });
        EditText stopCount = findViewById(R.id.stop_count);
        stopCount.setText(String.valueOf(STOPCOUNT));
        stopCount.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                STOPCOUNT = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        EditText stopCountx = findViewById(R.id.stop_countx);
        stopCountx.setText(String.valueOf(STOPCOUNTX));
        stopCountx.addTextChangedListener(new MyTextWatcherListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                STOPCOUNTX = ParseUtil.parseInt(s.toString());
                updateData();
            }
        });
        mPtrRecyclerView = findViewById(R.id.ptrRecyclerView);
        mAdapter = new BaseRecyclerAdapter<Record>(this, records) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final Record bean) {
                TextView date = holder.getView(R.id.date);
                TextView money = holder.getView(R.id.money);
                TextView daymoney = holder.getView(R.id.daymoney);
                Record record = records.get(position);
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
        records.clear();
        double win = 0;
        double oneMax = -99999;
        double oneMin = 99999;
        double totalMax = -99999;
        double totalMin = 99999;
        int winCount = 0;//胜场数
        int defeatCount = 0;//负场数
        int dayWinCount = 0;//负场数
        int dayDefeatCount = 0;//负场数
        for (MyLog log : mMyLogs) {
            LinkedList<Integer> integers = log.getData();
            LinkedList<Integer> paint = new LinkedList<>();
            LinkedList<Point> points = new LinkedList<>();
            int[] ints = new int[integers.size()];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = integers.get(i);
            }
            Point lastP = null;
            int stopCount = 0;
            int stopCountx = 0;
            for (int j = 0; j < ints.length; j++) {
                Point point = CaluUtil.calulate(ints, j + 1, points);
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
                    if (lastP.intentionn != GBData.VALUE_NONE) {
                        if (lastP.intentionn == point.current) {
                            point.winn = lastP.winn + 9.7 * Math.abs(lastP.multiplen);
                            stopCount = 0;
                        } else {
                            point.winn = lastP.winn - 10 * Math.abs(lastP.multiplen);
                            stopCount++;
                        }
                    } else {
                        point.winn = lastP.winn;
                    }
                    if (lastP.intentionX != GBData.VALUE_NONE) {
                        if (lastP.intentionX == point.current) {
                            point.winX = lastP.winX + 9.7;
                            if (lastP.state == 0) {
                                point.state = 1;
                            } else if (lastP.state == 2) {
                                point.state = 2;
                            }
                            stopCountx = 0;
                        } else {
                            point.winX = lastP.winX - 10;
                            if (lastP.state == 0) {
                                point.state = 2;
                            } else if (lastP.state == 2) {
                                point.state = 1;
                            }
                            stopCountx++;
                        }
                    } else {
                        point.winX = lastP.winX;
                        point.state = lastP.state;
                    }
                    if (lastP.intention != GBData.VALUE_NONE) {
                        if (lastP.intention == point.current) {
                            point.win = lastP.win + 9.7 * Math.abs(lastP.multiple);
                        } else {
                            point.win = lastP.win - 10 * Math.abs(lastP.multiple);
                        }
                    } else {
                        point.win = lastP.win;
                    }
                } else {
                    paint.add(1);
                }
                if (point.winn > GIVEUPCOUNT && stopCount < STOPCOUNT) {
                    if (LASTPOINTNUM2 > 0 && points.size() >= LASTPOINTNUM2) {
                        point.award2 = point.win2 - points.get(points.size() - LASTPOINTNUM2).win2;
                    } else {
                        point.award2 = point.win2;
                    }
                    if (LASTPOINTNUM3 > 0 && points.size() >= LASTPOINTNUM3) {
                        point.award3 = point.win3 - points.get(points.size() - LASTPOINTNUM3).win3;
                    } else {
                        point.award3 = point.win3;
                    }
                    if (point.award2 >= point.award3) {
                        point.currentType = 2;
                    } else {
                        point.currentType = 3;
                    }
                    if (lastP != null) {
                        if (j > START && point.award2 >= LASTWIN2 && point.award3 >= LASTWIN3
                                && point.win2 > WHOLEWIN2 && point.win3 > WHOLEWIN3) {
                            if (point.currentType == 2 && point.intention2 != GBData.VALUE_NONE) {
                                point.intentionn = point.intention2;
                                point.multiplen = point.multiple2;
                            } else if (point.currentType == 3 && point.intention3 != GBData.VALUE_NONE) {
                                point.intentionn = point.intention3;
                                point.multiplen = point.multiple3;
                            } else {
                                point.intentionn = GBData.VALUE_NONE;
                            }
                        } else {
                            point.intentionn = GBData.VALUE_NONE;
                        }
                        if (point.multiplen > 1 && point.winn - 10 * point.multiplen < GIVEUPCOUNT) {
                            point.multiplen = 1;
                        }
                    }
                }

                if (point.winX > GIVEUPCOUNTX && stopCountx < STOPCOUNTX) {
                    if (point.state == 0 && paint.size() > 1 && paint.get(paint.size() - 1) == 1 && paint.get(paint.size() - 2) > 1) {
                        point.intentionX = point.current;
                    } else if (point.state == 1 && paint.size() > 1 && paint.get(paint.size() - 2) == 1) {
                        point.state = 0;
                    } else if (point.state == 2 && paint.size() > 1 && paint.get(paint.size() - 1) == 1 && paint.get(paint.size() - 2) > 1) {
                        point.intentionX = point.current == GBData.VALUE_LONG ? GBData.VALUE_FENG : GBData.VALUE_LONG;
                    }
                }
                if (point.intentionn != GBData.VALUE_NONE && point.intentionX != GBData.VALUE_NONE) {
                    point.intention = point.intentionn;
                    if (point.intentionn == point.intentionX) {
                        point.multiple = point.multiplen + 1;
                    } else {
                        point.multiple = point.multiplen - 1;
                    }
                } else {
                    point.intention = point.intentionn + point.intentionX;
                    if (point.intentionn == GBData.VALUE_NONE) {
                        point.multiple = 1;
                    } else {
                        point.multiple = point.multiplen;
                    }
                }
                if (point.multiple == 0) {
                    point.intention = 0;
                } else if (point.multiple > 1 && point.win - 10 * point.multiple < GIVEUPCOUNTS) {
                    point.multiple = 1;
                }
                lastP = point;
                points.add(point);
            }

            Record record = new Record();
            record.win = lastP.win;
            record.createTime = log.getCreatedAt();
            records.add(record);

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
            for (Point point : points) {
                if (point.win > totalMax) {
                    totalMax = point.win;
                }
                if (point.win < totalMin) {
                    totalMin = point.win;
                }
            }
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

    class Record {

        String createTime;
        double win;
        double dayWin;
    }
}
