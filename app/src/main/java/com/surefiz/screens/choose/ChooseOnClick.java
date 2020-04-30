package com.surefiz.screens.choose;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.surefiz.R;
import com.surefiz.screens.aboutus.AboutUsActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.signup.SignUpActivity;

public class ChooseOnClick implements View.OnClickListener {
    private ChooseActivity ChooseActivity;

    public ChooseOnClick(ChooseActivity ChooseActivity) {
        this.ChooseActivity=ChooseActivity;

        setOnClick();
    }

    private void setOnClick(){

        ChooseActivity.rl_back.setOnClickListener(this);
        ChooseActivity.Btn_amazon.setOnClickListener(this);
        ChooseActivity.Btn_subscribed.setOnClickListener(this);
        ChooseActivity.Btn_surefiz.setOnClickListener(this);
        ChooseActivity.Btn_nothing.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rl_back:
                ChooseActivity.finish();
                break;

            case R.id.Btn_subscribed:
                //showHowToSignupDialog(true);
                /*String urlvalue = "https://www.surefiz.com/Home/landing";
                Intent learnmoreintent = new Intent(ChooseActivity, AboutUsActivity.class);
                learnmoreintent.putExtra("url", urlvalue);
                learnmoreintent.putExtra("menu", false);
                learnmoreintent.putExtra("header", "Learn More");
                ChooseActivity.startActivity(learnmoreintent);*/

                Intent intent1=new Intent(ChooseActivity, SignUpActivity.class);
                ChooseActivity.startActivity(intent1);
                break;

            case R.id.Btn_amazon:
                ChooseActivity.callAmazon();
                break;

            case R.id.Btn_surefiz:
                Intent intent=new Intent(ChooseActivity, LoginActivity.class);
                ChooseActivity.startActivity(intent);

                break;

            case R.id.Btn_nothing:
                showHowToSignupDialog(false);
                break;
        }

    }


    private void showHowToSignupDialog(boolean b) {

        String showToSignText ="";
        if(b) {
            showToSignText = "<font color=#000000>For your weight management make the best decision and please visit </font>" + "<font color=#4A68EA><b><u>https://www.surefiz.com</u></b></font>"
                    + "<font color=#000000> and click at  SIGNUP</font>";
        }else{
            showToSignText = "<font color=#000000>Coming Soon</font>";
        }

        AlertDialog alertDialog = new AlertDialog.Builder(ChooseActivity).create();
        alertDialog.setTitle(ChooseActivity.getResources().getString(R.string.app_name));
        alertDialog.setMessage(Html.fromHtml(showToSignText));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> {
                    dialog.dismiss();
                });

        alertDialog.show();
    }
}
