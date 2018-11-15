package com.surefiz.login;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.designhelper.ResolutionHelper;

public class LoginViewAndResponsive extends ResolutionHelper {
    Activity activity;
    View view;
    RelativeLayout rl_header;
    TextView tv_login;

    public LoginViewAndResponsive(Activity activity, View view) {
        super(activity);
        this.activity = activity;
        this.view = view;

        setViewBind();
        setSize();
        setPadding();
        setTextSize();

    }

    private void setViewBind() {
        rl_header = view.findViewById(R.id.rl_header);

        tv_login = view.findViewById(R.id.tv_login);

    }

    private void setSize() {
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getHeight(0.1));
        rl_header.setLayoutParams(rlParams);

    }

    private void setPadding() {

    }

    private void setTextSize() {

        tv_login.setTextSize(getTextSize(4));

    }
}
