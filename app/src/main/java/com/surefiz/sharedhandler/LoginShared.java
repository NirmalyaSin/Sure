package com.surefiz.sharedhandler;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.surefiz.screens.dashboard.model.DashboardModel;
import com.surefiz.screens.profile.model.profile.ViewProfileModel;
import com.surefiz.screens.registration.model.RegistrationModel;


public class LoginShared {
    private static Context context;
    private static SharedPreferences prefs;


    private static void activateShared(Context context) {
        LoginShared.context = context;
        LoginShared.prefs = context.getSharedPreferences(
                SharedUtils.TYPE_DEAD_ON_LOGOUT_SHARED, Context.MODE_PRIVATE);
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

    /**
     * Set Dashboard Data Model
     */
    public static void setDashBoardDataModel(Context context, DashboardModel loginModel) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(loginModel);
        editor.putString(SharedUtils.KEY_SHARED_DASHBOARD, json);
        editor.commit();

    }

    //
    public static DashboardModel getDashBoardDataModel(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        DashboardModel loginModel = null;
        Gson gson = new Gson();
        String userDataModelJson = LoginShared.prefs.getString(SharedUtils.KEY_SHARED_DASHBOARD, "");

        if (userDataModelJson.equals(""))
            loginModel = new DashboardModel();
        else
            loginModel = gson.fromJson(userDataModelJson, DashboardModel.class);

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

    public static void setUserMacId(long id) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(SharedUtils.KEY_SHARED_USER_MAC_ID, id);
        editor.commit();
    }

    public static long getUserMacId(Context context) {
        long id = 1;
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        id = prefs.getLong(SharedUtils.KEY_SHARED_USER_MAC_ID, id);
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

    /**
     * USER PHOTO
     */
    public static void setUserPhoto(Context context, String value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedUtils.KEY_SHARED_USER_PROFILE_PIC, value);
        editor.commit();
    }

    public static String getUserPhoto(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getString(SharedUtils.KEY_SHARED_USER_PROFILE_PIC, SharedUtils.KEY_SHARED_NO_DATA);
    }

    /**
     * USER NAME
     */
    public static void setUserName(Context context, String value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedUtils.KEY_SHARED_USER_NAME, value);
        editor.commit();
    }

    public static String getUserName(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getString(SharedUtils.KEY_SHARED_USER_NAME, SharedUtils.KEY_SHARED_NO_DATA);
    }

    /**
     * OTP
     */
    public static void setOTP(Context context, String value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedUtils.KEY_SHARED_OTP, value);
        editor.commit();
    }

    public static String getOTP(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getString(SharedUtils.KEY_SHARED_OTP, SharedUtils.KEY_SHARED_NO_DATA);
    }

    /**
     * NOTIFICATION WEIGHT
     */
    public static void setNotificationWeight(Context context, String value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedUtils.KEY_SHARED_NOTIFICATION_WEIGHT, value);
        editor.commit();
    }

    public static String getNotificationWeight(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getString(SharedUtils.KEY_SHARED_NOTIFICATION_WEIGHT, SharedUtils.KEY_SHARED_NO_DATA);
    }


    public static void destroySessionTypePreference(Context context) {
        prefs = context.getSharedPreferences(
                SharedUtils.TYPE_DEAD_ON_LOGOUT_SHARED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //Set Preference for welcome page
        boolean isWelcome = getWelcome(context);
        //Get Device Token
        String deviceToken = getDeviceToken(context);
        //Clear the session
        editor.clear();
        editor.apply();
        //Set Preference for welcome page
        setWelcome(context, isWelcome);
        //Set Device token again
        setDeviceToken(context, deviceToken);
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

    public static void setWeightFromNotification(Context context, String value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedUtils.KEY_SHARED_WEIGHT_PAGE_FROM_NOTIFICATION, value);
        editor.commit();
    }

    public static String getWeightFromNotification(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getString(SharedUtils.KEY_SHARED_WEIGHT_PAGE_FROM_NOTIFICATION, SharedUtils.KEY_SHARED_FROM_DEFAULT);
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

    public static void setWelcome(Context context, boolean value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SharedUtils.KEY_SHARED_IS_WELCOME, value);
        editor.commit();
    }

    public static boolean getWelcome(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getBoolean(SharedUtils.KEY_SHARED_IS_WELCOME, false);
    }

    public static void setweightpush(Context context, boolean value) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SharedUtils.KEY_SHARED_IS_WELCOME, value);
        editor.commit();
    }

    public static boolean getWeightpush(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getBoolean(SharedUtils.KEY_SHARED_IS_WELCOME, false);
    }


    /*public static void setInstructionVisibility(Context context, String userId) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        SharedPreferences.Editor editor = prefs.edit();
        String newStr = getInstructionVisibility(context);

        if (!newStr.contains(userId + ",")) {
            newStr = newStr.equalsIgnoreCase("") ? newStr + userId + "," : "";
        }
        editor.putString(SharedUtils.KEY_SHARED_IS_INSTRUCTION, newStr);
        editor.commit();
    }

    public static String getInstructionVisibility(Context context) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);
        return prefs.getString(SharedUtils.KEY_SHARED_IS_INSTRUCTION, "");
    }

    public static boolean isInstructionShown(Context context, String userId) {
        if (LoginShared.context == null || LoginShared.prefs == null)
            activateShared(context);

        String newStr = getInstructionVisibility(context);
        return newStr.contains(userId + ",");

    }*/
}
