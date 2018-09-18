package com.jerry.moneyapp;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

public class GBData {

    private static final String TAG = "GBData";
    private static final int VALUE_MAX = 235;//阈值
    private static final int MIN1 = 55;//阈值1
    private static final int MIN2 = 65;//阈值2
    public static final int VALUE_NONE = 0;
    public static final int VALUE_FENG = 1;
    public static final int VALUE_LONG = 2;
    private static int peaceCount;//和
    public static ImageReader reader;

    /**
     * @param x
     * @param y
     * @return
     */
    public static void getCurrentData(int[] x, int[] y, LinkedList<Integer> list, boolean focusUpdate) {
        if (reader == null) {
            Log.w(TAG, "getColor: reader is null");
            return;
        }

        Image image = reader.acquireLatestImage();

        if (image == null) {
            Log.w(TAG, "getColor: image is null");
            return;
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
        int assiableColor = bitmap.getPixel(MyService.ASSIABLEX, MyService.ASSIABLEY);
        int g = Color.green(assiableColor);
        if (g < 240) {
            Log.w(TAG, "not assiable!");
            return;
        }

        int currentSize = list.size() + peaceCount;
        if (currentSize == 0 || focusUpdate) {
            list.clear();
            for (int aX : x) {
                for (int aY : y) {
                    int color = bitmap.getPixel(aX, aY);
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    if (blue > VALUE_MAX && red > MIN1) {
                        list.add(VALUE_LONG);
                    } else if (red > VALUE_MAX && blue > MIN1) {
                        list.add(VALUE_FENG);
                    } else if (red + blue < 100 && green > 220) {
                        peaceCount++;
                    } else if (red > 215 && green > 215 && blue > 215) {
                        return;
                    } else {
                        peaceCount = 0;
                        list.clear();
                        return;
                    }
                }
            }
            return;
        }


        int cXIndex = currentSize / MyService.COUNTY;
        int cYIndex = currentSize % MyService.COUNTY;

        int color = bitmap.getPixel(x[cXIndex], y[cYIndex]);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        if (blue > VALUE_MAX && red > MIN1 && red < MIN2) {
            list.add(VALUE_LONG);
        } else if (red > VALUE_MAX && blue > MIN1 && blue < MIN2) {
            list.add(VALUE_FENG);
        } else if (red + blue < 70 && green > 140) {
            peaceCount++;
        } else if (red > 215 && red < 220 && blue > 215 && blue < 220) {
            int color1 = bitmap.getPixel(x[0], y[0]);
            int red1 = Color.red(color1);
            int blue1 = Color.blue(color1);
            if (red1 > 215 && red1 < 220 && blue1 > 215 && blue1 < 220) {
                peaceCount = 0;
                list.clear();
            }
        }
    }
}