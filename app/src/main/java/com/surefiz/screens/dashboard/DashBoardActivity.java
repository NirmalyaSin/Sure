package com.surefiz.screens.dashboard;

import android.os.Bundle;
import android.view.View;

import com.surefiz.R;

public class DashBoardActivity extends BaseActivity {

    public View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_dash_board, null);
        addContentView(view);
        iv_edit.setVisibility(View.GONE);
    }
}
