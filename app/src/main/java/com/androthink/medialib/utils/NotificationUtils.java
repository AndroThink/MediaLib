package com.androthink.medialib.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.androthink.medialib.R;

public class NotificationUtils {

    private static NotificationUtils notificationUtils = null;

    public static NotificationUtils getInstance(Context context){
        if(notificationUtils == null)
            notificationUtils = new NotificationUtils(context);

        return notificationUtils;
    }

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private String NOTIFICATION_CHANNEL_ID;

    private NotificationUtils(Context context){
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NOTIFICATION_CHANNEL_ID = context.getPackageName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "AndroThink App Notifications",
                    NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("AndroThink App Notification Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    public void Notify(Context context,String title,String text,String ticker) {
        notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.media_icon)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(text)
                .setContentInfo("Info");

        notificationManager.notify(/*notification id*/1, notificationBuilder.build());
    }

    public void MediaNotification(Context context,MediaModel mediaModel,Intent intent) {
        notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.layout_media_control_notification);

        // Set Icon
        notificationBuilder.setSmallIcon(R.drawable.media_icon)
                // Set Ticker Message
                .setTicker(mediaModel.getTitle())
                // Dismiss Notification
                .setAutoCancel(true)
                // Set RemoteViews into Notification
                .setContent(remoteViews);

        if (intent != null) {
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            // Set PendingIntent into Notification
            notificationBuilder.setContentIntent(pIntent);
        }

        if(mediaModel.getPicture() != null) {
            // Locate and set the Image into customnotificationtext.xml ImageViews
            remoteViews.setImageViewBitmap(R.id.image, BitmapFactory.decodeByteArray(mediaModel.getPicture(),
                    0, mediaModel.getPicture().length));
        }

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.title,mediaModel.getTitle());
        remoteViews.setTextViewText(R.id.text,mediaModel.getAlbum() + " , " +
                mediaModel.getArtist() + " , " + mediaModel.getDuration());

        // Build Notification with Notification Manager
        notificationManager.notify(0, notificationBuilder.build());
    }

    public void CustomNotification(Context context, String title, String text, String ticker,int icon,
                                   Intent intent,RemoteViews remoteViews) {
        notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        // Set Icon
        notificationBuilder.setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setTicker(ticker)
                .setAutoCancel(true)
                .setContent(remoteViews);

        if (intent != null) {
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // Set PendingIntent into Notification
            notificationBuilder.setContentIntent(pIntent);
        }

        // Build Notification with Notification Manager
        notificationManager.notify(0, notificationBuilder.build());
    }

}
