package com.surefiz.screens;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.dashboard.model.DashboardModel;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.screens.wificonfig.WifiConfigActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashActivity extends AppCompatActivity {

    RegistrationModel loginModel;
    private String notificationPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MethodUtils.fullScreen(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            LoginShared.setWeightFromNotification(this, "1");
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
            }

        } else {

            if (LoginShared.getRegistrationDataModel(this) != null) {
                if (LoginShared.getRegistrationDataModel(this).getData() != null) {
                    if (LoginShared.getRegistrationDataModel(this).getData().getToken().equals("") ||
                            LoginShared.getRegistrationDataModel(this).getData().getToken() == null) {
                        Intent loginIntent = new Intent(this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
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
                    }/* else if(LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).get) {
                    Intent intent = new Intent(this, WeightDetailsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }*/ else if (notificationPage.equals("1")) {
                        Intent intent = new Intent(this, WeightDetailsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        Intent intent = new Intent(this, DashBoardActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                } else {
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            } else {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }
    }
}
