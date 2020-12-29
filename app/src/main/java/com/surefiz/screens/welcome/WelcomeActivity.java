package com.surefiz.screens.welcome;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import com.surefiz.R;
import com.surefiz.helpers.PermissionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WelcomeActivity extends AppCompatActivity {
    @BindView(R.id.txt_login)
    TextView tv_login;
    @BindView(R.id.tv_signup)
    TextView tv_signup;
    @BindView(R.id.txt_learn_more)
    TextView tv_learn_more;
    @BindView(R.id.video_view_welcome)
    VideoView videoView;

    PermissionHelper permissionHelper;
    WelcomeClickEvent welcomeClickEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        permissionHelper=new PermissionHelper(this);
        welcomeClickEvent = new WelcomeClickEvent(this);

        VideoView videoView = findViewById(R.id.video_view_welcome);
        //videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.welcome_480_720);
        getHeight();
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

        if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {
        } else {
            permissionHelper.requestForPermission(PermissionHelper.PERMISSION_FINE_LOCATION);
        }

    }

    private void getHeight(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(this.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        Log.d("Device-WidthPixels",":::"+displayMetrics.heightPixels);

        if(displayMetrics.heightPixels<2132){
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc1);

        }else if(displayMetrics.heightPixels==2132){
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc2);

        }else if(displayMetrics.heightPixels>2132){
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc3);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHeight();
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }
}
