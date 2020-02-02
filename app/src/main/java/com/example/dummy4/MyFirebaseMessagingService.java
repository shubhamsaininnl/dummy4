package com.example.dummy4;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("myToken", s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification()!=null)
        {
            String title= remoteMessage.getNotification().getTitle();
            String body= remoteMessage.getNotification().getBody();

            NotificationHelper.displayNotification(getApplicationContext(),title, body);

        }
    }
}
