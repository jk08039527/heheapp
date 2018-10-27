package com.jerry.moneyapp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    private static int START = 6;
    private static int WHOLEWIN2;
    private static int WHOLEWIN3;
    private static int GUDAOCOUNT2 = 6;
    private static int GUDAOCOUNT3 = 8;
    private static int GUDAOLINIT2 = 2;
    private static int GUDAOLINIT3 = 3;
    private static int LASTPOINTNUM = 15;
    private static int LASTWIN2 = -10;
    private static int LASTWIN3 = -10;

    private List<MyLog> mMyLogs = new ArrayList<>();
    private ArrayList<LinkedList<Record>> pointss = new ArrayList<>();
    private CommonAdapter<LinkedList<Record>> mAdapter;
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
        mAdapter = new CommonAdapter<LinkedList<Record>>(this, pointss) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = ViewHolder.get(mContext, convertView, R.layout.item_text);
                TextView date = holder.getView(R.id.date);
                TextView money = holder.getView(R.id.money);
                Record record = pointss.get(position).getLast();
                date.setText(record.date);
                double win = pointss.get(position).getLast().point.win;
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
        query.setLimit(500).findObjects(new FindListener<MyLog>() {
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
            LinkedList<Record> records = new LinkedList<>();
            LinkedList<Point> points = new LinkedList<>();
            LinkedList<Integer> integers = log.getData();
            int[] ints = new int[integers.size()];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = integers.get(i);
            }
            Point lastP = null;
            for (int j = 0; j < ints.length; j++) {
                Point point = calulate(ints, j + 1, points);
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
                if (points.size() >= LASTPOINTNUM) {
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
                        } else if (point.currentType == 3 && point.intention3 != GBData.VALUE_NONE) {
                            point.intention = point.intention3;
                        } else {
                            point.intention = GBData.VALUE_NONE;
                        }
                    } else {
                        point.intention = GBData.VALUE_NONE;
                    }
                }

                lastP = point;
                Record record = new Record();
                record.date = log.getCreatedAt();
                record.point = lastP;
                points.add(point);
                records.add(record);
            }
            pointss.add(records);
        }
        mAdapter.notifyDataSetChanged();
        double count = 0;
        for (LinkedList<Record> points : pointss) {
            count += points.getLast().point.win;
        }
        text.setText(DeviceUtil.m2(count));
    }

    Point calulate(int[] ints, int position, LinkedList<Point> points) {
        Point point = new Point();
        if (position > ints.length) {
            return point;
        }
        if (position > 0) {
            // 判断是否加倍
            ArrayList<Integer> paint = new ArrayList<>();
            int index = position - 1;
            int tempSize = 1;
            while (index >= 0) {
                if (index == 0) {
                    paint.add(tempSize);
                } else {
                    if (ints[index] == ints[index - 1]) {
                        tempSize++;
                    } else {
                        paint.add(tempSize);
                        tempSize = 1;
                    }
                }
                index--;
            }
            boolean good = false;
            if (paint.size() > 1 && paint.get(0) > 1 && paint.get(1) > 1 && paint.get(0) + paint.get(1) > 5) {
                point.multiple2 = 2;
                point.multiple3 = 2;
                good = true;
            } else if (paint.size() > 2 && paint.get(0) > 1 && paint.get(1) > 1 && paint.get(2) > 1 && paint.get(0) + paint.get(1) +
                    paint.get(2) > 6) {
                point.multiple2 = 2;
                point.multiple3 = 2;
                good = true;
            } else if (paint.size() > 2 && paint.get(0) == 1 && paint.get(1) == 1 && paint.get(2) == 1) {
                point.multiple2 = -1;
                point.multiple3 = -1;
            }
            ArrayList<Integer> tempList = new ArrayList<>();
            int temp = 0;
            for (int num : paint) {
                if (num == 1) {
                    temp++;
                } else {
                    tempList.add(temp);
                    temp = 0;
                }
            }
            if (tempList.size() == 2 && tempList.get(1) > 2) {
                point.manyGudao = true;
            } else if (tempList.size() > 2 && (tempList.get(1) > 2 || tempList.get(2) > 2)) {
                point.manyGudao = true;
            } else if (paint.size() > 5 && (paint.get(0) == 1 && paint.get(1) == 1 && paint.get(2) == 1) && (paint.get(3) > 1 && paint
                    .get(4) > 1 && paint.get(3) + paint.get(4) > 6)) {
                point.manyGudao = true;
            }

            if (!good) {
                int paintSize = paint.size();
                if (paintSize > 1 && paint.get(0) == 1) {
                    if (paintSize > 2 && paint.get(1) > 1) {
                        point.multiple2 = 2;
                    } else if (paintSize == 2) {
                        point.multiple2 = 2;
                    }
                }
                if (paintSize > 2 && paint.get(0) == 1 && paint.get(1) == 1) {
                    if (paintSize > 3 && paint.get(2) > 1) {
                        point.multiple3 = 2;
                    } else if (paintSize == 3) {
                        point.multiple3 = 2;
                    }
                }
            }
            // 记录当前数到第几个
            int gd = 0;
            // 记录当前索引
            int gdIndex = 0;
            int min = Math.min(GUDAOCOUNT2, position);
            while (gd < min && gdIndex < paint.size() - 1) {
                if (paint.get(gdIndex) == 1) {
                    point.gudao2++;
                }
                gd += paint.get(gdIndex);
                gdIndex++;
            }
            if (point.multiple2 > 0) {
                if (point.gudao2 >= GUDAOLINIT2) {
                    point.intention2 = GBData.VALUE_NONE;
                } else {
                    point.intention2 = ints[position - 1];
                }
            } else {
                point.intention2 = ints[position - 2];
            }
            // 记录当前数到第几个
            gd = 0;
            // 记录当前索引
            gdIndex = 0;
            min = Math.min(GUDAOCOUNT3, position);
            while (gd < min && gdIndex < paint.size() - 1) {
                if (paint.get(gdIndex) == 1) {
                    point.gudao3++;
                }
                gd += paint.get(gdIndex);
                gdIndex++;
            }
            if (point.multiple3 > 0) {
                if (point.gudao3 >= GUDAOLINIT3) {
                    point.intention3 = GBData.VALUE_NONE;
                } else {
                    point.intention3 = ints[position - 1];
                }
            } else {
                point.intention3 = ints[position - 2];
            }
        } else {
            point.intention2 = GBData.VALUE_LONG;
            point.intention3 = GBData.VALUE_LONG;
        }
        return point;
    }

    class Record {

        Point point = new Point();
        String date;
    }
}
