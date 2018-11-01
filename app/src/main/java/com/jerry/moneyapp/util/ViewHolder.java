package com.jerry.moneyapp.util;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author sunsx 2015-8-24
 * @类说明 :ViewHolder封装类
 */
public class ViewHolder {

    private View convertView;
    /**
     * 预设容器size，默认大小是10
     */
    private SparseArray<View> views;

    private ViewHolder(Context context, int layoutId, int size) {
        views = new SparseArray<>(size);
        convertView = LayoutInflater.from(context).inflate(layoutId, null);
        convertView.setTag(this);
    }

    public static ViewHolder get(Context context, View convertView, int layoutId) {
        return get(context, convertView, layoutId, 10);
    }

    public static ViewHolder get(Context context, View convertView, int layoutId, int size) {
        if (convertView == null || convertView.getTag() == null) {
            return new ViewHolder(context, layoutId, size);
        }
        return (ViewHolder) convertView.getTag();
    }

    /**
     * 获取view
     */
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 填充FrameLayout
     *
     * @param frameId :父控件id
     * @param layoutId :填充布局id
     */
    public void addFrameLayout(Context context, int frameId, int layoutId) {
        FrameLayout fl = (FrameLayout) views.get(frameId);
        if (fl == null) {
            fl = convertView.findViewById(frameId);
            fl.addView(LayoutInflater.from(context).inflate(layoutId, null));
            views.put(frameId, fl);
        }
    }

    /**
     * merge合并
     */
    void mergeFrameLayout(Context context, int frameId, int layoutId) {
        FrameLayout fl = (FrameLayout) views.get(frameId);
        if (fl == null) {
            ViewGroup v = convertView.findViewById(frameId);
            LayoutInflater.from(context).inflate(layoutId, v, true);
            fl = convertView.findViewById(frameId);// 在convertview中刷新fl，然后放进SparseArray
            views.put(frameId, fl);
        }
    }

    /**
     * 获取convertView
     */
    public View getConvertView() {
        return convertView;
    }

}
