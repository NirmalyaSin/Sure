package com.surefiz.screens.wificonfig;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.surefiz.R;

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

    private WifiActivityClickEvent wifiActivityClickEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_config);
        ButterKnife.bind(this);
        wifiActivityClickEvent = new WifiActivityClickEvent(this);
    }
}
