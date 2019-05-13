package com.surefiz.screens.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.surefiz.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MembershipActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_member);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.membership_single, R.id.membership_group, R.id.membership_family,
            R.id.rl_back})
    public void onViewClicked(View view) {
        String intentCode = "";
        switch (view.getId()) {
            case R.id.membership_single:
                intentCode = "single";
                break;
            case R.id.membership_group:
                intentCode = "group";
                break;
            case R.id.membership_family:
                intentCode = "family";
                break;
            case R.id.rl_back:
                onBackPressed();
                return;
        }

        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra("membership", intentCode);
        if (getIntent().hasExtra("completeStatus"))
            intent.putExtra("completeStatus", getIntent().getStringExtra("completeStatus"));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
