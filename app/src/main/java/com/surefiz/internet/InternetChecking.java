package com.surefiz.internet;

import android.os.AsyncTask;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InternetChecking {

    public  boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            Log.d("Internet",":::::"+!address.equals(""));

            return !address.equals("");

        } catch (UnknownHostException e) {
            // Log error
            Log.d("Internet",":::::No Internet");

        }
        return false;
    }

    AsyncTask asyncTask=new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] objects) {
            return isInternetAvailable();
        }
    };

}