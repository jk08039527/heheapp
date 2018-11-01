package com.jerry.moneyapp.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * Created by th on 2015/12/18  类说明：公共adapter
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDatas;

    public CommonAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mDatas = data;
        if (null == mDatas) {
            mDatas = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return null == mDatas ? 0 : mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return null == mDatas ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getDatas() {
        return mDatas;
    }
}
