package com.surefiz.registration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.surefiz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistrationActivity extends AppCompatActivity {

    public RegistrationClickEvent registrationClickEvent;
    @BindView(R.id.tv_upload)
    TextView tv_upload;
    @BindView(R.id.et_DOB)
    EditText et_DOB;
    @BindView(R.id.et_gender)
    EditText et_gender;
    @BindView(R.id.et_units)
    EditText et_units;
    @BindView(R.id.et_height)
    EditText et_height;
    @BindView(R.id.et_weight)
    EditText et_weight;
    @BindView(R.id.et_time_loss)
    EditText et_time_loss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ButterKnife.bind(this);
        registrationClickEvent = new RegistrationClickEvent(this);

    }
}
