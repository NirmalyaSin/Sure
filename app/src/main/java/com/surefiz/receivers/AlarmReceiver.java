package com.surefiz.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.surefiz.R;
import com.surefiz.screens.dashboard.DashBoardActivity;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int notifyId = intent.getIntExtra("notifyId", 0);

        //createNotification(context, notifyId, intent.getStringExtra("notificationText"));

        Intent notificationIntent = new Intent(context, DashBoardActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DashBoardActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notifyId, PendingIntent.FLAG_UPDATE_CURRENT);


        String channelId = context.getString(R.string.app_name);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = builder.setContentTitle(context.getString(R.string.app_name)+" Reminder")
                .setContentText(intent.getStringExtra("notificationText"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notifyId, notification);
    }


    /*public void createNotification(Context context, int notifyId, String aMessage) {

        NotificationManager notifManager;

        //final int NOTIFY_ID = 0; // ID of notification
        String Channel_id = context.getString(R.string.app_name); // default_channel_id
        String title = context.getString(R.string.app_name); // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(Channel_id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(Channel_id, title, importance);
                mChannel.enableVibration(false);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, Channel_id);
            intent = new Intent(context, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, notifyId, intent, 0);
            builder.setContentTitle(context.getString(R.string.app_name))                            // required
                    .setSmallIcon(R.mipmap.ic_launcher)   // required
                    .setContentText(aMessage) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage);
        } else {
            builder = new NotificationCompat.Builder(context, Channel_id);
            intent = new Intent(context, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(context.getString(R.string.app_name))                            // required
                    .setSmallIcon(R.mipmap.ic_launcher)   // required
                    .setContentText(aMessage) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(notifyId, notification);
    }*/
}
