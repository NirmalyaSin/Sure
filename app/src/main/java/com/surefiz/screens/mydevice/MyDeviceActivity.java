package com.surefiz.screens.mydevice;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.surefiz.R;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.sharedhandler.LoginShared;

public class MyDeviceActivity extends BaseActivity {
    public View view;
    Button btn_update;
    EditText et_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_my_device, null);
        addContentView(view);
        setHeaderView();
        setViewBind();
        showButton();

    }

    private void setViewBind() {
        btn_update = findViewById(R.id.btn_update);
        et_id = findViewById(R.id.et_id);
    }

    private void showButton() {
        et_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {

                    public void run() {
//        ((EditText) findViewById(R.id.et_find)).requestFocus();
//
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);

                        btn_update.setVisibility(View.VISIBLE);
                        et_id.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                        et_id.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));
                    }
                }, 200);
            }
        });
    }

    private void setHeaderView() {
        tv_universal_header.setText("Device Binding");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.GONE);
    }
}
