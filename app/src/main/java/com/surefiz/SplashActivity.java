package com.surefiz;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.surefiz.login.LoginActivity;
import com.surefiz.registration.RegistrationActivity;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;

public class SplashActivity extends AppCompatActivity {

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
        Intent loginIntent = new Intent(this, RegistrationActivity.class);
        startActivity(loginIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}
