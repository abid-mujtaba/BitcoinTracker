package com.abid_mujtaba.bitcoin.tracker.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import static com.abid_mujtaba.bitcoin.tracker.Resources.Logd;
import static com.abid_mujtaba.bitcoin.tracker.Resources.Loge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


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
        Logd("FetchPriceService - onHandleIntent");

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
}
