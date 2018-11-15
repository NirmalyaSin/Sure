package com.rts.commonutils_2_0.logcollection;

/**
 * Developer: Hari Narayan Jha
 * Date: 04.02.2015
 * Copyright by RTS
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * <p>Class: LogCollection</p>
 * <p>Purpose: To implement different logs type directly</p>
 * <p>
 * <p><strong>Direction:</strong></p>
 * <p><code>private LogCollection logcLogCollection;</code></p>
 * <p><code>logcLogCollection = new LogCollection(MyActivity.this);</code></p>
 * <p><code>logcLogCollection.setLogVBlack("Your Message");</code></p>
 * <p><strong>Supporting: </strong>No supporting files need to implement.</p>
 * <p><strong>boolean enableLog: </strong>Change enableLog status false manually to disable all log related message from your application.</p>
 */
public class LogCollection {

    private Context context;
    private static final String TAG = "LogCollection";
//    private static String tag = "";
    private static boolean enableLog = true;

    public static void enableLogCollection(boolean temp){
        enableLog = temp;
    }

    private static LogCollection mInstance;

    private LogCollection(Context context) {
        this.context = context;
    }

    public static synchronized LogCollection getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LogCollection(context);
        }
        return mInstance;
    }

    /**
     * Calling Log.d()
     */
    public static void setLogDBlue(String logValue) {
        if (enableLog)
            Log.d(getCallerCallerClassName(), logValue);
    }

    /**
     * Calling Log.d() with tag
     */
    public static void setLogDBlue(String tag, String logValue) {
        if (enableLog)
            Log.d(tag, logValue);
    }

    /**
     * Calling Log.e()
     */
    public static void setLogERed(String logValue) {
        if (enableLog)
            Log.e(getCallerClassName(), logValue);
    }

    /**
     * Calling Log.e() with tag
     */
    public static void setLogERed(String tag, String logValue) {
        if (enableLog)
            Log.e(tag, logValue);
    }

    /**
     * Calling Log.v()
     */
    public static void setLogVBlack(String logValue) {
        if (enableLog)
            Log.v(getCallerCallerClassName(), logValue);
    }

    /**
     * Calling Log.v() with tag
     */
    public static void setLogVBlack(String tag, String logValue) {
        if (enableLog)
            Log.v(tag, logValue);
    }

    /**
     * Calling Log.i()
     */
    public static void setLogIGreen(String logValue) {
        if (enableLog)
            Log.i(getCallerCallerClassName(), logValue);
    }

    /**
     * Calling Log.i() with tag
     */
    public static void setLogIGreen(String tag, String logValue) {
        if (enableLog)
            Log.i(tag, logValue);
    }

    /**
     * Calling Log.w()
     */
    public static void setLogWOrange(String logValue) {
        if (enableLog)
            Log.w(getCallerCallerClassName(), logValue);
    }

    /**
     * Calling Log.w() with tag
     */
    public static void setLogWOrange(String tag, String logValue) {
        if (enableLog)
            Log.w(tag, logValue);
    }

    /**
     * Calling Log.wtf()
     */
    public static void setLogWtfRed(String logValue) {
        if (enableLog)
            Log.wtf(getCallerCallerClassName(), logValue);
    }

    /**
     * Calling Log.wtf() with tag
     */
    public static void setLogWtfRed(String tag, String logValue) {
        if (enableLog)
            Log.wtf(tag, logValue);
    }

    /**
     * Calling stack trace for Exception class
     */
    public static void setStackTrace(Exception e) {
        if (enableLog)
            e.printStackTrace();
    }

    /**
     * Showing Toast Message for LENGTH_LONG
     */
    public void showToastLong(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Showing Toast Message for LENGTH_SHORT
     */
    public void showToastShort(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This returns Class name of the top level parent class in the Heirarchy. Like Activity even the calling class is SplashActivity extends AppCompatActivity.
     *
     * @return Class Name, Class name of calling method from which this method has been called. Return LogCollection otherwise.
     */
    public static String getCallerCallerClassName() {
        try {
            StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
            String callerClassName = null;
            for (int i=1; i<stElements.length; i++) {
                StackTraceElement ste = stElements[i];
                if (!ste.getClassName().equals(LogCollection.class.getName())&& ste.getClassName().indexOf("java.lang.Thread")!=0) {
                    if (callerClassName==null) {
                        callerClassName = ste.getClassName();
                    } else if (!callerClassName.equals(ste.getClassName())) {
                        return ste.getClassName();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TAG;
    }

    /**
     * Return just class name of the calling class like SplashActivity or ProductFragment.
     *
     * @return Class Name, Class name of calling method from which this method has been called. Return LogCollection otherwise.
     */
    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(LogCollection.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getClassName();
            }
        }
        return TAG;
    }
}
