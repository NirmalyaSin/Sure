package com.surefiz.screens.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.surefiz.R;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.sharedhandler.LoginShared;

import static com.surefiz.utils.GeneralToApp.WELCOME_WAIT_TIME;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Set welcome screen shown flag as true
                LoginShared.setWelcome(WelcomeActivity.this, true);
                //Go to Login Page
                startActivity(new Intent(WelcomeActivity.this,
                        LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }, WELCOME_WAIT_TIME);
    }
}
