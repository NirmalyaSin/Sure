package com.surefiz.screens.familyinvite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.surefiz.R;
import com.surefiz.dialog.AddUserDialog;
import com.surefiz.dialog.ChooseOptionDialog;
import com.surefiz.interfaces.MoveTutorial;
import com.surefiz.interfaces.OnUiEventClick;
import com.surefiz.screens.groupinvite.adapter.GroupInviteAdapter;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.users.UserListActivity;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.progressloader.LoadingData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FamilyInviteActivity extends AppCompatActivity implements OnUiEventClick {
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
                new ChooseOptionDialog(this, this).show();
                break;
            case R.id.btn_register_skip:
                Intent loginIntent = new Intent(this, OtpActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
        }
    }

    @Override
    public void onUiClick(Intent intent, int eventCode) {
        if (intent != null) {
            boolean isDialog = intent.getBooleanExtra("userDialog", false);
            new AddUserDialog(this, success -> {
                if (success.equals("1")) {
                    count--;
                    if (count > 0) {
                        btn_register_family_add.setText("Add Sub User (" + count + ")");
                    } else {
                        Intent loginIntent = new Intent(this, OtpActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                }
            }, isDialog).show();
        }
    }
}
