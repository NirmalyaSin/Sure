package com.surefiz.screens;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.surefiz.R;
import com.surefiz.screens.accountability.AcountabilityActivity;
import com.surefiz.screens.bmidetails.BMIDetailsActivity;
import com.surefiz.screens.chat.ChatActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.notifications.NotificationActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.screens.welcome.WelcomeActivity;
import com.surefiz.screens.wificonfig.WifiConfigActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class SplashActivity extends AppCompatActivity {

    RegistrationModel loginModel;
    private String notificationPage;
    private String receiver_id;
    private JSONObject jsonObject1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MethodUtils.fullScreen(this);
        //bundle.get("pushData")
        //pushData

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            jsonObject1 = null;
            if (getIntent().hasExtra("_fbSourceApplicationHasBeenSet") && !getIntent().hasExtra("pushData")) {
                System.out.print("SureFIZ");
            } else if (getIntent().hasExtra("pushData")) {
                String value = bundle.get("pushData").toString();
                try {
                    jsonObject1 = new JSONObject(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject1.optInt("pushType") == 2) {
                    receiver_id = jsonObject1.optString("senderId");
                    LoginShared.setWeightFromNotification(this, "2");
                } else if (jsonObject1.optInt("pushType") == 3) {
                    LoginShared.setWeightFromNotification(this, "3");
                } else if (jsonObject1.optInt("pushType") == 4) {
                    LoginShared.setWeightFromNotification(this, "4");
                }else if (jsonObject1.optInt("pushType") == 5) {
                    LoginShared.setWeightFromNotification(this, "5");
                } else {
                    LoginShared.setWeightFromNotification(this, "1");
                }
            } else {
                LoginShared.setWeightFromNotification(this, "1");
            }
        }
        if (getIntent().getStringExtra("notificationFlag") != null) {
            notificationPage = getIntent().getStringExtra("notificationFlag");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    Log.d("@@Splash : ", "onCreate() ==> " + notificationPage);
                    if (notificationPage != null) {
                        if (notificationPage.equals("1")) {
                            LoginShared.setWeightPageFrom(SplashActivity.this, "0");
                        }
                    } else {
                        notificationPage = "0";
                    }
                } catch (Exception e) {
                    Log.d("exception", "exception happened weight");
                    notificationPage = "0";
                    e.printStackTrace();
                }
                navigate();

            }
        }, GeneralToApp.SPLASH_WAIT_TIME);

    }

    private void navigate() {
        Log.d("@@SNotification : ", String.valueOf(notificationPage));

        if (!LoginShared.getWeightFromNotification(this).equals("0")) {
            if (LoginShared.getWeightFromNotification(this).equals("1")) {
                Intent intent = new Intent(this, WeightDetailsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else if (LoginShared.getWeightFromNotification(this).equals("2")) {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("reciver_id", receiver_id);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else if (LoginShared.getWeightFromNotification(this).equals("3")) {
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else if (LoginShared.getWeightFromNotification(this).equals("4")) {
                Intent intent = new Intent(this, AcountabilityActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }else if (LoginShared.getWeightFromNotification(this).equals("5")) {
                Intent intent = new Intent(this, BMIDetailsActivity.class);
                intent.putExtra("notificationFlag", "1");
                intent.putExtra("serverUserId", jsonObject1.optString("serverUserId"));
                intent.putExtra("ScaleUserId", jsonObject1.optString("ScaleUserId"));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        } else {
            if (LoginShared.getRegistrationDataModel(this) != null) {
                if (LoginShared.getRegistrationDataModel(this).getData() != null) {
                    if (LoginShared.getRegistrationDataModel(this).getData().getToken().equals("") ||
                            LoginShared.getRegistrationDataModel(this).getData().getToken() == null) {
                        if(LoginShared.getWelcome(this)) {
                            Intent loginIntent = new Intent(this, LoginActivity.class);
                            startActivity(loginIntent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }else {
                            Intent loginIntent = new Intent(this, WelcomeActivity.class);
                            startActivity(loginIntent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }
                    } else if (!LoginShared.getstatusforOtpvarification(this)) {
                        Intent intent = new Intent(this, OtpActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else if (!LoginShared.getstatusforwifivarification(this)) {
                        Intent intent = new Intent(this, WifiConfigActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } /*else if (notificationPage.equals("1")) {
                        Intent intent = new Intent(this, WeightDetailsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }*/ else {
                        Intent intent = new Intent(this, DashBoardActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                } else {
                    if(LoginShared.getWelcome(this)) {
                        Intent loginIntent = new Intent(this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }else {
                        Intent loginIntent = new Intent(this, WelcomeActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                }
            } else {
                if(LoginShared.getWelcome(this)) {
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }else {
                    Intent loginIntent = new Intent(this, WelcomeActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        }
    }
}
