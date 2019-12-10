package com.google.price.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.ml.md.R;
import com.google.price.PriceHistory;

public class NotificationHandler {
    private static final int NOTIFICATION_ID = 1;
    private static final int PENDING_INTENT_ID = 2;
    private static String CHANNEL_ID = "price_drop";
    private static String textTitle = "Price drop";
    private static String textContent = "The price of some selected items was reduced";
    private static String bigTextContent = "Click on highlighted item to see up-to-date price";

    public static void priceDropedNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Sets the channel for the notification
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Price is Dropped";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String description = "Description";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
        }
        //The notification itself
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
//                .setLargeIcon(largeIcon(context))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigTextContent))
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        //Set priority for older APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        //Calls the Notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    public static PendingIntent contentIntent(Context context) {
        Intent gotoApp = new Intent(context, PriceHistory.class);
        return PendingIntent.getActivity(context,
                PENDING_INTENT_ID,
                gotoApp,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.product_img);
        return largeIcon;
    }
}
