package com.surefiz.screens.setupPreparation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.apconfig.ApConfigActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.wificonfig.WifiConfigActivity;

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

    @BindView(R.id.bothButtonView)
    RelativeLayout bothButtonView;

    protected boolean fromLogin=false;
    protected boolean fromSettings=false;
    protected boolean AP=false;
    protected boolean SMART=false;
    protected boolean isSecondStage =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_preparation);
        ButterKnife.bind(this);

        if(getIntent().hasExtra("fromLogin")){
            fromLogin=getIntent().getBooleanExtra("fromLogin",false);
        }

        if(getIntent().hasExtra("fromSettings")){
            fromSettings=getIntent().getBooleanExtra("fromSettings",false);
        }


        setUpDefaultView();
        setOnClick();
    }

    protected void setOnClick(){
        rl_back.setOnClickListener(this);
        btnWifi.setOnClickListener(this);
        btnWifiAp.setOnClickListener(this);
        btnYes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rl_back:
                onBackPressed();
                break;

            case R.id.btnWifi:
                setUpSmartWifiView();
                break;

            case R.id.btnWifiAp:
                setUpApWifiView();
                break;

            case R.id.btnYes:
                if(SMART)
                    callSmartConfig();
                else if(AP)
                    callAPConfig();
                break;
        }

    }

    private void setUpSmartWifiView(){

        sub_txt.setText(R.string.TextSmart);
        bothButtonView.setVisibility(View.GONE);
        btnYes.setVisibility(View.VISIBLE);
        SMART=true;
        isSecondStage=true;

    }

    private void setUpApWifiView(){
        sub_txt.setText(R.string.TextAp);
        bothButtonView.setVisibility(View.GONE);
        btnYes.setVisibility(View.VISIBLE);
        AP=true;
        isSecondStage=true;

    }

    private void setUpDefaultView(){
        sub_txt.setText(R.string.SetupPreparation);
        btnYes.setVisibility(View.GONE);
        bothButtonView.setVisibility(View.VISIBLE);
        AP=false;
        SMART=false;
        isSecondStage=false;
    }



    private void callSmartConfig(){
        Intent intent = new Intent(this, WifiConfigActivity.class);
        if(fromLogin){
            intent.putExtra("fromLogin",true);
        } else if(fromSettings){
            intent.putExtra("fromSettings",true);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void callAPConfig(){
        Intent apconfigIntent = new Intent(this, ApConfigActivity.class);
        startActivity(apconfigIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else{
            if(isSecondStage)
                setUpDefaultView();
            else
                super.onBackPressed();
        }
    }
}
