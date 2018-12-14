package com.jerry.moneyapp.ui;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.jerry.moneyapp.R;
import com.jerry.moneyapp.bean.GBData;
import com.jerry.moneyapp.util.DeviceUtil;

/**
 * Created by wzl on 2018/12/14.
 *
 * @Description
 */
public class DetailView extends View {

    private int count;
    private LinkedList<Integer> points;
    private Paint mPaint;
    private int fengC;
    private int longC;

    public DetailView(Context context, int count, LinkedList<Integer> points) {
        super(context);
        this.count = count;
        this.points = points;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        fengC = ContextCompat.getColor(context, R.color.red_primary);
        longC = ContextCompat.getColor(context, R.color.blue_primary);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (points == null || count == 0) {
            return;
        }
        final int radias = DeviceUtil.getDisplayWidth() / (count + 3) / 2;
        final int distance = radias * 2 + 2;
        int x = radias;
        int y = radias;
        int last = 0;
        for (int i = 0; i < points.size(); i++) {
            int temp = points.get(i);
            if (last == GBData.VALUE_NONE) {
                mPaint.setColor(temp == GBData.VALUE_FENG ? fengC : longC);
            } else {
                if (last == temp) {
                    y += distance;
                } else {
                    x += distance;
                    y = radias;
                    mPaint.setColor(temp == GBData.VALUE_FENG ? fengC : longC);
                }
            }
            canvas.drawCircle(x, y, radias, mPaint);
            last = temp;
        }
    }
}
