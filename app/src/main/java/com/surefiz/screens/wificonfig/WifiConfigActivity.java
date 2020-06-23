package com.surefiz.screens.wificonfig;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.surefiz.R;
import com.surefiz.helpers.PermissionHelper;
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
    @BindView(R.id.iv_showPassword)
    ImageView iv_showPassword;
    @BindView(R.id.iv_hidePassword)
    ImageView iv_hidePassword;
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;

    private PermissionHelper permissionHelper;


    private WifiActivityClickEvent wifiActivityClickEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_config);
        ButterKnife.bind(this);
        setViewAndFunctionality();
        //editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        permissionHelper=new PermissionHelper(this);
        wifiActivityClickEvent = new WifiActivityClickEvent(this);

        //btn_skip_config.setBackgroundDrawable(null);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==PermissionHelper.PERMISSION_FINE_LOCATION) {
            if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {
                wifiActivityClickEvent.showConnectedWifiSSID();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
