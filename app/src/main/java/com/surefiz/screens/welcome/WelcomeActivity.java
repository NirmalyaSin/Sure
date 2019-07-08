package com.surefiz.screens.welcome;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.surefiz.R;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.login.LoginClickEvent;
import com.surefiz.sharedhandler.LoginShared;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.surefiz.utils.GeneralToApp.WELCOME_WAIT_TIME;

public class WelcomeActivity extends AppCompatActivity {
    @BindView(R.id.txt_login)
    TextView tv_login;
    @BindView(R.id.txt_signup)
    TextView tv_signup;
    @BindView(R.id.txt_learn_more)
    TextView tv_learn_more;

    WelcomeClickEvent welcomeClickEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        welcomeClickEvent = new WelcomeClickEvent(this);

        VideoView videoView = findViewById(R.id.video_view_welcome);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.welcomevideo);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

  /*      new Handler().postDelayed(new Runnable() {
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
        }, WELCOME_WAIT_TIME);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoView videoView = findViewById(R.id.video_view_welcome);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.welcomevideo);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }
}
