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

package com.abid_mujtaba.bitcoin.tracker.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.abid_mujtaba.bitcoin.tracker.data.Data;
import com.abid_mujtaba.bitcoin.tracker.exceptions.DataException;
import com.abid_mujtaba.bitcoin.tracker.exceptions.NetworkException;
import static com.abid_mujtaba.bitcoin.tracker.Resources.Logd;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


/**
 * This is the Service that runs in the background and fetches the Bitcoin prices from the coinbase
 * backend after every specified interval of time.
 */

public class FetchPriceService extends IntentService
{
    private static final int INTERVAL = 5 * 60 * 1000;          // Interval of time in milliseconds between launching the service. (5 min)


    public FetchPriceService()
    {
        super("FetchPriceService");
    }


    @Override
    protected void onHandleIntent(Intent intent)
    {
        try
        {
            String data = get_btc_price();

            Data.append(data);
        }
        catch (NetworkException e) { e.log(); }
        catch (DataException e) { e.log(); }
    }


    @Override
    public IBinder onBind(Intent intent) { return null; }


    public static void start(Context context)               // Method used to start service via AlarmManager
    {
        Intent intent = new Intent(context, FetchPriceService.class);                      // Intent to launch service
        PendingIntent alarmIntent = PendingIntent.getService(context, 0, intent, 0);       // PendingIntent required by AlarmManager. This gives the AlarmManager permission to launch this Intent as if it were being launched by this application

        AlarmManager amgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        amgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL, alarmIntent);
    }


    public static void stop(Context context)                // Method used to stop service via AlarmManager
    {
        Intent intent = new Intent(context, FetchPriceService.class);                      // Intent to launch service
        PendingIntent alarmIntent = PendingIntent.getService(context, 0, intent, 0);       // PendingIntent required by AlarmManager. This gives the AlarmManager permission to launch this Intent as if it were being launched by this application

        AlarmManager amgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        amgr.cancel(alarmIntent);

        Logd("Stopping FetchPriceService.");
    }


    public static String get_btc_price() throws NetworkException
    {
        final String url = "https://www.bitstamp.net/api/ticker/";

        HttpClient client = getHttpClient();
        HttpGet get = new HttpGet(url);

        long time = System.currentTimeMillis() / 1000;
        float buy_price, sell_price;

        try
        {
            // Fetch buy price from coinbase backend
            HttpResponse response = client.execute(get);

            JSONObject jResponse = new JSONObject( HttpResponseToString(response) );
            buy_price = Float.parseFloat( jResponse.getString("ask") );
            sell_price = Float.parseFloat( jResponse.getString("bid") );

            return String.format("%d %.2f %.2f", time, buy_price, sell_price);
        }
        catch (IOException e) { throw new NetworkException("GET failure.", e); }
        catch (JSONException e) { throw new NetworkException("Failed to convert GET response to JSON.", e); }
    }


    private static final int CONNECTION_TIMEOUT = 1 * 60 * 1000;         // Timeouts before the app gives up and closes the socket and connection
    private static final int SOCKET_TIMEOUT = 1 * 60 * 1000;

    private static HttpClient getHttpClient()       // Returns an HTTP client
    {
        HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);

        return new DefaultHttpClient(params);
    }


    private static String HttpResponseToString(HttpResponse response) throws NetworkException
    {
        try
        {
            InputStream is = response.getEntity().getContent();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            br.close();

            return sb.toString();
        }
        catch (UnsupportedEncodingException e) { throw new NetworkException("Error converting HttpResponse to String.", e); }
        catch (IOException e) { throw new NetworkException("Error converting HttpResponse to String.", e); }
    }
}
