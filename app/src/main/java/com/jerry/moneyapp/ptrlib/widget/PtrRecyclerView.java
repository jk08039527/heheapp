package com.jerry.moneyapp.ptrlib.widget;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.jerry.moneyapp.R;
import com.jerry.moneyapp.ptrlib.OnLoadMoreListener;
import com.jerry.moneyapp.ptrlib.OnRefreshListener;
import com.jerry.moneyapp.ptrlib.PtrDefaultHandler;
import com.jerry.moneyapp.ptrlib.PtrFrameLayout;
import com.jerry.moneyapp.ptrlib.header.PtrSimpleHeader;
import com.jerry.moneyapp.util.WeakHandler;
/**
 * Created by wzl on 2018/8/10.
 *
 * @Description 类说明:RecyclerView刷新封装
 */
public class PtrRecyclerView extends FrameLayout {

    private static final int REFRESH_LOADING_TIME = 300;
    protected RecyclerView mRecyclerView;
    protected BaseRecyclerAdapter mAdapter;
    private View mFooterView;
    protected PtrFrameLayout mPtrFrameLayout;
    protected WeakHandler mWeakHandler;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    public boolean canRefresh = true;

    public PtrRecyclerView(Context context) {
        this(context, null);
    }

    public PtrRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerView getRefreshableView() {
        return mRecyclerView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        View.inflate(getContext(), R.layout.refresh_recyclerview, this);
        mPtrFrameLayout = findViewById(R.id.ptrFrameLayout);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        PtrSimpleHeader mPtrSimpleHeader = new PtrSimpleHeader(getContext());
        mPtrFrameLayout.setHeaderView(mPtrSimpleHeader);
        mPtrFrameLayout.addPtrUIHandler(mPtrSimpleHeader);
        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return canRefresh && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        mRecyclerView.setOnTouchListener((v, event) -> mPtrFrameLayout.isRefreshing());
    }

    public void setAdapter(BaseRecyclerAdapter adapter) {
        setAdapterOnLoadMore(adapter, null);
    }

    public RecyclerView.LayoutManager initLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public void setAdapterOnLoadMore(BaseRecyclerAdapter adapter, OnLoadMoreListener listener) {
        mLayoutManager = initLayoutManager();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = adapter;
        mOnLoadMoreListener = listener;
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mOnLoadMoreListener != null) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        int mLastItemVisible = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        if (!mPtrFrameLayout.isRefreshing() && mAdapter.getItemCount() > 0 && mLastItemVisible == mAdapter.getItemCount()
                                - 1) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                    }
                }
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                    View currentFocus = ((Activity) getContext()).getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                }
            }

        });
    }

    public void scrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    public void autoRefresh() {
        mPtrFrameLayout.autoRefresh();
    }

    public void setPrtBgColor(@ColorRes int color) {
        mPtrFrameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), color));
    }

    public void onRefreshComplete() {
        if (mWeakHandler == null) {
            mWeakHandler = new WeakHandler();
        }
        mWeakHandler.postDelayed(() -> {
            if (mPtrFrameLayout != null) {
                mPtrFrameLayout.refreshComplete();
            }
        }, REFRESH_LOADING_TIME);
    }

    public void disableWhenHorizontalMove(boolean disable) {
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.disableWhenHorizontalMove(disable);
        }
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.mOnRefreshListener = refreshListener;
    }

}
