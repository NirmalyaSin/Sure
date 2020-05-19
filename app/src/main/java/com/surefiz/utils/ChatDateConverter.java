package com.surefiz.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatDateConverter {

    public static String DateConverter(String s){

        //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (Exception e) {
            e.printStackTrace();

            sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                date = sdf.parse(s);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(date);

        return formattedDate;
    }


    public static String WeightProgressDateConverter(String s){

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(date);

        return formattedDate;
    }

    public static String Hour_Minute(String s){

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String formattedDate = sdf.format(date);

        return formattedDate;
    }
}
