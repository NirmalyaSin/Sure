package com.surefiz.fcm;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.surefiz.R;
import com.surefiz.application.MyApplicationClass;
import com.surefiz.screens.SplashActivity;
import com.surefiz.screens.accountability.AcountabilityActivity;
import com.surefiz.screens.bmidetails.BMIDetailsActivity;
import com.surefiz.screens.chat.ChatActivity;
import com.surefiz.screens.chat.model.Conversation;
import com.surefiz.screens.notifications.NotificationActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private MyApplicationClass myApplicationClass;

    private static final String TAG = "MyFirebaseMsgService";
    private JSONObject jObject;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        LoginShared.setWeightFromNotification(this, "1");
        myApplicationClass = (MyApplicationClass) getApplication();
        Log.d(TAG, "FromDataPush: " + remoteMessage.getData());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            componentInfo.getPackageName();
            JSONObject jsonObject = null;
            jObject = null;
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                try {
                    jsonObject = new JSONObject(remoteMessage.getData().toString());
                    jObject = jsonObject.getJSONObject("pushData");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jObject.optInt("pushType") == 2) {
                    if (taskInfo.get(0).topActivity.getClassName().equals("com.surefiz.screens.chat.ChatActivity")) {
                        Conversation conversation = new Conversation();
                        conversation.setDateTime(jObject.optString("dateTime"));
                        conversation.setMessage(jObject.optString("message"));
                        conversation.setMessageFrom(jObject.optString("messageFrom"));
                        conversation.setReciverId(Integer.parseInt(jObject.optString("receiverId")));
                        conversation.setSenderId(Integer.parseInt(jObject.optString("senderId")));
                        myApplicationClass.chatListNotification.add(conversation);
                        LoginShared.setWeightFromNotification(this, "2");
                    }
                } else if (jObject.optInt("pushType") == 3) {
                    LoginShared.setWeightFromNotification(this, "3");
                } else if (jObject.optInt("pushType") == 4) {
                    LoginShared.setWeightFromNotification(this, "4");
                }else if (jObject.optInt("pushType") == 5) {
                    LoginShared.setWeightFromNotification(this, "5");
                } else {
                    LoginShared.setWeightFromNotification(this, "1");
                }
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            componentInfo.getPackageName();
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            if (!taskInfo.get(0).topActivity.getClassName().equals("com.surefiz.screens.chat.ChatActivity")) {
                showNotification(remoteMessage.getNotification(), remoteMessage.getData());
            }else if (!taskInfo.get(0).topActivity.getClassName().equals("com.surefiz.screens.bmidetails.BMIDetailsActivity")) {
                showNotification(remoteMessage.getNotification(), remoteMessage.getData());
            }else if (!taskInfo.get(0).topActivity.getClassName().equals("com.surefiz.screens.weightdetails.WeightDetailsActivity")) {
                sendBroadcastToPage(jObject.optInt("pushType"));
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See showNotification method below.
    }

    public void sendBroadcastToPage(int pushType) {
        if(pushType==5){
            Intent intent = new Intent("new_bmi_data");
            intent.putExtra("notificationFlag", "1");
            intent.putExtra("serverUserId", jObject.optString("serverUserId"));
            intent.putExtra("ScaleUserId", jObject.optString("ScaleUserId"));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(FcmLongJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param serverNotification FCM serverNotification received.
     * @param data
     */
    private void showNotification(RemoteMessage.Notification serverNotification, Map<String, String> data) {
        Log.d("@@Notify : ", "message Received");
        PendingIntent pendingIntent;
        JSONObject jsonObject = null;
        JSONObject jObject = null;
        try {
            jsonObject = new JSONObject(data.toString());
            jObject = jsonObject.getJSONObject("pushData");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jObject.optInt("pushType") == 2) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("notificationFlag", "1");
            intent.putExtra("reciver_id", jObject.optString("senderId"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (jObject.optInt("pushType") == 3) {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("notificationFlag", "1");
            intent.putExtra("reciver_id", jObject.optString("senderId"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (jObject.optInt("pushType") == 4) {
            Intent intent = new Intent(this, AcountabilityActivity.class);
            intent.putExtra("notificationFlag", "1");
            intent.putExtra("reciver_id", jObject.optString("senderId"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (jObject.optInt("pushType") == 5) {
            Intent intent = new Intent(this, BMIDetailsActivity.class);
            intent.putExtra("notificationFlag", "1");
            intent.putExtra("serverUserId", jObject.optString("serverUserId"));
            intent.putExtra("ScaleUserId", jObject.optString("ScaleUserId"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }else {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.putExtra("notificationFlag", "1");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

        String channelId = getString(R.string.fcm_default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setContentTitle(serverNotification.getTitle())
                        .setContentText(serverNotification.getBody())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(serverNotification.getBody()))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

//        notificationBuilder.setAutoCancel(true);
        notificationManager.notify(100, notificationBuilder.build());
    }
}
