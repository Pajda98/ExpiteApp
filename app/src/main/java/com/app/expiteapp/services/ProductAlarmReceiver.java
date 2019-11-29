package com.app.expiteapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.expiteapp.notifications.NotificationManager;

public class ProductAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager nm = new NotificationManager(context);
        nm.checkAllNotifications();
    }
}
