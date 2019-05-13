package com.surefiz.screens.familyinvite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.surefiz.R;
import com.surefiz.screens.groupinvite.adapter.GroupInviteAdapter;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.utils.progressloader.LoadingData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FamilyInviteActivity extends AppCompatActivity {
    @BindView(R.id.btn_register_family_add)
    Button btn_register_family_add;

    private LoadingData loader;
    private int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_family);
        ButterKnife.bind(this);
        loader = new LoadingData(this);
        init();
    }

    private void init() {
        count = getIntent().getIntExtra("count", 0);
        btn_register_family_add.setText("Add Sub User (" + count + ")");
    }


    @OnClick({R.id.btn_register_family_add, R.id.btn_register_skip})
    public void manageClicks(View view) {
        switch (view.getId()) {
            case R.id.btn_register_family_add:
                break;
            case R.id.btn_register_skip:
                Intent loginIntent = new Intent(this, OtpActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
        }
    }
}
