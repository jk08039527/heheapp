package com.jerry.moneyapp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AnalyzeActivity extends AppCompatActivity {

    private List<MyLog> mMyLogs = new ArrayList<>();
    private ArrayList<LinkedList<Record>> pointss = new ArrayList<>();
    private CommonAdapter<LinkedList<Record>> mAdapter;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        text = findViewById(R.id.text);
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
        query.findObjects(new FindListener<MyLog>() {
            @Override
            public void done(List<MyLog> list, BmobException e) {
                if (e != null) {
                    return;
                }
                for (MyLog log : list) {
                    LinkedList<Record> records = new LinkedList<>();
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
                        if (points.size() >= 15) {
                            point.award2 = point.win2 - points.get(points.size() - 15).win2;
                            point.award3 = point.win3 - points.get(points.size() - 15).win3;
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
                            double qiwang = ints.length * 0.8;
                            if (ints.length > 6 && point.award2 >= -10 && point.award3 >= -10 && point.win2 > qiwang && point.win3 >
                                    qiwang) {
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
                handleData();
            }
        });
    }

    private void handleData() {
        double count = 0;
        for (LinkedList<Record> points : pointss) {
            count += points.getLast().point.win;
        }
        text.setText(DeviceUtil.m2(count));
    }

    class Record {

        Point point = new Point();
        String date;
    }
}
