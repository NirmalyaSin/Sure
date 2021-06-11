package com.surefiz.screens.choose;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.surefiz.R;
import com.surefiz.dialog.CustomAlert;
import com.surefiz.screens.aboutus.AboutUsActivity;

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
                String urlvalue = "https://www.surefiz.com/Payment/basicinfo";
                Log.d("Wen_Link",":::"+urlvalue);

                Intent learnmoreintent = new Intent(ChooseActivity, AboutUsActivity.class);
                learnmoreintent.putExtra("url", urlvalue);
                learnmoreintent.putExtra("menu", false);
                learnmoreintent.putExtra("header", "Sign Up");
                ChooseActivity.startActivity(learnmoreintent);

                /*Intent intent1=new Intent(ChooseActivity, SignUpActivity.class);
                ChooseActivity.startActivity(intent1);*/
                break;

            case R.id.Btn_amazon:
                ChooseActivity.isAmazon=true;
                ChooseActivity.callAmazon();
                break;

           /* case R.id.Btn_surefiz:
                Intent intent=new Intent(ChooseActivity, LoginActivity.class);
                ChooseActivity.startActivity(intent);
                ChooseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;*/

            case R.id.Btn_nothing:
            case R.id.Btn_surefiz:
                ChooseActivity.isAmazon=false;
                ChooseActivity.callAmazon();
                break;
        }

    }


    private void showHowToSignupDialog(boolean b) {

        String showToSignText ="";
        if(b) {
            showToSignText = "<font color=#000000>For your weight management make the best decision and please visit </font>" + "<font color=#4A68EA><b><u>https://www.surefiz.com</u></b></font>"
                    + "<font color=#000000> and click at  SIGNUP</font>";
        }else{
            showToSignText = "<font color=#000000>Please reach SUPPORT</font>";
        }

        CustomAlert customAlert=new CustomAlert(ChooseActivity);
        customAlert.setSubText(""+Html.fromHtml(showToSignText));
        customAlert.show();
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
            }
        });
    }
}
