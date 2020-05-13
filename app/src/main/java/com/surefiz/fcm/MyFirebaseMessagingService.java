package com.surefiz.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.surefiz.screens.NotificationHandleClassOnForeground;
import com.surefiz.screens.accountability.AcountabilityActivity;
import com.surefiz.screens.bmidetails.BMIDetailsActivity;
import com.surefiz.screens.boardcast.BoardCastActivity;
import com.surefiz.screens.boardcast.model.BroadcastItem;
import com.surefiz.screens.chat.ChatActivity;
import com.surefiz.screens.chat.model.Conversation;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.notifications.NotificationActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.progressstatus.ProgressStatusActivity;
import com.surefiz.screens.userconfirmation.UserConfirmationActivity;
import com.surefiz.screens.welcome.WelcomeActivity;
import com.surefiz.sharedhandler.LoginShared;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private MyApplicationClass myApplicationClass;
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
        Log.e(TAG, "FromDataPush: " + remoteMessage.getData());
        Log.e(TAG, "FromDataPush: " + remoteMessage.getNotification().getBody());

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

                        callMessageReceived();
                    }
                }  else if (jObject.optInt("pushType") == 5) {
                    LoginShared.setWeightFromNotification(this, "5");
                } else if (jObject.optInt("pushType") == 6) {
                    LoginShared.setWeightFromNotification(this, "6");
                } else if (jObject.optInt("pushType") == 1) {
                    LoginShared.setWeightFromNotification(this, "1");
                } else if (jObject.optInt("pushType") == 7) {
                    LoginShared.setWeightFromNotification(this, "7");
                } else if (jObject.optInt("pushType") == 9) {
                    LoginShared.setOTP(this, jObject.optString("OTP"));
                    callOTPReceived();
                } else if (jObject.optInt("pushType") == 10) {
                    LoginShared.setWeightFromNotification(this, "10");
                } else if (jObject.optInt("pushType") == 3) {
                    LoginShared.setWeightFromNotification(this, "3");
                } else if (jObject.optInt("pushType") == 4) {
                    LoginShared.setWeightFromNotification(this, "4");
                } else if (jObject.optInt("pushType") == 12) {


                    if (taskInfo.get(0).topActivity.getClassName().equals("com.surefiz.screens.boardcast.BoardCastActivity")) {
                            BroadcastItem broadcastItem = new BroadcastItem();
                            broadcastItem.setDateTime(jObject.optString("dateTime"));
                            broadcastItem.setSenderId(jObject.optString("senderId"));
                            broadcastItem.setName(jObject.optString("name"));
                            broadcastItem.setMessage(jObject.optString("message"));
                            broadcastItem.setMessageFrom(jObject.optString("messageFrom"));
                            myApplicationClass.broadcastItemsNotification.add(broadcastItem);
                            callBroadCastMessageReceived();
                    }else {
                        LoginShared.setWeightFromNotification(this, "12");
                    }

                } else {
                    //LoginShared.setWeightFromNotification(this, "7");
                    LoginShared.setWeightFromNotification(this, "0");
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

            if (taskInfo.get(0).topActivity.getClassName().equals("com.surefiz.screens.bmidetails.BMIDetailsActivity")) {
                sendBroadcastToPage(jObject.optInt("pushType"));
            } else if (taskInfo.get(0).topActivity.getClassName().equals("com.surefiz.screens.weightdetails.WeightDetailsActivity")) {
                sendBroadcastToPage(jObject.optInt("pushType"));
            } else if (taskInfo.get(0).topActivity.getClassName().equals("com.surefiz.screens.chat.ChatActivity")) {
                //Nothing to do
            }else if (taskInfo.get(0).topActivity.getClassName().equals("com.surefiz.screens.boardcast.BoardCastActivity")) {
                //Nothing to do
            } else {
                if(LoginShared.getAccessToken(this).equals("")){

                    /*Intent intent = new Intent(this, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);*/

                }else {
                      showNotification(remoteMessage.getNotification(), remoteMessage.getData());
                }
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See showNotification method below.
    }

    public void sendBroadcastToPage(int pushType) {
        if (pushType == 5) {
            Intent intent = new Intent("new_bmi_data");
            intent.putExtra("notificationFlag", "1");
            intent.putExtra("serverUserId", jObject.optString("serverUserId"));
            intent.putExtra("ScaleUserId", jObject.optString("ScaleUserId"));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    private void callOTPReceived() {
        Intent intent = new Intent("ON_OTP_RECEIVED");
        sendBroadcast(intent);
    }

    private void callMessageReceived() {
        Intent intent = new Intent("ON_MESSAGE_RECEIVED");
        sendBroadcast(intent);
    }


    private void callBroadCastMessageReceived() {
        Intent intent = new Intent("ON_BROAD_CAST_MESSAGE_RECEIVED");
        sendBroadcast(intent);
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
        PendingIntent pendingIntent = null;
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
        } /*else if (jObject.optInt("pushType") == 3) {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("notificationFlag", "1");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }*/ else if (jObject.optInt("pushType") == 4) {
            Intent intent = new Intent(this, AcountabilityActivity.class);
            //intent.putExtra("notificationFlag", "1");
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
        } else if (jObject.optInt("pushType") == 9) {
            Intent intent = new Intent(this, OtpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (jObject.optInt("pushType") == 6) {
            Intent intent = new Intent(this, ProgressStatusActivity.class);
            intent.putExtra("notificationFlag", "1");
            intent.putExtra("userId", jObject.optString("userId"));
            intent.putExtra("contentId", jObject.optString("contentId"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (jObject.optInt("pushType") == 1) {

            Intent intent = new Intent(this, NotificationHandleClassOnForeground.class);
            intent.putExtra("notificationFlag", "1");
            intent.putExtra("lastServerUpdateDate", jObject.optString("lastServerUpdateDate"));
            intent.putExtra("lastServerUpdateTime", jObject.optString("lastServerUpdateTime"));

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (jObject.optInt("pushType") == 7) {
            Intent intent = new Intent(this, UserConfirmationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (jObject.optInt("pushType") == 3) {
            Intent intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("fromDashboard", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else if (jObject.optInt("pushType") == 10) {

            Intent intent = new Intent(this, NotificationHandleClassOnForeground.class);
            intent.putExtra("notificationFlag", "1");
            intent.putExtra("shouldOpenWeightAssignView", true);
            intent.putExtra("lastServerUpdateDate", jObject.optString("lastServerUpdateDate"));
            intent.putExtra("lastServerUpdateTime", jObject.optString("lastServerUpdateTime"));

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            /*Intent intent = new Intent(this, UserListActivity.class);
            intent.putExtra("isFromPushNotification",true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);*/
        } else if (jObject.optInt("pushType") == 12) {
            Intent intent = new Intent(this, BoardCastActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        } else {
            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.putExtra("notificationFlag", "1");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }



        //buildNotification(pendingIntent,serverNotification.getBody());

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        String channelId = getString(R.string.fcm_default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        //.setContentTitle(serverNotification.getTitle())
                        .setContentTitle(getString(R.string.app_name_splash))
                        .setContentText(serverNotification.getBody())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(serverNotification.getBody()))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //notificationBuilder.setSmallIcon(R.drawable.ic_stat_ac_unit);
            notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(m, notificationBuilder.build());
    }



    private void buildNotification(PendingIntent pendingIntent ,String body){

        final int NOTIFY_ID = 0; // ID of notification
        String CHANNEL_ID = this.getString(R.string.fcm_default_notification_channel_id);
        String id = this.getString(R.string.app_name); // default_channel_id
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setContentInfo("")
                .setLargeIcon(icon)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_notification);
            notificationBuilder.setColor(getResources().getColor(R.color.colorAccent));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("channel description");
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(getResources().getColor(R.color.colorAccent));
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

            notificationBuilder.setChannelId(CHANNEL_ID);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }


        assert notificationManager != null;
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }
}