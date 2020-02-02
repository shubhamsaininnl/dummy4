package com.example.dummy4;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

public class NotificationHelper {

    public static void displayNotification(Context context, String title, String body)
    {
        NotificationCompat.Builder mBuilder=
                new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat= NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(1, mBuilder.build());
    }
}
