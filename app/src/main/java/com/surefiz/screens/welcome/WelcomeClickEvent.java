package com.surefiz.screens.welcome;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.surefiz.R;
import com.surefiz.screens.aboutus.AboutUsActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.login.LoginNewActivity;

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

                /*String url = "https://www.surefiz.com/Signup";
                Intent howToSignUpintent = new Intent(welcomeActivity, AboutUsActivity.class);
                howToSignUpintent.putExtra("url", url);
                howToSignUpintent.putExtra("menu", false);
                howToSignUpintent.putExtra("header", "How to Sign up");
                welcomeActivity.startActivity(howToSignUpintent);*/

                showHowToSignupDialog();

                break;

            case R.id.txt_learn_more:
                //String urlvalue = "https://www.surefiz.com/InShort";
                String urlvalue = "https://www.surefiz.com/Home/landing";
                Intent learnmoreintent = new Intent(welcomeActivity, AboutUsActivity.class);
                learnmoreintent.putExtra("url", urlvalue);
                learnmoreintent.putExtra("menu", false);
                learnmoreintent.putExtra("header", "Learn More");
                welcomeActivity.startActivity(learnmoreintent);

                break;
        }
    }


    private void showHowToSignupDialog() {

        //For your weight management make the best decision and please visit  https://www.surefiz.com and click at  SIGNUP

        String showToSignText = "<font color=#000000>For your weight management make the best decision and please visit </font>" + "<font color=#4A68EA><b><u>https://www.surefiz.com</u></b></font>"
                +"<font color=#000000> and click at  SIGNUP</font>";

        AlertDialog alertDialog = new AlertDialog.Builder(welcomeActivity).create();
        alertDialog.setTitle(welcomeActivity.getResources().getString(R.string.app_name));
        alertDialog.setMessage(Html.fromHtml(showToSignText));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> {
                    dialog.dismiss();
                });

        alertDialog.show();
    }
}
