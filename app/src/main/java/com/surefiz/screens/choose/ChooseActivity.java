package com.surefiz.screens.choose;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.surefiz.R;

import butterknife.ButterKnife;

public class ChooseActivity extends ChooseActivityView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_layout);
        ButterKnife.bind(this);
        chooseOnClick=new ChooseOnClick(ChooseActivity.this);
    }
}
