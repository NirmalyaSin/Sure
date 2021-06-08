package com.surefiz.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.surefiz.sharedhandler.LoginShared;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
//    private MyPreference myPreference;

    @Override
    public void onCreate() {
        super.onCreate();

//        myPreference = MyPreference.getMyPreference(getApplicationContext());
    }

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
//        myPreference.setFcmToken(refreshedToken);

        LoginShared.setDeviceToken(getApplicationContext(), refreshedToken);


    }
}
