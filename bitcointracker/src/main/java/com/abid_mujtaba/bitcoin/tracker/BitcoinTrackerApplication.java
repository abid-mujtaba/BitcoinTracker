package com.abid_mujtaba.bitcoin.tracker;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.abid_mujtaba.bitcoin.tracker.services.FetchPriceService;

/**
 * Start point of the application. Used to carry out operations that must happen on startup
 */

public class BitcoinTrackerApplication extends Application
{
    private static final int INTERVAL = 30 * 1000;          // Interval of time in milliseconds between launching the service


    @Override
    public void onCreate()
    {
        super.onCreate();

        // We set the FetchPriceService to be run repeated after a fixed interval of time using an AlarmManager

        Intent intent = new Intent(this, FetchPriceService.class);                      // Intent to launch service
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);       // PendingIntent required by AlarmManager. This gives the AlarmManager permission to launch this Intent as if it were being launched by this application

        AlarmManager amgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        amgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL, alarmIntent);
    }
}