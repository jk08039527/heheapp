package com.jerry.moneyapp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

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
    public static final int VALUE_FENG = 1;
    public static final int VALUE_LONG = 2;
    public static ImageReader reader;

    /**
     * @param x
     * @param y
     * @return
     */
    public static int[] getCurrentData(int[] x, int[] y, int tempSize) {
        if (reader == null) {
            Log.w(TAG, "getColor: reader is null");
            return null;
        }

        Image image = reader.acquireLatestImage();

        if (image == null) {
            Log.w(TAG, "getColor: image is null");
            return null;
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
        Random random = new Random();
        int a = random.nextInt();
        int assiableColor = bitmap.getPixel((ASSIABLEX + (int) (Math.random() * 10)), ASSIABLEY);
        int r = Color.red(assiableColor);
        int g = Color.green(assiableColor);
        int b = Color.blue(assiableColor);
        if (r + g + b < 150) {//46+49+52
            Log.w(TAG, "not assiable!");
            return null;
        }

        ArrayList<Integer> intValues = new ArrayList<>();
        for (int aX : x) {
            for (int aY : y) {
                int color = bitmap.getPixel(aX, aY);
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                if (blue > VALUE_MAX) {
                    intValues.add(VALUE_LONG);
                    continue;
                } else if (red > VALUE_MAX) {
                    intValues.add(VALUE_FENG);
                    continue;
                } else if (red + blue < 70 && green > 140) {
                    continue;
                } else if (red > 215 && red < 220 && blue > 215 && blue < 220) {
                    break;
                }
                return null;
            }
        }
        if (tempSize != intValues.size()) {
            int[] ints = new int[intValues.size()];
            for (int i = 0; i < intValues.size(); i++) {
                ints[i] = intValues.get(i);
            }
            return ints;
        }
        return null;
    }
}