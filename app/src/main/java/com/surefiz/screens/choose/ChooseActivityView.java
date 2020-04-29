package com.surefiz.screens.choose;

import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surefiz.R;

import butterknife.BindView;

public class ChooseActivityView extends AppCompatActivity {

    @BindView(R.id.rl_back)
    RelativeLayout rl_back;

    @BindView(R.id.Btn_subscribed)
    TextView Btn_subscribed;

    @BindView(R.id.Btn_amazon)
    TextView Btn_amazon;

    @BindView(R.id.Btn_nothing)
    TextView Btn_nothing;

    @BindView(R.id.Btn_surefiz)
    TextView Btn_surefiz;

    protected ChooseOnClick chooseOnClick;

}
