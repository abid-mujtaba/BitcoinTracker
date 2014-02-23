package com.abid_mujtaba.bitcoin.tracker;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class containing all resources shared between various components of the application
 */

public class Resources
{
    private static final String LOGTAG = "BTC_TRKR";


    public static void Logd(String msg)
    {
        Log.d(LOGTAG, msg);
    }

    public static void Loge(String msg, Throwable throwable)
    {
        Log.e(LOGTAG, msg, throwable);
    }


    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd kk:mm");


    public static String time()          // Returns current time in human-readable form
    {
        long time = System.currentTimeMillis();

        return dateFormatter.format( new Date(time) );
    }
}
