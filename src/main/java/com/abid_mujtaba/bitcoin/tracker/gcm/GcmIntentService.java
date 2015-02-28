package com.abid_mujtaba.bitcoin.tracker.gcm;

import android.app.IntentService;
import android.content.Intent;

import static com.abid_mujtaba.bitcoin.tracker.Resources.Logd;

/**
 * IntentService called by the GcmBroadcastReceiver to actually handle the incoming message.
 */

public class GcmIntentService extends IntentService
{
    public GcmIntentService() { super("GcmIntentService"); }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Logd("onHandleIntent for GCM message");
    }
}
