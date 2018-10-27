package com.jerry.moneyapp;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AnalyzeActivity extends AppCompatActivity {

    List<MyLog> mMyLogs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        BmobQuery<MyLog> query = new BmobQuery<>();
        query.findObjects(new FindListener<MyLog>() {
            @Override
            public void done(List<MyLog> list, BmobException e) {
                if (e != null) {
                    return;
                }
                mMyLogs.clear();
                mMyLogs.addAll(list);
                handleData();
            }
        });
    }

    private void handleData() {

    }
}
