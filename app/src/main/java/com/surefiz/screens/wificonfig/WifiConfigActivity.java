package com.surefiz.screens.wificonfig;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.surefiz.R;
import com.surefiz.screens.settings.SettingsActivity;

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
    /*@BindView(R.id.iv_showPassword)
    ImageView iv_showPassword;
    @BindView(R.id.iv_hidePassword)
    ImageView iv_hidePassword;*/
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;

    private WifiActivityClickEvent wifiActivityClickEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_config);
        ButterKnife.bind(this);
        setViewAndFunctionality();
        //editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        wifiActivityClickEvent = new WifiActivityClickEvent(this);

        btn_skip_config.setBackgroundDrawable(null);
    }

    private void setViewAndFunctionality() {
        if (getIntent().getStringExtra("comeFrom") != null)
            if (getIntent().getStringExtra("comeFrom").equals("1")) {
                rl_back.setVisibility(View.VISIBLE);
                btn_skip_config.setVisibility(View.GONE);

                rl_back.setOnClickListener(v -> {
                    Intent deviceIntent = new Intent(WifiConfigActivity.this, SettingsActivity.class);
                    startActivity(deviceIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                });
            }
    }
}
