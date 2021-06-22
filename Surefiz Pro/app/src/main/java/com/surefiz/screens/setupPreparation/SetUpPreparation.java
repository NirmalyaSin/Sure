package com.surefiz.screens.setupPreparation;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.surefiz.R;
import com.surefiz.application.Constant;
import com.surefiz.dialog.CustomAlert;
import com.surefiz.interfaces.OnUiEventClick;
import com.surefiz.screens.apconfig.ApConfigActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.wificonfig.WifiConfigActivity;
import com.surefiz.sharedhandler.InstructionSharedPreference;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.progressloader.LoadingData;

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

    @BindView(R.id.rl_video_onlyOneWifi)
    RelativeLayout rl_video_onlyOneWifi;

    @BindView(R.id.ivPlayPauseOnlyWifi)
    ImageView ivPlayPauseOnlyWifi;

    @BindView(R.id.rl_video_onlyTwoWifi)
    RelativeLayout rl_video_onlyTwoWifi;

    @BindView(R.id.ivPlayPauseOnlyTwoWifi)
    ImageView ivPlayPauseOnlyTwoWifi;

    @BindView(R.id.rl_videoViewWelcome)
    RelativeLayout rl_videoViewWelcome;


    protected boolean fromLogin = false;
    protected boolean fromSettings = false;
    protected boolean AP = false;
    protected boolean SMART = false;
    protected boolean isSecondStage = false;
    VideoView videoView;
    VideoView video_view_only_oneWifi;
    VideoView video_view_only_twoWifi;
    boolean isPlayed = false;
    boolean isOnlyWifiVideoPlayed = false;
    boolean isTwoWifiPlayed = false;
    CustomAlert customAlert;
    GestureDetectorCompat gestureDetectorCompat;
    int videoNegativeCount = 0;


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

        setOnClick();
        getHeight();
        setUpDefaultView();
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

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isPlayed) {
                    videoView.pause();
                    ivPlayPause.setVisibility(View.VISIBLE);
                    ivPlayPause.setImageResource(R.drawable.play);
                    btnWifiAp.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnWifi.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                    btnWifi.setEnabled(true);
                    btnWifiAp.setEnabled(true);
                    isPlayed = false;
                }
                return gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });


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

        ivPlayPause.setVisibility(View.GONE);
        ivPlayPause.setImageResource(R.drawable.pause);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc3);
        videoView.start();
        isPlayed = true;
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
                    ivPlayPause.setVisibility(View.VISIBLE);
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
        ivPlayPauseOnlyWifi.setOnClickListener(this);
        ivPlayPauseOnlyTwoWifi.setOnClickListener(this);
        videoView.setOnClickListener(this);
        rl_videoViewWelcome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivPlayPause:
                if (!isPlayed) {
                    videoView.start();
                    isPlayed = true;
                    ivPlayPause.setImageResource(R.drawable.pause);
                    ivPlayPause.setVisibility(View.GONE);
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

            case R.id.video_view_welcome:


                break;

            case R.id.ivPlayPauseOnlyWifi:
                if (!isOnlyWifiVideoPlayed) {

                    video_view_only_oneWifi.start();
                    ivPlayPauseOnlyWifi.setImageResource(R.drawable.pause);
                    ivPlayPauseOnlyWifi.setVisibility(View.GONE);
                    btnYes.setEnabled(false);
                    btnYes.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
                    isOnlyWifiVideoPlayed = true;
                } else {
                    isOnlyWifiVideoPlayed = false;
                    video_view_only_oneWifi.pause();
                    ivPlayPauseOnlyWifi.setImageResource(R.drawable.play);
                    btnYes.setEnabled(true);
                    btnYes.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                }

                break;

            case R.id.ivPlayPauseOnlyTwoWifi:

                if (!isTwoWifiPlayed) {
                    video_view_only_twoWifi.start();
                    ivPlayPauseOnlyTwoWifi.setImageResource(R.drawable.pause);
                    ivPlayPauseOnlyTwoWifi.setVisibility(View.GONE);
                    btnYes.setEnabled(false);
                    btnYes.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
                    isTwoWifiPlayed = true;
                } else {
                    video_view_only_twoWifi.pause();
                    ivPlayPauseOnlyTwoWifi.setImageResource(R.drawable.play);
                    isTwoWifiPlayed = false;
                    if (videoNegativeCount == 0) {
                        callDialog();
                    } else {
                        btnYes.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                        btnYes.setEnabled(true);
                    }
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
                    videoView.stopPlayback();
                    new SendWifiLog(Constant.TWO_WIFI, this, new OnUiEventClick() {
                        @Override
                        public void onUiClick(Intent intent, int eventCode) {
                            if (intent.getBooleanExtra("success", false)) {
                                setUpSmartWifiView();
                            }
                        }
                    });
                }
                break;

            case R.id.btnWifiAp:
                if (!videoView.isPlaying()) {
                    videoView.stopPlayback();
                    new SendWifiLog(Constant.ONE_WIFI, this, new OnUiEventClick() {
                        @Override
                        public void onUiClick(Intent intent, int eventCode) {
                            if (intent.getBooleanExtra("success", false)) {
                                setUpApWifiView();
                            }
                        }
                    });
                }
                break;

            case R.id.btnYes:
                if (SMART) {
                    new SendWifiLog(Constant.TWO_WIFI_YES_ITS_DONE, this, new OnUiEventClick() {
                        @Override
                        public void onUiClick(Intent intent, int eventCode) {
                            if(intent.getBooleanExtra("success",false)){
                                callSmartConfig();
                            }
                        }
                    });
                } else if (AP) {
                    new SendWifiLog(Constant.ONE_WIFI_YES_ITS_DONE, this, new OnUiEventClick() {
                        @Override
                        public void onUiClick(Intent intent, int eventCode) {
                            if(intent.getBooleanExtra("success",false)){
                                callAPConfig();
                            }
                        }
                    });
                }
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

        rl_video_onlyOneWifi.setVisibility(View.VISIBLE);
        video_view_only_oneWifi = findViewById(R.id.video_view_only_oneWifi);
        video_view_only_oneWifi.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc3);
        video_view_only_oneWifi.start();
        isOnlyWifiVideoPlayed = true;
        ivPlayPauseOnlyWifi.setVisibility(View.GONE);
        ivPlayPauseOnlyWifi.setImageResource(R.drawable.pause);
        btnYes.setEnabled(false);
        btnYes.setBackground(getResources().getDrawable(R.drawable.button_disable_background));

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
                }
                return gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });

        AP = true;
        isSecondStage = true;

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
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (rl_video_onlyOneWifi.getVisibility() == View.VISIBLE) {
            video_view_only_oneWifi.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc3);
            video_view_only_oneWifi.start();
            isOnlyWifiVideoPlayed = true;
            ivPlayPauseOnlyWifi.setVisibility(View.GONE);
            ivPlayPauseOnlyWifi.setImageResource(R.drawable.pause);
            btnYes.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
            btnYes.setEnabled(false);
            video_view_only_oneWifi.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
                }
            });
        } else if (rl_video_onlyTwoWifi.getVisibility() == View.VISIBLE) {
            video_view_only_twoWifi.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc3);
            video_view_only_twoWifi.start();
            isTwoWifiPlayed = true;
            ivPlayPauseOnlyTwoWifi.setVisibility(View.GONE);
            ivPlayPauseOnlyTwoWifi.setImageResource(R.drawable.pause);
            btnYes.setBackground(getResources().getDrawable(R.drawable.button_disable_background));
            btnYes.setEnabled(false);
            video_view_only_twoWifi.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
                }
            });

        }
    }

    private void setUpSmartWifiView() {

        sub_txt.setText(R.string.TextSmart);
        bothButtonView.setVisibility(View.VISIBLE);
        btnWifiAp.setVisibility(View.GONE);
        btnWifi.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        ivPlayPause.setVisibility(View.GONE);
        btnYes.setVisibility(View.VISIBLE);
        rl_video_onlyOneWifi.setVisibility(View.GONE);

        rl_video_onlyTwoWifi.setVisibility(View.VISIBLE);
        video_view_only_twoWifi = findViewById(R.id.video_view_only_twoWifi);
        video_view_only_twoWifi.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc3);
        video_view_only_twoWifi.start();
        isTwoWifiPlayed = true;
        ivPlayPauseOnlyTwoWifi.setVisibility(View.GONE);
        ivPlayPauseOnlyTwoWifi.setImageResource(R.drawable.pause);
        btnYes.setEnabled(false);
        btnYes.setBackground(getResources().getDrawable(R.drawable.button_disable_background));

        onCompletionTwoWifiVideo();

        video_view_only_twoWifi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isTwoWifiPlayed) {
                    video_view_only_twoWifi.pause();
                    ivPlayPauseOnlyTwoWifi.setVisibility(View.VISIBLE);
                    ivPlayPauseOnlyTwoWifi.setImageResource(R.drawable.play);
                    isTwoWifiPlayed = false;
                    if (videoNegativeCount == 0) {
                        callDialog();
                    } else {
                        btnYes.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                        btnYes.setEnabled(true);
                    }
                }
                return gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });

        SMART = true;
        isSecondStage = true;

    }

    private void onCompletionTwoWifiVideo() {

        video_view_only_twoWifi.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (video_view_only_twoWifi != null) {
                    ivPlayPauseOnlyTwoWifi.setVisibility(View.VISIBLE);
                    ivPlayPauseOnlyTwoWifi.setImageResource(R.drawable.play);
                    isTwoWifiPlayed = false;
                    video_view_only_twoWifi.pause();

                    if (videoNegativeCount == 0) {
                        callDialog();
                    } else {
                        btnYes.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                        btnYes.setEnabled(true);
                    }
                }
            }
        });

    }

    private void callDialog() {
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
                new SendWifiLog(Constant.TWO_WIFI_DILOG_NO, SetUpPreparation.this, new OnUiEventClick() {
                    @Override
                    public void onUiClick(Intent intent, int eventCode) {
                        if(intent.getBooleanExtra("success",false)){
                            video_view_only_twoWifi.stopPlayback();
                            videoNegativeCount++;
                            startNewVideo();
                        }
                    }
                });


            }
        });

        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
                btnYes.setBackground(getResources().getDrawable(R.drawable.login_button_gradient));
                btnYes.setEnabled(true);
                new SendWifiLog(Constant.TWO_WIFI_DIALOG_YES, SetUpPreparation.this, new OnUiEventClick() {
                    @Override
                    public void onUiClick(Intent intent, int eventCode) {
                        if(intent.getBooleanExtra("success",false)){
                            callSmartConfig();
                        }
                    }
                });
            }
        });

    }

    private void startNewVideo() {
        rl_video_onlyTwoWifi.setVisibility(View.VISIBLE);
        video_view_only_twoWifi = findViewById(R.id.video_view_only_twoWifi);
        video_view_only_twoWifi.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.wc3);
        video_view_only_twoWifi.start();
        isTwoWifiPlayed = true;
        ivPlayPauseOnlyTwoWifi.setVisibility(View.GONE);
        ivPlayPauseOnlyTwoWifi.setImageResource(R.drawable.pause);
        btnYes.setEnabled(false);
        btnYes.setBackground(getResources().getDrawable(R.drawable.button_disable_background));

        onCompletionTwoWifiVideo();

    }


    private void setUpDefaultView() {
        sub_txt.setText(R.string.SetupPreparation);
        btnYes.setVisibility(View.GONE);
        bothButtonView.setVisibility(View.VISIBLE);
        btnWifi.setVisibility(View.VISIBLE);
        btnWifiAp.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.VISIBLE);
//        ivPlayPause.setVisibility(View.VISIBLE);
        rl_video_onlyOneWifi.setVisibility(View.GONE);
        rl_video_onlyTwoWifi.setVisibility(View.GONE);
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
                if (video_view_only_oneWifi != null) {
                    video_view_only_oneWifi.stopPlayback();
                } else if (video_view_only_twoWifi != null) {
                    video_view_only_twoWifi.stopPlayback();
                }
                setUpDefaultView();
                getHeight();
            } else
                super.onBackPressed();
        }
    }

}
