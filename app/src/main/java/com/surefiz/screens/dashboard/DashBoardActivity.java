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
        setHeaderView();
    }

    private void setHeaderView() {
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.VISIBLE);
    }
}
