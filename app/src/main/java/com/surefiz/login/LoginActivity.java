package com.surefiz.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.surefiz.R;
import com.surefiz.utils.MethodUtils;


public class LoginActivity extends AppCompatActivity {
    LoginViewAndResponsive loginViewAndResponsive;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_login, null);
        setContentView(view);

        MethodUtils.setStickyBar(this);
//        loginViewAndResponsive = new LoginViewAndResponsive(this, view);

    }
}
