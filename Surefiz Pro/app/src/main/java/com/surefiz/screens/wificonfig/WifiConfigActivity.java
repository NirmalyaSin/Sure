package com.surefiz.screens.wificonfig;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.surefiz.R;
import com.surefiz.application.Constant;
import com.surefiz.dialog.CustomAlert;
import com.surefiz.helpers.PermissionHelper;
import com.surefiz.interfaces.OnUiEventClick;
import com.surefiz.screens.setupPreparation.SetUpPreparation;
import com.surefiz.sharedhandler.InstructionSharedPreference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiConfigActivity extends AppCompatActivity {
    @BindView(R.id.editSSID)
    EditText editSSID;
    @BindView(R.id.editBSSID)
    EditText editBSSID;
    @BindView(R.id.editPassword)
    EditText editPassword;
    @BindView(R.id.btnConfigure)
    Button btnConfigure;
    @BindView(R.id.btn_skip_config)
    Button btn_skip_config;
    @BindView(R.id.iv_showPassword)
    ImageView iv_showPassword;
    @BindView(R.id.iv_hidePassword)
    ImageView iv_hidePassword;
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;
    @BindView(R.id.txt_body)
    TextView txt_body;

    @BindView(R.id.rl_video_onlyOneWifi)
    RelativeLayout rl_video_onlyOneWifi;

    @BindView(R.id.ivPlayPauseOnlyWifi)
    ImageView ivPlayPauseOnlyWifi;

    @BindView(R.id.sub_txt)
    TextView sub_txt;

    @BindView(R.id.bothButtonView)
    RelativeLayout bothButtonView;

    @BindView(R.id.btnYes)
    Button btnYes;

    @BindView(R.id.btnNo)
    Button btnNo;

    @BindView(R.id.sv_main)
    ScrollView sv_main;
    CustomAlert customAlert;

    protected boolean isSecondStage = false;
    GestureDetectorCompat gestureDetectorCompat;
    boolean isOnlyWifiVideoPlayed = true;
    VideoView video_view_only_oneWifi;
    int videoNegativeCount = 0;

    protected boolean fromSettings = false;
    public boolean fromLogin = false;
    private PermissionHelper permissionHelper;
    boolean isNoPressed = false;


    private WifiActivityClickEvent wifiActivityClickEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_config);
        ButterKnife.bind(this);
        //editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        permissionHelper = new PermissionHelper(this);
        wifiActivityClickEvent = new WifiActivityClickEvent(this);

        if (getIntent().hasExtra("fromLogin")) {
            fromLogin = getIntent().getBooleanExtra("fromLogin", false);
        }

        if (getIntent().hasExtra("fromSettings")) {
            fromSettings = getIntent().getBooleanExtra("fromSettings", false);
        }

        setViewAndFunctionality();

        gestureDetectorCompat = new GestureDetectorCompat(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                /* Always return true, to indicate that the gestureDetectorCompat
                 * consumed the touch and can continue to the
                 * next gestures(long, single, etc..)
                 */
                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                Log.i("SOME_TAG", "Longpress detected");
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });


    }

    private void setViewAndFunctionality() {

        if (fromLogin) {
            btn_skip_config.setVisibility(View.VISIBLE);
        }

        rl_back.setOnClickListener(v -> {

          /*  Intent intent = new Intent(this, SetUpPreparation.class);

            if(fromLogin){
                intent.putExtra("fromLogin",true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

            }else if(fromSettings){
                intent.putExtra("fromSettings",true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }else*/
            onBackPressed();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PermissionHelper.PERMISSION_FINE_LOCATION) {
            if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {
                wifiActivityClickEvent.showConnectedWifiSSID();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new InstructionSharedPreference(this).setFirstText(true);
    }


    public void setUpSmartWifiView() {
        sub_txt.setVisibility(View.GONE);
        sub_txt.setText(R.string.TextSmart);
        bothButtonView.setVisibility(View.VISIBLE);
        btnYes.setVisibility(View.VISIBLE);
        btnNo.setVisibility(View.VISIBLE);

        rl_video_onlyOneWifi.setVisibility(View.VISIBLE);
        video_view_only_oneWifi = findViewById(R.id.video_view_only_oneWifi);
        video_view_only_oneWifi.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.video3b);
        video_view_only_oneWifi.start();
        isOnlyWifiVideoPlayed = true;
        ivPlayPauseOnlyWifi.setVisibility(View.GONE);
        ivPlayPauseOnlyWifi.setImageResource(R.drawable.pause);
        btnYes.setEnabled(false);
        btnYes.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
        btnNo.setEnabled(false);
        btnNo.setBackground(getResources().getDrawable(R.drawable.button_disable_background));

        setOncompletion();

        video_view_only_oneWifi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isOnlyWifiVideoPlayed) {
                    isOnlyWifiVideoPlayed = false;
                    video_view_only_oneWifi.pause();
                    ivPlayPauseOnlyWifi.setVisibility(View.VISIBLE);
                    ivPlayPauseOnlyWifi.setImageResource(R.drawable.play);
                    btnYes.setEnabled(true);
                    btnYes.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnNo.setEnabled(true);
                    btnNo.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));

                }
                return gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });
//        AP = true;
        isSecondStage = true;
    }


    public void callDialog() {
        customAlert = new CustomAlert(this);
        customAlert.setHeaderText(getResources().getString(R.string.app_name));
        customAlert.setSubText(getResources().getString(R.string.doYouSeeFlashingLigght));
        customAlert.setKeyName("No", "Yes");
        customAlert.setCancelVisible();
        customAlert.show();

        customAlert.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
               /* new SendWifiLog(Constant.TWO_WIFI_DILOG_NO, SetUpPreparation.this, new OnUiEventClick() {
                    @Override
                    public void onUiClick(Intent intent, int eventCode) {
                        if(intent.getBooleanExtra("success",false)){

                        }
                    }
                });*/


            }
        });

        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
                btnYes.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                btnYes.setEnabled(true);
               /* new SendWifiLog(Constant.TWO_WIFI_DIALOG_YES, SetUpPreparation.this, new OnUiEventClick() {
                    @Override
                    public void onUiClick(Intent intent, int eventCode) {
                        if(intent.getBooleanExtra("success",false)){
                            callSmartConfig();
                        }
                    }
                });*/
            }
        });

    }

    protected void startNewVideo() {
        rl_video_onlyOneWifi.setVisibility(View.VISIBLE);
        video_view_only_oneWifi = findViewById(R.id.video_view_only_oneWifi);
        video_view_only_oneWifi.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.video4);
        video_view_only_oneWifi.start();
        isOnlyWifiVideoPlayed = true;
        ivPlayPauseOnlyWifi.setVisibility(View.GONE);
        ivPlayPauseOnlyWifi.setImageResource(R.drawable.pause);
        btnYes.setVisibility(View.VISIBLE);
        btnYes.setEnabled(false);
        btnYes.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
        btnNo.setVisibility(View.GONE);
        btnNo.setEnabled(false);
        btnNo.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
        isNoPressed = true;
        onCompletionTwoWifiVideo();
    }

    private void onCompletionTwoWifiVideo() {

        video_view_only_oneWifi.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (video_view_only_oneWifi != null) {
                    ivPlayPauseOnlyWifi.setVisibility(View.VISIBLE);
                    ivPlayPauseOnlyWifi.setImageResource(R.drawable.play);
                    isOnlyWifiVideoPlayed = false;
                    video_view_only_oneWifi.pause();
                    btnYes.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnYes.setEnabled(true);
                    btnNo.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnNo.setEnabled(true);
                   /* if (videoNegativeCount == 0) {
                        callDialog();
                    } else {
                        btnYes.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                        btnYes.setEnabled(true);
                    }*/
                }
            }
        });

    }


    private void setOncompletion() {
        video_view_only_oneWifi.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (video_view_only_oneWifi != null) {
                    ivPlayPauseOnlyWifi.setVisibility(View.VISIBLE);
                    ivPlayPauseOnlyWifi.setImageResource(R.drawable.play);
                    isOnlyWifiVideoPlayed = false;
                    video_view_only_oneWifi.pause();
                    btnYes.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnYes.setEnabled(true);
                    btnNo.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnNo.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isSecondStage) {
            if (video_view_only_oneWifi != null) {
                if (isNoPressed) {
                    setUpSmartWifiView();
                    isNoPressed = false;
                    videoNegativeCount = 0;
                } else {
                    video_view_only_oneWifi.stopPlayback();
                    sub_txt.setVisibility(View.GONE);
                    btnYes.setVisibility(View.GONE);
                    btnNo.setVisibility(View.GONE);
                    rl_video_onlyOneWifi.setVisibility(View.GONE);
                    sv_main.setVisibility(View.VISIBLE);
                /*AP = false;
                SMART = false;*/
                    isSecondStage = false;
                }
            }
        } else {
            super.onBackPressed();
        }
    }
}
