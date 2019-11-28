package com.app.expiteapp.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.app.expiteapp.notifications.NotificationManager;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ProductsService extends IntentService {
    Timer timer = new Timer();

    public ProductsService(){
        super("ExpiryApp_ProductsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Date now = new Date();
        now.setTime(TimeUnit.HOURS.toMillis(3));
        final Calendar cal = Calendar.getInstance();
        cal.setTime(now);
//        cal.add(Calendar.DATE, +1);
        cal.add(Calendar.MINUTE, 2);
        timer.scheduleAtFixedRate(new TimerTask() {

            synchronized public void run() {
                NotificationManager nm = new NotificationManager(getApplicationContext());
                nm.checkAllNotifications();
            }

        }, cal.getTime(), TimeUnit.MINUTES.toMillis(2));
//        TimeUnit.HOURS.toMillis(24)

//        NotificationManager nm = new NotificationManager(getApplicationContext());
//        nm.checkAllNotifications();
    }
}
