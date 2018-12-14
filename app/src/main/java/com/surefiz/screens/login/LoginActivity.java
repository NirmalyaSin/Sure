package com.surefiz.screens.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.surefiz.R;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.editEmail)
    EditText editEmail;
    @BindView(R.id.editPassword)
    EditText editPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tv_forgetPassword)
    TextView tv_forgetPassword;
    @BindView(R.id.iv_facebook)
    ImageView iv_facebook;
    @BindView(R.id.iv_twiter)
    ImageView iv_twiter;
    @BindView(R.id.tv_register)
    TextView tv_register;

    private LoginClickEvent loginClickEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loginClickEvent = new LoginClickEvent(this);
    }

}
