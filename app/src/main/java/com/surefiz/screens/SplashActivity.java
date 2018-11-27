package com.surefiz.screens;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.surefiz.R;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.registration.RegistrationActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.screens.wificonfig.WifiConfigActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;

public class SplashActivity extends AppCompatActivity {

    RegistrationModel loginModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MethodUtils.fullScreen(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate();
            }
        }, GeneralToApp.SPLASH_WAIT_TIME);

    }

    private void navigate() {
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
