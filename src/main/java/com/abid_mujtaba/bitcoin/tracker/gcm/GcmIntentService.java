package com.abid_mujtaba.bitcoin.tracker.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.abid_mujtaba.bitcoin.tracker.MainActivity;
import com.abid_mujtaba.bitcoin.tracker.R;

/**
 * IntentService called by the GcmBroadcastReceiver to actually handle the incoming message.
 */

public class GcmIntentService extends IntentService
{
    public GcmIntentService() { super("GcmIntentService"); }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        String msg = extras.getString("msg");

        sendNotification(msg);
    }


    private static final int NOTIFICATION_ID = 1;

    private void sendNotification(String msg)
    {
        NotificationManager mgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // We set a Pending Intent for what happens when the notification is pressed. We launch the main activity of the application.
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.bitcoin)
               .setContentTitle("Bitcoin Tracker")
               .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
               .setContentText(msg)
               .setAutoCancel(true)                         // Dismiss the notification when clicked
               .setDefaults(Notification.DEFAULT_ALL);      // Use all defaults (mostly for sound and vibration)

        builder.setContentIntent(intent);

        mgr.notify(NOTIFICATION_ID, builder.build());
    }
}
