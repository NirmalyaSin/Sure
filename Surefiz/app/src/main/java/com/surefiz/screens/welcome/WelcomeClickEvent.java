package com.surefiz.screens.welcome;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.surefiz.R;
import com.surefiz.screens.aboutus.AboutUsActivity;
import com.surefiz.screens.choose.ChooseActivity;
import com.surefiz.screens.choose.ChooseActivityView;
import com.surefiz.screens.login.LoginActivity;

public class WelcomeClickEvent implements View.OnClickListener {
    WelcomeActivity welcomeActivity;

    public WelcomeClickEvent(WelcomeActivity welcomeActivity) {
        this.welcomeActivity = welcomeActivity;
        setOnClick();
    }

    private void setOnClick() {

        welcomeActivity.tv_login.setOnClickListener(this);
        welcomeActivity.tv_signup.setOnClickListener(this);
        welcomeActivity.tv_learn_more.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txt_login:
                Intent loginintent = new Intent(welcomeActivity, LoginActivity.class);
                welcomeActivity.startActivity(loginintent);
                break;

            case R.id.tv_signup:

                Intent intent=new Intent(welcomeActivity, ChooseActivity.class);
                welcomeActivity.startActivity(intent);
                welcomeActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


                break;

            case R.id.txt_learn_more:
                String urlvalue = "https://www.surefiz.com/Home/landing";
                Log.d("Web_Link",":::"+urlvalue);

                Intent learnmoreintent = new Intent(welcomeActivity, AboutUsActivity.class);
                learnmoreintent.putExtra("url", urlvalue);
                learnmoreintent.putExtra("menu", false);
                learnmoreintent.putExtra("header", "Learn More");
                welcomeActivity.startActivity(learnmoreintent);

                break;
        }
    }
}
