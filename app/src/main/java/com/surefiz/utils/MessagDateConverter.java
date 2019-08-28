package com.surefiz.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MessagDateConverter {

    public static String DateConverter(Context mContext, String s) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }




        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        //boolean use24HourClock = DateFormat.is24HourFormat(mContext);
        /*if (use24HourClock) {
            df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        } else {
            df = new SimpleDateFormat("MMM dd, yyyy HH:mm aa");
        }*/

        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(date);

        return formattedDate;
    }


    public static String DateConverterForNotification(String dateStr, String time, String notificationType) {

        SimpleDateFormat sdf;
        if (notificationType.equalsIgnoreCase("4")) {
            sdf = new SimpleDateFormat("MMM dd HH:mm");
        } else {
            sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        }
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(dateStr + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat df;
        if (notificationType.equalsIgnoreCase("4")) {
            df = new SimpleDateFormat("MMM dd, HH:mm");
        } else {
            df = new SimpleDateFormat("dd/MM/yyy, HH:mm:ss");
        }
        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(date);

        return formattedDate;
    }
}
