package com.abid_mujtaba.bitcoin.tracker;

import android.util.Log;

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
}
