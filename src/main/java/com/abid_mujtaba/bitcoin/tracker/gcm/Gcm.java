package com.abid_mujtaba.bitcoin.tracker.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import static com.abid_mujtaba.bitcoin.tracker.Resources.Logd;
import static com.abid_mujtaba.bitcoin.tracker.Resources.Loge;

/**
 * Handles the registration of the Device with the GCM service.
 */

public class Gcm
{
    private final static String SENDER_ID = "843891651127";
    private final static String STORAGE = "BitcoinTracker Storage";
    private final static String REG_ID = "REG_ID";

    public static String getRegistrationId(Context context)
    {
        final SharedPreferences prefs = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        return prefs.getString(REG_ID, null);
    }


    public static void register(Context context)
    {
        new RegisterTask(context).execute();
    }


    private static class RegisterTask extends AsyncTask<Void, Void, Void>
    {
        Context mContext;

        public RegisterTask(Context context) { mContext = context; }


        @Override
        protected Void doInBackground(Void... voids)
        {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);

            try
            {
                String reg_id = gcm.register(SENDER_ID);
                Logd("GCM Registration ID fetched: " + reg_id);

                // Store the registration id in SharedPreferences so that we won't have to register it again.
                final SharedPreferences prefs = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString(REG_ID, reg_id);
                editor.apply();
            }
            catch (IOException e) { Loge("Failed to register with GCM.", e); }

            return null;
        }
    }
}
