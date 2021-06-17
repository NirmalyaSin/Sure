package com.surefiz.screens.setupPreparation;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.surefiz.R;
import com.surefiz.screens.apconfig.ApConfigActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.wificonfig.WifiConfigActivity;
import com.surefiz.sharedhandler.InstructionSharedPreference;
import com.surefiz.sharedhandler.LoginShared;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetUpPreparation extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.rl_back)
    RelativeLayout rl_back;

    @BindView(R.id.sub_txt)
    TextView sub_txt;

    @BindView(R.id.btnWifi)
    Button btnWifi;

    @BindView(R.id.btnWifiAp)
    Button btnWifiAp;

    @BindView(R.id.btnYes)
    Button btnYes;

    @BindView(R.id.btn_skip)
    Button btn_skip;

    @BindView(R.id.bothButtonView)
    RelativeLayout bothButtonView;

    @BindView(R.id.ivPlayPause)
    ImageView ivPlayPause;

    protected boolean fromLogin = false;
    protected boolean fromSettings = false;
    protected boolean AP = false;
    protected boolean SMART = false;
    protected boolean isSecondStage = false;
    VideoView videoView;
    boolean isPlayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_preparation);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("fromLogin")) {
            fromLogin = getIntent().getBooleanExtra("fromLogin", false);
        }

        if (getIntent().hasExtra("fromSettings")) {
            fromSettings = getIntent().getBooleanExtra("fromSettings", false);
        }
        videoView = findViewById(R.id.video_view_welcome);
        getHeight();

        setUpDefaultView();
        setOnClick();


    }

    private void getHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(this.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        Log.d("Device-WidthPixels", ":::" + displayMetrics.heightPixels);

      /*  if(displayMetrics.heightPixels<2132){
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc1);

        }else if(displayMetrics.heightPixels==2132){
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc2);

        }else if(displayMetrics.heightPixels>2132){
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc3);
        }*/


        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc3);
        videoView.start();
        isPlayed = true;
        ivPlayPause.setImageResource(R.drawable.pause);
        btnWifiAp.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
        btnWifi.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
        btnWifi.setEnabled(false);
        btnWifiAp.setEnabled(false);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (videoView != null) {
                    ivPlayPause.setImageResource(R.drawable.play);
                    isPlayed = false;
                    videoView.pause();
                    btnWifiAp.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnWifi.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnWifi.setEnabled(true);
                    btnWifiAp.setEnabled(true);
                }
            }
        });
    }


    protected void setOnClick() {
        rl_back.setOnClickListener(this);
        btnWifi.setOnClickListener(this);
        btnWifiAp.setOnClickListener(this);
        btnYes.setOnClickListener(this);
        btn_skip.setOnClickListener(this);
        ivPlayPause.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivPlayPause:
                if (!isPlayed) {
                    videoView.start();
                    isPlayed = true;
                    ivPlayPause.setImageResource(R.drawable.pause);
                    btnWifiAp.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
                    btnWifi.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
                    btnWifi.setEnabled(false);
                    btnWifiAp.setEnabled(false);
                } else {
                    videoView.pause();
                    ivPlayPause.setImageResource(R.drawable.play);
                    btnWifiAp.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnWifi.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnWifi.setEnabled(true);
                    btnWifiAp.setEnabled(true);
                    isPlayed = false;
                }
                break;

            case R.id.rl_back:
                onBackPressed();
                break;

            case R.id.btn_skip:

                if (!new InstructionSharedPreference(this).isInstructionShown(this, LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId())) {
                    Intent details = new Intent(this, InstructionActivity.class);
                    startActivity(details);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    break;
                } else {
                    Intent details = new Intent(this, DashBoardActivity.class);
                    startActivity(details);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                break;

            case R.id.btnWifi:
                if (!videoView.isPlaying()) {
                    setUpSmartWifiView();
                }
                break;

            case R.id.btnWifiAp:
                if (!videoView.isPlaying()) {
                    setUpApWifiView();
                }
                break;

            case R.id.btnYes:
                if (SMART)
                    callSmartConfig();
                else if (AP)
                    callAPConfig();
                break;
        }

    }

    private void setUpApWifiView() {
        sub_txt.setText(R.string.TextAp);
        bothButtonView.setVisibility(View.VISIBLE);
        btnWifiAp.setVisibility(View.GONE);
        btnWifi.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        ivPlayPause.setVisibility(View.GONE);
        btnYes.setVisibility(View.VISIBLE);
        AP = true;
        isSecondStage = true;

    }


    private void setUpSmartWifiView() {

        sub_txt.setText(R.string.TextSmart);
        bothButtonView.setVisibility(View.VISIBLE);
        btnWifiAp.setVisibility(View.GONE);
        btnWifi.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        ivPlayPause.setVisibility(View.GONE);
        btnYes.setVisibility(View.VISIBLE);
        SMART = true;
        isSecondStage = true;

    }


    private void setUpDefaultView() {
        sub_txt.setText(R.string.SetupPreparation);
        btnYes.setVisibility(View.GONE);
        bothButtonView.setVisibility(View.VISIBLE);
        btnWifi.setVisibility(View.VISIBLE);
        btnWifiAp.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.VISIBLE);
        ivPlayPause.setVisibility(View.VISIBLE);
        AP = false;
        SMART = false;
        isSecondStage = false;
        if (fromLogin)
            btn_skip.setVisibility(View.VISIBLE);
    }


    private void callSmartConfig() {
        Intent intent = new Intent(this, WifiConfigActivity.class);
        if (fromLogin) {
            intent.putExtra("fromLogin", true);
        } else if (fromSettings) {
            intent.putExtra("fromSettings", true);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //finish();
    }

    private void callAPConfig() {
        Intent apconfigIntent = new Intent(this, ApConfigActivity.class);

        if (fromLogin) {
            apconfigIntent.putExtra("fromLogin", true);
        } else if (fromSettings) {
            apconfigIntent.putExtra("fromSettings", true);
        }
        startActivity(apconfigIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //finish();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            if (isSecondStage) {
                setUpDefaultView();
                getHeight();
            } else
                super.onBackPressed();
        }
    }

}
