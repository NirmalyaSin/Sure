package com.surefiz.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.surefiz.R;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.utils.MessagDateConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class NotificationHandleClassOnForeground extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String dateStr = getIntent().getStringExtra("lastServerUpdateDate") + " " + getIntent().getStringExtra("lastServerUpdateTime");

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");



        try {
            Date date = dateFormat.parse(MessagDateConverter.getConvertedNotificationDate(dateStr));
            Date currentDate = new Date();
            long diff = currentDate.getTime() - date.getTime();

            int diffSecond = (int) (diff / 1000);
            if (diffSecond < 120) {
                Intent intent = new Intent(this, WeightDetailsActivity.class);
                intent.putExtra("timerValue", diffSecond);
                intent.putExtra("fromPush", "1");
                if (getIntent().getBooleanExtra("shouldOpenWeightAssignView",false)) {
                    intent.putExtra("shouldOpenWeightAssignView", true);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else {
                Intent intent = new Intent(this, DashBoardActivity.class);
                intent.putExtra("expired", "1");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
            /*} else {
                Intent intent = new Intent(this, DashBoardActivity.class);
                intent.putExtra("expired", "1");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }*/
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
