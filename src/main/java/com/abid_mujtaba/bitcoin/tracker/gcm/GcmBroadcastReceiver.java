package com.abid_mujtaba.bitcoin.tracker.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Receiver that handles incoming GCM messages.
 *
 * Source: https://developer.android.com/google/gcm/client.html
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());

        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
