package com.surefiz.screens.wificonfig;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetUpPreparation extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.rl_back)
    RelativeLayout rl_back;

    @BindView(R.id.sub_txt)
    TextView sub_txt;

    @BindView(R.id.btnOk)
    Button btnOk;

    protected boolean fromLogin=false;
    protected boolean fromSettings=false;

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


        sub_txt.setText(R.string.SetupPreparation);
        setOnClick();
    }

    protected void setOnClick(){
        rl_back.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rl_back:

                onBackPressed();
                break;

            case R.id.btnOk:

                Intent intent = new Intent(this, WifiConfigActivity.class);
                if(fromLogin){
                    intent.putExtra("fromLogin",true);
                } else if(fromSettings){
                    intent.putExtra("fromSettings",true);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;

        }

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
