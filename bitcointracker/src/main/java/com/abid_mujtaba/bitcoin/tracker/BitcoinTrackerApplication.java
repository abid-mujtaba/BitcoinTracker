/*
 *  Copyright 2014 Abid Hasan Mujtaba
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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