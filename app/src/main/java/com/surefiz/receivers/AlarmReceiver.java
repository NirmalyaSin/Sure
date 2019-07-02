package com.surefiz.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.surefiz.R;
import com.surefiz.screens.dashboard.DashBoardActivity;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int notifyId = intent.getIntExtra("notifyId",0);

        Intent notificationIntent = new Intent(context, DashBoardActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DashBoardActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notifyId, PendingIntent.FLAG_UPDATE_CURRENT);


        String channelId = context.getString(R.string.app_name);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        Notification notification = builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(intent.getStringExtra("notificationText"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notifyId, notification);
    }
}
