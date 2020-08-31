package com.surefiz.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.surefiz.R;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.utils.MessagDateConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NotificationHandleClassOnForeground extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().hasExtra("userWeight"))
        if(getIntent().getIntExtra("userWeight",0)==0){

            Intent intent = new Intent(this, DashBoardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();

        }else{

            String dateStr = getIntent().getStringExtra("lastServerUpdateDate") + " " + getIntent().getStringExtra("lastServerUpdateTime");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

            try {
                Date date = dateFormat.parse(MessagDateConverter.getConvertedNotificationDate(dateStr));
                Date currentDate = new Date();
                long diff = currentDate.getTime() - date.getTime();

                //*********************AVIK

                //Log.d("Time_Diff",":::::"+currentDate.getTime()+"-"+date.getTime()+"="+diff);

                int diffSecond = (int) (diff / 1000);
                if (diffSecond < 120) {
                    int remainingTime=120-diffSecond;
                    Log.e("Remaining-Time",":::::::::::::"+remainingTime);
                    Intent intent = new Intent(this, WeightDetailsActivity.class);
                    intent.putExtra("timerValue", remainingTime);
                    intent.putExtra("fromPush", "1");
                    if (getIntent().getBooleanExtra("shouldOpenWeightAssignView",false)) {
                        intent.putExtra("shouldOpenWeightAssignView", true);
                        intent.putExtra("userWeight", getIntent().getIntExtra("userWeight",0));
                        intent.putExtra("scaleMacAddress", getIntent().getStringExtra("scaleMacAddress"));
                        intent.putExtra("text", getIntent().getStringExtra("text"));
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    Intent intent = new Intent(this, DashBoardActivity.class);
                    intent.putExtra("expired", "1");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

}
