package com.app.expiteapp.notifications;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.app.expiteapp.MainActivity;
import com.app.expiteapp.R;
import com.app.expiteapp.database.ExpiryDbHelper;
import com.app.expiteapp.models.LVPItem;
import com.app.expiteapp.models.ListViewProduct;
import com.app.expiteapp.models.ProductGroupLevels;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    String CHANNEL_ID;
    Context context;
    Intent intent;

    public NotificationManager(Context context, Intent intent){
        this.context = context;
        this.intent = intent;

    }

    public void checkAllNotifications(){
        try {

            ExpiryDbHelper DB_HELPER = new ExpiryDbHelper(context);
            ProductGroupLevels levels = new ProductGroupLevels(context);
            List<ListViewProduct> products = ListViewProduct.getList(DB_HELPER.getReadableDatabase());
            int position = 0;
            for (final ListViewProduct product: products) {
                if(!ProductGroupLevels.InDelayCategory(product, levels.expiryDelays[0]) && ProductGroupLevels.InDelayCategory(product, levels.expiryDelays[1])) {
                    position++;
                    createNotification(
                            product.Name,
                            String.format(context.getResources().getString(R.string.notification_big_text), product.Name, product.ExpiryDate),
                            position
                            );
                }
            }
        }catch(Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void createNotification(String text, String bigText,int id){
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, "notify_001")
                .setSmallIcon(R.drawable.ic_stat_expiryapp_logo)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(text)

                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigText))

                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }


// notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, builder.build());

    }
}
