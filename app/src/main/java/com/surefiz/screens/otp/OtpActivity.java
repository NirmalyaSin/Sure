package com.surefiz.screens.otp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.surefiz.R;
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
}
