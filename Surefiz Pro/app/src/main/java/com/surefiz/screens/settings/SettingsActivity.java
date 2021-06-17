package com.surefiz.screens.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.apconfig.ApConfigActivity;
import com.surefiz.screens.changepassword.ChangePasswordActivity;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.mydevice.MyDeviceActivity;
import com.surefiz.screens.privacy.PrivacyActivity;
import com.surefiz.screens.setupPreparation.SetUpPreparation;
import com.surefiz.screens.weightManagement.WeightManagementActivity;
import com.surefiz.sharedhandler.LoginShared;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    public View view;
    RelativeLayout rl_config, rl_device, rl_privacy, rl_password, rl_weight, rl_apconfig;
    TextView Txt_Register;
    View view1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_settings, null);
        addContentView(view);
        initializeView();
        setClickEvent();
    }

    private void setClickEvent() {
        rl_config.setOnClickListener(this);
        rl_device.setOnClickListener(this);
        rl_privacy.setOnClickListener(this);
        rl_password.setOnClickListener(this);
        rl_weight.setOnClickListener(this);
        rl_apconfig.setOnClickListener(this);

        if(LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getScaleUserId().equals("2")){
            rl_device.setClickable(false);
            Txt_Register.setTextColor(getResources().getColor(R.color.grey_color));
        }

    }

    private void initializeView() {
        rl_config = findViewById(R.id.rl_config);
        rl_device = findViewById(R.id.rl_device);
        rl_privacy = findViewById(R.id.rl_privacy);
        rl_password = findViewById(R.id.rl_password);
        rl_weight = findViewById(R.id.rl_weight);
        rl_apconfig = findViewById(R.id.rl_apconfig);
        Txt_Register = findViewById(R.id.Txt_Register);

        view1 = findViewById(R.id.view1);
        setHeaderView();
    }

    private void setHeaderView() {
        tv_universal_header.setText("Settings");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_config:
                Intent settingsIntent = new Intent(this, SetUpPreparation.class);
                settingsIntent.putExtra("fromSettings", true);
                startActivity(settingsIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                break;
            case R.id.rl_device:
                Intent deviceIntent = new Intent(this, MyDeviceActivity.class);
                startActivity(deviceIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                // finish();
                break;
            case R.id.rl_privacy:
                //    MethodUtils.errorMsg(SettingsActivity.this, "Under Development");
                Intent privacyIntent = new Intent(this, PrivacyActivity.class);
                startActivity(privacyIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //    finish();
                break;
            case R.id.rl_password:
//                MethodUtils.errorMsg(SettingsActivity.this, "Under Development");
                Intent passwordIntent = new Intent(this, ChangePasswordActivity.class);
                startActivity(passwordIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.rl_weight:
                Intent weightIntent = new Intent(this, WeightManagementActivity.class);
                startActivity(weightIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.rl_apconfig:
                Intent apconfigIntent = new Intent(this, ApConfigActivity.class);
                startActivity(apconfigIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }
}