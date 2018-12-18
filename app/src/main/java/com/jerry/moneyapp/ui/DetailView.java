package com.jerry.moneyapp.ui;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.jerry.moneyapp.R;
import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.bean.Point;
import com.jerry.moneyapp.util.DeviceUtil;

/**
 * Created by wzl on 2018/12/14.
 *
 * @Description
 */
public class DetailView extends View {

    /**
     * 数据集数量
     */
    private int count;
    /**
     * 原始数据
     */
    private LinkedList<Point> points;
    private Paint mPaint;
    private int fengC;
    private int longC;
    private int victoryC;
    private int defeatC;

    public DetailView(Context context, int count, LinkedList<Point> points) {
        super(context);
        this.count = count;
        this.points = points;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        fengC = ContextCompat.getColor(context, R.color.red_primary);
        longC = ContextCompat.getColor(context, R.color.blue_primary);
        victoryC = ContextCompat.getColor(context, R.color.white);
        defeatC = ContextCompat.getColor(context, R.color.black);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (points == null || count == 0) {
            return;
        }
        final int radias = DeviceUtil.getDisplayWidth() / (count + 3) / 2;
        final int inRaias = radias - 10;
        final int distance = radias * 2 + 2;
        int x = radias;
        int y = radias;
        Point last = null;
        for (int i = 0; i < points.size(); i++) {
            Point temp = points.get(i);
            if (last != null) {
                if (last.current == temp.current) {
                    y += distance;
                } else {
                    x += distance;
                    y = radias;
                }
            }
            mPaint.setColor(temp.current == GBData.VALUE_FENG ? fengC : longC);
            canvas.drawCircle(x, y, radias, mPaint);
            if (last != null && last.intention != GBData.VALUE_NONE) {
                if (last.intention == temp.current) {
                    mPaint.setColor(victoryC);
                } else {
                    mPaint.setColor(defeatC);
                }
                canvas.drawCircle(x, y, inRaias, mPaint);
            }
            last = temp;
        }
    }
}
