package com.surefiz.screens.weightdetails;

import android.view.View;

import com.surefiz.R;
import com.surefiz.utils.GeneralToApp;

import java.text.DecimalFormat;

public class WeightDetailsOnclick implements View.OnClickListener {
    WeightDetailsActivity weightDetailsActivity;
    int kg_lbs=5545;
    float kg=0;
    Double lbs=0.0;
    public WeightDetailsOnclick(WeightDetailsActivity weightDetailsActivity) {
        this.weightDetailsActivity=weightDetailsActivity;
        setonclicklistner();
    }

    private void setonclicklistner() {
        weightDetailsActivity.btn_kg.setOnClickListener(this);
        weightDetailsActivity.btn_lbs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_kg:
                weightDetailsActivity.btn_kg.setBackgroundResource(R.drawable.weight_blue_button);
                weightDetailsActivity.btn_lbs.setBackgroundResource(R.drawable.weight_white_button);
                weightDetailsActivity.btn_kg.setTextColor(weightDetailsActivity.getResources().getColor(R.color.registration_color_black));
                weightDetailsActivity.btn_lbs.setTextColor(weightDetailsActivity.getResources().getColor(R.color.whiteColor));
                convertinlbs();

                break;
            case R.id.btn_lbs:
                weightDetailsActivity.btn_kg.setBackgroundResource(R.drawable.weight_white_button);
                weightDetailsActivity.btn_lbs.setBackgroundResource(R.drawable.weight_blue_button);
                weightDetailsActivity.btn_lbs.setTextColor(weightDetailsActivity.getResources().getColor(R.color.registration_color_black));
                weightDetailsActivity.btn_kg.setTextColor(weightDetailsActivity.getResources().getColor(R.color.whiteColor));


                covertinkg();
                break;
        }
    }

    private void convertinlbs() {
        kg= (float)kg_lbs/100;
        weightDetailsActivity.tv_kg_lb_value.setText(new DecimalFormat("##.##").format(kg)+" kg");

    }

    private void covertinkg() {
        lbs=kg* GeneralToApp.KG_TO_LBS;
        weightDetailsActivity.tv_kg_lb_value.setText(new DecimalFormat("##.###").format(lbs)+" lbs");

    }
}
