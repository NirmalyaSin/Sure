package com.surefiz.screens.dashboard;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.profile.ProfileActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.base_rl_contentview)
    public RelativeLayout base_rl_contentview;
    @BindView(R.id.img_topbar_menu_profile)
    public CircleImageView img_topbar_menu_profile;
    @BindView(R.id.tv_user_name)
    public TextView tv_user_name;
    @BindView(R.id.tv_dashboard)
    public TextView tv_dashboard;
    @BindView(R.id.tv_weight)
    public TextView tv_weight;
    @BindView(R.id.tv_users)
    public TextView tv_users;
    @BindView(R.id.tv_profile)
    public TextView tv_profile;
    @BindView(R.id.tv_circle)
    public TextView tv_circle;
    @BindView(R.id.tv_message)
    public TextView tv_message;
    @BindView(R.id.tv_reminder)
    public TextView tv_reminder;
    @BindView(R.id.tv_notification)
    public TextView tv_notification;
    @BindView(R.id.tv_settings)
    public TextView tv_settings;
    @BindView(R.id.tv_about)
    public TextView tv_about;
    @BindView(R.id.tv_signout)
    public TextView tv_signout;
    @BindView(R.id.img_topbar_menu)
    public ImageButton img_topbar_menu;
    @BindView(R.id.tv_universal_header)
    public TextView tv_universal_header;
    @BindView(R.id.iv_edit)
    public ImageView iv_edit;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        clickEvent();
        initializeDrawer();
    }

    private void clickEvent() {
        img_topbar_menu.setOnClickListener(this);
        tv_profile.setOnClickListener(this);
    }

    public void addContentView(View view) {
        base_rl_contentview.removeAllViews();
        base_rl_contentview.addView(view,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
    }

    /**
     * Initialize side menu
     */
    private void initializeDrawer() {
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, R.string.app_name
        ) {
            @Override
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        initializeDrawerToggle(mDrawerToggle);
    }

    public void initializeDrawerToggle(ActionBarDrawerToggle mDrawerToggle) {
        this.mDrawerToggle = mDrawerToggle;
    }

    /**
     * To check whether the side menu is open or not
     */
    private boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null)
            mDrawerToggle.onConfigurationChanged(newConfig);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_topbar_menu:
                if (isDrawerOpen())
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.tv_profile:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent loginIntent = new Intent(this, ProfileActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
        }
    }
}
