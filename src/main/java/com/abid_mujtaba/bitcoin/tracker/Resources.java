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
