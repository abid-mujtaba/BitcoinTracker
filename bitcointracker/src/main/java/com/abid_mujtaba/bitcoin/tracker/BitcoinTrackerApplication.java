package com.abid_mujtaba.bitcoin.tracker;

import android.app.Application;
import android.content.Intent;

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

        // We launch the FetchPriceService

        Intent intent = new Intent(this, FetchPriceService.class);
        startService(intent);
    }
}