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
    private boolean fRunning = false;       // Flag that determines whether the service is already running


    public FetchPriceService()
    {
        super("FetchPriceService");
    }


    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (! fRunning)         // We perform these tasks only if the Service isn't already running.
        {
            fRunning = true;        // We set fRunning = true here which stops this code from being executed over and over again.

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/bitcoin");
            dir.mkdirs();           // Make the directory along with all necessary parent directories if they don't already exist

            File file = new File(dir, "data.txt");

            try
            {
                FileOutputStream fos = new FileOutputStream(file, true);        // We pass in true so that the text is appended
                PrintWriter pw = new PrintWriter(fos);
                pw.println("Hello, World\n");
                pw.flush();
                pw.close();
                fos.close();
            }
            catch (FileNotFoundException e) { Loge("Error while writing to file.", e); }
            catch (IOException e) { Loge("Error while writing to file.", e); }
        }
    }


    @Override
    public IBinder onBind(Intent intent) { return null; }
}
