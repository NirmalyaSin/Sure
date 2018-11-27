package com.surefiz.sharedhandler;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.surefiz.screens.profile.model.ViewProfileModel;
import com.surefiz.screens.registration.model.RegistrationModel;


public class LoginShared {
    private static Context context;
    private static SharedPreferences prefs;


    private static void activateShared(Context context) {
        LoginShared.context = context;
        LoginShared.prefs = context.getSharedPreferences(
                SharedUtils.TYPE_DEAD_ON_LOGOUT_SHARED, context.MODE_PRIVATE);
    }

    /**
     * Set User Data Model
     */
    public static void setRegistrationDataModel(Context context, RegistrationModel loginModel) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(loginModel);
        editor.putString(SharedUtils.KEY_SHARED_REGISTRATION_MODEL, json);
        editor.commit();

    }

    //
    public static RegistrationModel getRegistrationDataModel(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        RegistrationModel loginModel = null;
        Gson gson = new Gson();
        String userDataModelJson = LoginShared.prefs.getString(SharedUtils.KEY_SHARED_REGISTRATION_MODEL, "");

        if (userDataModelJson.equals(""))
            loginModel = new RegistrationModel();
        else
            loginModel = gson.fromJson(userDataModelJson, RegistrationModel.class);

        return loginModel;
    }

    /**
     * Set User Data Model
     */
    public static void setViewProfileDataModel(Context context, ViewProfileModel loginModel) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(loginModel);
        editor.putString(SharedUtils.KEY_SHARED_VIEW_PROFILE, json);
        editor.commit();

    }

    //
    public static ViewProfileModel getViewProfileDataModel(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        ViewProfileModel loginModel = null;
        Gson gson = new Gson();
        String userDataModelJson = LoginShared.prefs.getString(SharedUtils.KEY_SHARED_VIEW_PROFILE, "");

        if (userDataModelJson.equals(""))
            loginModel = new ViewProfileModel();
        else
            loginModel = gson.fromJson(userDataModelJson, ViewProfileModel.class);

        return loginModel;
    }

    //otp varification get set method

    public static void setstatusforOtpvarification(Context context, boolean otpvatified) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SharedUtils.KEY_SHARED_OTP_VERIFIED, otpvatified);
        editor.commit();
    }

    public static boolean getstatusforOtpvarification(Context context) {
        boolean isotp_varified = false;
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        isotp_varified = prefs.getBoolean(SharedUtils.KEY_SHARED_OTP_VERIFIED, false);
        return isotp_varified;
    }


//wifi config-varification

    public static void setstatusforwifivarification(Context context, boolean wifivarified) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SharedUtils.KEY_SHARED_WIFI_VERIFIED, wifivarified);
        editor.commit();
    }

    public static boolean getstatusforwifivarification(Context context) {
        boolean isotp_varified = false;
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        isotp_varified = prefs.getBoolean(SharedUtils.KEY_SHARED_WIFI_VERIFIED, false);
        return isotp_varified;
    }

    public static void setScaleUserId(int id) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SharedUtils.KEY_SHARED_SCALE_USER_ID, id);
        editor.commit();
    }

    public static int getScaleUserId(Context context) {
        int id = 1;
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        id = prefs.getInt(SharedUtils.KEY_SHARED_SCALE_USER_ID, id);
        return id;
    }

    /**
     * DEVICE TOKEN
     */
    public static void setDeviceToken(Context context, String value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedUtils.KEY_SHARED_DEVICE_TOKEN_KEY, value);
        editor.commit();
    }

    public static String getDeviceToken(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getString(SharedUtils.KEY_SHARED_DEVICE_TOKEN_KEY, SharedUtils.KEY_SHARED_NO_DATA);
    }


    public static void destroySessionTypePreference() {
        prefs = context.getSharedPreferences(
                SharedUtils.TYPE_DEAD_ON_LOGOUT_SHARED, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }


    /**
     * CALLING PAGE PREFERENCES
     */
    public static void setDashboardPageFrom(Context context, String value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedUtils.KEY_SHARED_DASHBOARD_FROM, value);
        editor.commit();
    }

    public static String getDashboardPageFrom(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getString(SharedUtils.KEY_SHARED_DASHBOARD_FROM, SharedUtils.KEY_SHARED_FROM_DEFAULT);
    }

    public static void setWeightPageFrom(Context context, String value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedUtils.KEY_SHARED_WEIGHT_PAGE_FROM, value);
        editor.commit();
    }

    public static String getWeightPageFrom(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getString(SharedUtils.KEY_SHARED_WEIGHT_PAGE_FROM, SharedUtils.KEY_SHARED_FROM_DEFAULT);
    }


    /*
    * Set captured weight
    */

    public static void setCapturedWeight(Context context, String value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedUtils.KEY_SHARED_CAPTURED_WEIGHT, value);
        editor.commit();
    }

    public static String getCapturedWeight(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getString(SharedUtils.KEY_SHARED_CAPTURED_WEIGHT,
                SharedUtils.KEY_SHARED_CAPTURED_WEIGHT_DEFAULT);
    }
}
