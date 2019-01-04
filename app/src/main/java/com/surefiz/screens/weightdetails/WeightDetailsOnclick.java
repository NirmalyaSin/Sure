package com.surefiz.screens.weightdetails;

import android.view.View;

import com.surefiz.R;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;

import java.text.DecimalFormat;

public class WeightDetailsOnclick implements View.OnClickListener {
    private WeightDetailsActivity weightDetailsActivity;

    public WeightDetailsOnclick(WeightDetailsActivity weightDetailsActivity) {
        this.weightDetailsActivity = weightDetailsActivity;
        setonclicklistner();
        weightDetailsActivity.btn_kg.setBackgroundResource(R.drawable.weight_white_button);
        weightDetailsActivity.btn_lbs.setBackgroundResource(R.drawable.weight_blue_button);
        weightDetailsActivity.btn_lbs.setTextColor(weightDetailsActivity.getResources().getColor(R.color.whiteColor));
        weightDetailsActivity.btn_kg.setTextColor(weightDetailsActivity.getResources().getColor(R.color.whiteColor));
    }

    private void setonclicklistner() {
        weightDetailsActivity.btn_kg.setOnClickListener(this);
        weightDetailsActivity.btn_lbs.setOnClickListener(this);
        weightDetailsActivity.btn_go_next.setOnClickListener(this);
        weightDetailsActivity.btnSkipWeight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_kg:
                weightDetailsActivity.btn_kg.setBackgroundResource(R.drawable.weight_blue_button);
                weightDetailsActivity.btn_lbs.setBackgroundResource(R.drawable.weight_white_button);
                weightDetailsActivity.btn_kg.setTextColor(weightDetailsActivity.getResources().getColor(R.color.whiteColor));
                weightDetailsActivity.btn_lbs.setTextColor(weightDetailsActivity.getResources().getColor(R.color.whiteColor));
                //Convert weight to kg
                double weightKg = covertInKG(weightDetailsActivity.captureWeight);
                weightDetailsActivity.tv_kg_lb_value.setText(new DecimalFormat("##.##").format(weightKg) + " KG");

                break;
            case R.id.btn_lbs:

                weightDetailsActivity.btn_kg.setBackgroundResource(R.drawable.weight_white_button);
                weightDetailsActivity.btn_lbs.setBackgroundResource(R.drawable.weight_blue_button);
                weightDetailsActivity.btn_lbs.setTextColor(weightDetailsActivity.getResources().getColor(R.color.whiteColor));
                weightDetailsActivity.btn_kg.setTextColor(weightDetailsActivity.getResources().getColor(R.color.whiteColor));
                //Convert weight to lbs
                double weightLBS = covertInLBS(weightDetailsActivity.captureWeight);
                weightDetailsActivity.tv_kg_lb_value.setText(new DecimalFormat("##.##").format(weightLBS) + " LBS");

                break;

            case R.id.btn_go_next:
                LoginShared.setWeightPageFrom(weightDetailsActivity, "0");
                LoginShared.setDashboardPageFrom(weightDetailsActivity, "0");
                weightDetailsActivity.goToDashboard();
                break;

            case R.id.btnSkipWeight:
                weightDetailsActivity.goToDashboard();
                LoginShared.setWeightPageFrom(weightDetailsActivity, "0");
                LoginShared.setDashboardPageFrom(weightDetailsActivity, "0");
                break;
        }
    }

    public double covertInKG(int weight) {
        double kg = weight / 100;
        return kg;
    }

    private double covertInLBS(int weight) {
        double lbs = (weight / 100) * GeneralToApp.KG_TO_LBS;
        return lbs;
    }
}
