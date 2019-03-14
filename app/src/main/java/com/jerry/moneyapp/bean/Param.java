package com.jerry.moneyapp.bean;

/**
 * Created by wzl on 2019/3/14.
 *
 * @Description
 */
public class Param {

    public final static int STATE_WEEKEND = 1;
    public final static int STATE_WEEKDAY = 2;
    public final static int STOPCOUNT = 3;

    public static int START = 12;
    public static double WHOLEWIN21 = 15;
    public static double WHOLEWIN22 = 17;
    public static double WHOLEWIN3 = 6;
    public static int LASTPOINTNUM21 = 17;
    public static int LASTPOINTNUM22 = 1;
    public static double LASTWIN21 = -5;
    public static double LASTWIN22 = -10;
    public static int LASTPOINTNUM3 = 6;
    public static double LASTWIN3 = -21;
    public static double GIVEUPCOUNT1 = -53;
    public static double GIVEUPCOUNT2 = -42;

    public int start;
    public double wholewin2;
    public double wholewin3;
    public int lastpointnum2;
    public double lastwin2;
    public int lastpointnum3;
    public double lastwin3;
    public double giveupcount;

    public Param(int state) {
        if (STATE_WEEKEND == state) {
            start = START;
            wholewin2 = WHOLEWIN21;
            wholewin3 = WHOLEWIN3;
            lastpointnum2 = LASTPOINTNUM21;
            lastwin2 = LASTWIN21;
            lastpointnum3 = LASTPOINTNUM3;
            lastwin3 = LASTWIN3;
            giveupcount = GIVEUPCOUNT1;
        } else if (STATE_WEEKDAY == state) {
            start = START;
            wholewin2 = WHOLEWIN22;
            wholewin3 = WHOLEWIN3;
            lastpointnum2 = LASTPOINTNUM22;
            lastwin2 = LASTWIN22;
            lastpointnum3 = LASTPOINTNUM3;
            lastwin3 = LASTWIN3;
            giveupcount = GIVEUPCOUNT2;
        }
    }

}
