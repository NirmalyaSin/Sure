package com.surefiz.sharedhandler;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.surefiz.registration.model.RegistrationModel;


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

    public static void destroySessionTypePreference() {
        prefs = context.getSharedPreferences(
                SharedUtils.TYPE_DEAD_ON_LOGOUT_SHARED, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }
}
