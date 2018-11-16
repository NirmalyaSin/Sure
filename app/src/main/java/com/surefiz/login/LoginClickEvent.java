package com.surefiz.login;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.surefiz.R;
import com.surefiz.registration.RegistrationActivity;
import com.surefiz.wificonfig.WifiActivityClickEvent;
import com.surefiz.wificonfig.WifiConfigActivity;

public class LoginClickEvent implements View.OnClickListener {
    LoginActivity mLoginActivity;

    public LoginClickEvent(LoginActivity activity) {
        this.mLoginActivity = activity;
    //    hideSoftKeyBoard();
        setClickEvent();
    }


    private void setClickEvent() {
        mLoginActivity.btnLogin.setOnClickListener(this);
        mLoginActivity.tv_register.setOnClickListener(this);
        mLoginActivity.iv_facebook.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_forgetPassword:
                break;

            case R.id.btnLogin:
                break;

            case R.id.iv_facebook:
                mLoginActivity.startActivity(new Intent(mLoginActivity, WifiConfigActivity.class));
                mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.iv_twiter:
                break;

            case R.id.tv_register:
                mLoginActivity.startActivity(new Intent(mLoginActivity, RegistrationActivity.class));
                mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    private void hideSoftKeyBoard() {
        View view = mLoginActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mLoginActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
