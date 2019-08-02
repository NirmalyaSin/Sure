package com.surefiz.notificationclasses;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.surefiz.receivers.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReminderNotification {

    Activity mActivity;
    String reminderId, reminderText, dateTime;

    //This Constructor is to create a Notification
    public ReminderNotification(Activity mActivity, String reminderId, String reminderText, String dateTime) {
        this.mActivity = mActivity;
        this.reminderId = reminderId;
        this.reminderText = reminderText;
        this.dateTime = dateTime;

        createReminderNotification();
    }

    //This Constructor is to Cancle/Remove a Notification
    public ReminderNotification(Activity mActivity, String reminderId) {
        this.mActivity = mActivity;
        this.reminderId = reminderId;

        cancelAlarm();
    }

    private void createReminderNotification () {

        AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(mActivity, AlarmReceiver.class);
        notificationIntent.putExtra("notificationText", reminderText);
        notificationIntent.putExtra("notifyId", Integer.parseInt(reminderId));
        PendingIntent broadcast = PendingIntent.getBroadcast(mActivity.getBaseContext(), Integer.parseInt(reminderId), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.setTime(convertDateTime(dateTime));
        //cal.add(Calendar.SECOND, 10);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }


    public Date convertDateTime(String dateTime) {
        //String dateTime = "02 Jul 2019 15:57";
        Date date = new Date();
        SimpleDateFormat formatter6 = new SimpleDateFormat("dd MMM yyyy HH:mm");
        try {
            date = formatter6.parse(dateTime);
            System.out.println("dateTime: " + date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


    public void cancelAlarm()
    {
        Intent notificationIntent = new Intent(mActivity, AlarmReceiver.class);
        notificationIntent.putExtra("notificationText", reminderText);
        notificationIntent.putExtra("notifyId", Integer.parseInt(reminderId));
        PendingIntent sender = PendingIntent.getBroadcast(mActivity.getBaseContext(), Integer.parseInt(reminderId), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
