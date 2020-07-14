package com.surefiz.sharedhandler;


/**
 * It is used to declare different purpose of Shared types and their shared keys.<p>
 * Here two level of index is maintained. First in the variable in shared variable to recognize the shared type for each key used.
 * Second used in each key values to avoid duplicate value, with a combination of both index level.<p>
 * <b>Calling Type:</b><ol><p>
 * SharedPreferences sharedPreferences = getSharedPreferences(SHORT_TIME_SHARED_INDEX_0, MODE_PRIVATE);
 */
public class SharedUtils {

    /**
     * DEAD_ON_LOGOUT_SHARED_INDEX_2<P>
     * This is used to save data for the SESSION of an application.<p>
     * Ex. login status, user id etc.
     */

    public static final String TYPE_DEAD_ON_LOGOUT_SHARED = "TYPE_DEAD_ON_LOGOUT_SHARED";
    public static final String KEY_SHARED_USER_DATA_MODEL = "KEY_SHARED_USER_DATA_MODEL";
    public static final String KEY_SHARED_REGISTRATION_MODEL = "KEY_SHARED_REGISTRATION_MODEL";
    public static final String KEY_SHARED_OTP_VERIFIED = "IS_OTP_VARIFIED";
    public static final String KEY_SHARED_WIFI_VERIFIED = "IS_WiFi_VARIFIED";
    public static final String KEY_SHARED_VIEW_PROFILE = "KEY_SHARED_VIEW_PROFILE";
    public static final String KEY_SHARED_DASHBOARD = "KEY_SHARED_DASHBOARD";
    public static final String KEY_SHARED_SCALE_USER_ID = "KEY_SHARED_SCALE_USER_ID";
    public static final String KEY_SHARED_USER_MAC_ID = "KEY_SHARED_USER_MAC_ID";
    public static final String KEY_SHARED_DEVICE_TOKEN_KEY = "KEY_SHARED_DEVICE_TOKEN_KEY";
    public static final String KEY_SHARED_ACCESS_TOKEN_KEY = "KEY_SHARED_ACCESS_TOKEN_KEY";
    public static final String KEY_SHARED_USER_PROFILE_PIC = "KEY_SHARED_USER_PROFILE_PIC";
    public static final String KEY_SHARED_USER_NAME = "KEY_SHARED_USER_NAME";
    public static final String KEY_SHARED_OTP = "KEY_SHARED_OTP";
    public static final String KEY_SHARED_NOTIFICATION_WEIGHT = "KEY_SHARED_NOTIFICATION_WEIGHT";
    public static final String KEY_SHARED_DASHBOARD_FROM = "KEY_SHARED_DASHBOARD_FROM";
    public static final String KEY_SHARED_WEIGHT_PAGE_FROM = "KEY_SHARED_WEIGHT_PAGE_FROM";
    public static final String KEY_SHARED_WEIGHT_PAGE_FROM_NOTIFICATION = "KEY_SHARED_WEIGHT_PAGE_FROM_NOTIFICATION";
    public static final String KEY_SHARED_CAPTURED_WEIGHT = "KEY_SHARED_CAPTURED_WEIGHT";
    public static final String KEY_SHARED_IS_WELCOME = "KEY_SHARED_IS_WELCOME";
    public static final String KEY_SHARED_IS_INSTRUCTION = "KEY_SHARED_IS_INSTRUCTION";


    public static final String KEY_SHARED_USER_TYPE = "KEY_SHARED_USER_TYPE"; //customer or normal or fb etc

    public static final String KEY_SHARED_NO_DATA = "";
    public static final String KEY_SHARED_FROM_DEFAULT = "0";
    public static final String KEY_SHARED_CAPTURED_WEIGHT_DEFAULT = "0.0";
    public static final int KEY_SHARED_NO_DATA_INDEX = -1;
    public static final String ISFIRST = "ISFIRST";
    public static final String ISFIRSTTEXT = "ISFIRSTTEXT";
    public static final String ISINSTRUCTION = "ISINSTRUCTION";

    /*******************************************************************************/
}
