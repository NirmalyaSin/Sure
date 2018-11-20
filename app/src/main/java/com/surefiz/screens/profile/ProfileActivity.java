package com.surefiz.screens.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.surefiz.R;
import com.surefiz.screens.dashboard.BaseActivity;

import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivity {
    public View view;
    ProfileClickEvent profileClickEvent;
    de.hdodenhof.circleimageview.CircleImageView profile_image;
    ImageView iv_plus_add_image;
    EditText et_DOB;
    EditText et_phone;
    EditText et_full;
    EditText et_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        view = View.inflate(this, R.layout.activity_profile, null);
        addContentView(view);
        initView();
        profileClickEvent = new ProfileClickEvent(this);

    }

    private void initView() {
        profile_image=view.findViewById(R.id.profile_image);
        iv_plus_add_image=view.findViewById(R.id.iv_plus_add_image);
        et_DOB=view.findViewById(R.id.et_DOB);
        et_phone=view.findViewById(R.id.et_phone);
        et_full=view.findViewById(R.id.et_full);
        et_gender=view.findViewById(R.id.et_gender);
    }
}
