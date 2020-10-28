package com.surefiz.screens.otp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.surefiz.R;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.sharedhandler.LoginShared;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtpActivity extends AppCompatActivity {

    public OtpClickEvent otpClickEvent;
    @BindView(R.id.et_first)
    EditText et_first;
    @BindView(R.id.et_second)
    EditText et_second;
    @BindView(R.id.et_third)
    EditText et_third;
    @BindView(R.id.et_fourth)
    EditText et_fourth;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;

    private BroadcastReceiver mOTPReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //updateUi();
            setOTP();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        ButterKnife.bind(this);
        otpClickEvent = new OtpClickEvent(this);
        setOTP();
    }

    private void setOTP() {
        String otpFromNotification = LoginShared.getOTP(this);
        if (!otpFromNotification.isEmpty() && otpFromNotification.length() == 4) {
            et_first.setText("" + otpFromNotification.charAt(0));
            et_second.setText("" + otpFromNotification.charAt(1));
            et_third.setText("" + otpFromNotification.charAt(2));
            et_fourth.setText("" + otpFromNotification.charAt(3));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mOTPReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mOTPReceiver, new IntentFilter("ON_OTP_RECEIVED"));

    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else{
            super.onBackPressed();
        }
    }
}
