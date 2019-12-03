package com.app.expiteapp.services;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.app.expiteapp.R;
import com.app.expiteapp.notifications.NotificationManager;

public class ProductAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_SHORT).show();
        NotificationManager nm = new NotificationManager(context, intent);
        nm.checkAllNotifications();
    }
}
