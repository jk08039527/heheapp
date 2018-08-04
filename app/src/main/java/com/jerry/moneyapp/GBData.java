package com.jerry.moneyapp;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import static com.jerry.moneyapp.MyService.ASSIABLEX;
import static com.jerry.moneyapp.MyService.ASSIABLEY;

public class GBData {

    private static final String TAG = "GBData";
    private static final int VALUE_MAX = 240;//阈值
    private static final int VALUE_MIN = 80;//阈值
    public static final int VALUE_FENG = 1;
    public static final int VALUE_LONG = 2;
    private static int peaceCount;
    public static ImageReader reader;

    /**
     * @param x
     * @param y
     * @return
     */
    public static void getCurrentData(int[] x, int[] y, LinkedList<Integer> list) {
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
        int assiableColor = bitmap.getPixel((ASSIABLEX + (int) (Math.random() * 10)), ASSIABLEY);
        int r = Color.red(assiableColor);
        int g = Color.green(assiableColor);
        int b = Color.blue(assiableColor);
        if (r + g + b < 150) {//46+49+52
            Log.w(TAG, "not assiable!");
            return;
        }

        int currentSize = list.size() + peaceCount;
        if (currentSize == 0) {
            for (int aX : x) {
                for (int aY : y) {
                    int color = bitmap.getPixel(aX, aY);
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    if (blue > VALUE_MAX && red < VALUE_MIN) {
                        list.add(VALUE_LONG);
                    } else if (red > VALUE_MAX && blue < VALUE_MIN) {
                        list.add(VALUE_FENG);
                    } else if (red + blue < 70 && green > 140) {
                        peaceCount++;
                    } else if (red > 215 && red < 220 && blue > 215 && blue < 220) {
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
        if (blue > VALUE_MAX && red < VALUE_MIN) {
            list.add(VALUE_LONG);
        } else if (red > VALUE_MAX && blue < VALUE_MIN) {
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