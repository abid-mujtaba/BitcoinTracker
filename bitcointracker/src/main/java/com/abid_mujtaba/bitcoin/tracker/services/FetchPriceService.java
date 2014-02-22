package com.abid_mujtaba.bitcoin.tracker.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import static com.abid_mujtaba.bitcoin.tracker.Resources.Logd;
import static com.abid_mujtaba.bitcoin.tracker.Resources.Loge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


/**
 * This is the Service that runs in the background and fetches the Bitcoin prices from the coinbase
 * backend after every specified interval of time.
 */

public class FetchPriceService extends IntentService
{
    public FetchPriceService()
    {
        super("FetchPriceService");
    }


    @Override
    protected void onHandleIntent(Intent intent)
    {
        String data = get_btc_price();
        Logd("FetchPriceService: " + data);

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/bitcoin");
        dir.mkdirs();           // Make the directory along with all necessary parent directories if they don't already exist

        File file = new File(dir, "data.txt");

        String time = "" + System.currentTimeMillis() / 1000;

        try
        {
            FileOutputStream fos = new FileOutputStream(file, true);        // We pass in true so that the text is appended
            PrintWriter pw = new PrintWriter(fos);
            pw.println(time + "\n");
            pw.flush();
            pw.close();
            fos.close();
        }
        catch (FileNotFoundException e) { Loge("Error while writing to file.", e); }
        catch (IOException e) { Loge("Error while writing to file.", e); }
    }


    @Override
    public IBinder onBind(Intent intent) { return null; }


    private static final int INTERVAL = 10 * 1000;          // Interval of time in milliseconds between launching the service

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



    private String get_btc_price()
    {
        final String url = "https://coinbase.com/api/v1/prices/buy";

        HttpClient client = getHttpClient();
        HttpGet get = new HttpGet(url);

        long time = System.currentTimeMillis() / 1000;

        try
        {
            HttpResponse response = client.execute(get);

            return HttpResponseToString(response);
        }
        catch (IOException e) { Loge("GET failure.", e); }

        return "";
    }


    private static final int CONNECTION_TIMEOUT = 1000;
    private static final int SOCKET_TIMEOUT = 1000;

    private static HttpClient getHttpClient()       // Returns an HTTP client
    {
        HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);

        return new DefaultHttpClient(params);
    }


    private static String HttpResponseToString(HttpResponse response)
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
        catch (UnsupportedEncodingException e) { Loge("Error converting HttpResponse to String.", e); }
        catch (IOException e) { Loge("Error converting HttpResponse to String.", e); }

        return "";
    }
}
