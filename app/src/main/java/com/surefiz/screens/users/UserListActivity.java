package com.surefiz.screens.users;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.surefiz.R;
import com.surefiz.screens.dashboard.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListActivity extends BaseActivity {

    @BindView(R.id.rv_items)
    RecyclerView rv_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);
        setHeaderView();
    }

    private void setHeaderView() {
        tv_universal_header.setText("List of Users");
    }
}
