package com.surefiz.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatDateConverter {

    public static String DateConverter(String s){

        SimpleDateFormat sdf;

        if(s.length()==19){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }else if(s.length()==21){
            sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        }else
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        SimpleDateFormat sdfOutout = new SimpleDateFormat("h:mm a");

        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String formattedDate = sdfOutout.format(date);

        return formattedDate;
    }

    public static String Hour_Minute2(String s){

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        SimpleDateFormat sdfOutout = new SimpleDateFormat("HH:mm");

        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String formattedDate = sdfOutout.format(date);

        return formattedDate;
    }

    public static String Hours12(String _24HourTime){

            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("h:mm a");
        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(_24HourTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return _12HourSDF.format(_24HourDt);
    }
}
