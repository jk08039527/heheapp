package com.jerry.moneyapp.ptrlib.widget;

import com.jerry.moneyapp.ptrlib.OnRefreshListener;
import com.jerry.moneyapp.ptrlib.PtrDefaultHandler;
import com.jerry.moneyapp.ptrlib.PtrFrameLayout;
import com.jerry.moneyapp.ptrlib.header.PtrSimpleHeader;
import com.jerry.moneyapp.util.WeakHandler;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by wzl on 2018/8/10.
 *
 * @Description 类说明:PtrSimpleView刷新封装
 */
public class PtrSimpleView extends PtrFrameLayout {

    public static final int REFRESH_LOADING_TIME = 300;
    private WeakHandler mWeakHandler;
    private PtrDefaultHandler mDefaultHandler;

    public PtrSimpleView(Context context) {
        this(context, null);
    }

    public PtrSimpleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        PtrSimpleHeader mPtrSimpleHeader = new PtrSimpleHeader(getContext());
        mDefaultHandler = new PtrDefaultHandler();
        setHeaderView(mPtrSimpleHeader);
        addPtrUIHandler(mPtrSimpleHeader);
        setPtrHandler(mDefaultHandler);
    }

    public void onRefreshComplete() {
        if (mWeakHandler == null) {
            mWeakHandler = new WeakHandler();
        }
        mWeakHandler.postDelayed(this::refreshComplete, REFRESH_LOADING_TIME);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mDefaultHandler.setOnRefreshListener(listener);
    }
}
