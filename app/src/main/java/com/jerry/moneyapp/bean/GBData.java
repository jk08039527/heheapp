package com.jerry.moneyapp.bean;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import com.jerry.moneyapp.MyService;

public class GBData {

    private static final String TAG = "GBData";
    private static final int VALUE_MAX = 235;//阈值
    private static final int MIN1 = 55;//阈值1
    public static final int VALUE_NONE = 0;
    public static final int VALUE_FENG = 1;
    public static final int VALUE_LONG = 2;
    public static ImageReader reader;

    /**
     * @param x
     * @param y
     * @return
     */
    public static boolean getCurrentData(int[] x, int[] y, LinkedList<Integer> list) {
        if (reader == null) {
            Log.w(TAG, "getColor: reader is null");
            return false;
        }

        Image image = reader.acquireLatestImage();

        if (image == null) {
            Log.w(TAG, "getColor: image is null");
            return false;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();
        int enterColor = bitmap.getPixel(MyService.MIDDELX, MyService.JUDGEY);
        int r = Color.red(enterColor);
        int gg = Color.green(enterColor);
        int b = Color.blue(enterColor);
        if (r < 50 && gg < 50 && b < 50) {
            Log.w(TAG, "not assiable!");
            return true;
        }
        int assiableColor = bitmap.getPixel(MyService.ASSIABLEX, MyService.ASSIABLEY);
        int g = Color.green(assiableColor);
        if (g < 240) {
            Log.w(TAG, "not assiable!");
            return false;
        }
        list.clear();
        for (int aX : x) {
            for (int aY : y) {
                int color = bitmap.getPixel(aX, aY);
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                if (blue > VALUE_MAX && red > MIN1 && red < 200) {
                    list.add(VALUE_LONG);
                } else if (red > VALUE_MAX && blue > MIN1 && blue < 200) {
                    list.add(VALUE_FENG);
                } else if (red + blue < 100 && green > 140) {
                    list.add(VALUE_NONE);
                } else if (red > 215 && green > 215 && blue > 215) {
                    return false;
                } else {
                    list.clear();
                    return false;
                }
            }
        }
        return false;
    }
}