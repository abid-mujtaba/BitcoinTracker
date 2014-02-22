package com.abid_mujtaba.bitcoin.tracker;

import android.app.Application;

import com.abid_mujtaba.bitcoin.tracker.services.FetchPriceService;

/**
 * Start point of the application. Used to carry out operations that must happen on startup
 */

public class BitcoinTrackerApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // We set the FetchPriceService to be run repeated after a fixed interval of time using an AlarmManager
        FetchPriceService.start(this);
    }
}