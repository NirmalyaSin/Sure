package com.rts.commonutils_2_0.deviceinfo;

import android.app.Activity;
import android.util.DisplayMetrics;

public class DeviceResolution {

    private int mHeight, mWidth;
    private double mScreenInches;
    private Activity mActivity;

    public DeviceResolution(Activity activity) {
        this.mActivity = activity;
        getDeviceResolution();
    }

    private void getDeviceResolution() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mHeight = displaymetrics.heightPixels;
        mWidth = displaymetrics.widthPixels;

        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidth / dm.xdpi, 2);
        double y = Math.pow(mHeight / dm.ydpi, 2);

        mScreenInches = Math.sqrt(x + y);

        if (mWidth <= 480 && mScreenInches > 5.0) {
            mScreenInches = 4.5;
        }
    }

    public int getHeight(double heightVal) {
        return (int) (mHeight * heightVal);
    }

    public int getWidth(double widthVal) {
        return (int) (mWidth * widthVal);
    }

    public float getTextSize(double textSize) {
        return (float) (mScreenInches * textSize);
    }

}
