package com.surefiz.screens.bmidetails;

import android.view.View;

import com.surefiz.R;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;

import java.text.DecimalFormat;

public class BMIDetailsOnclick implements View.OnClickListener {
    private BMIDetailsActivity mBMIDetailsActivity;

    public BMIDetailsOnclick(BMIDetailsActivity bmiDetailsActivity) {
        this.mBMIDetailsActivity=bmiDetailsActivity;
        setonclicklistner();
        mBMIDetailsActivity.btn_kg.setBackgroundResource(R.drawable.weight_white_button);
        mBMIDetailsActivity.btn_lbs.setBackgroundResource(R.drawable.weight_blue_button);
        mBMIDetailsActivity.btn_lbs.setTextColor(mBMIDetailsActivity.getResources().getColor(R.color.whiteColor));
        mBMIDetailsActivity.btn_kg.setTextColor(mBMIDetailsActivity.getResources().getColor(R.color.whiteColor));
    }

    private void setonclicklistner() {
        mBMIDetailsActivity.btn_kg.setOnClickListener(this);
        mBMIDetailsActivity.btn_lbs.setOnClickListener(this);
        mBMIDetailsActivity.btn_go_next.setOnClickListener(this);
        mBMIDetailsActivity.btnSkipWeight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_kg:
                mBMIDetailsActivity.btn_kg.setBackgroundResource(R.drawable.weight_blue_button);
                mBMIDetailsActivity.btn_lbs.setBackgroundResource(R.drawable.weight_white_button);
                mBMIDetailsActivity.btn_kg.setTextColor(mBMIDetailsActivity.getResources().getColor(R.color.whiteColor));
                mBMIDetailsActivity.btn_lbs.setTextColor(mBMIDetailsActivity.getResources().getColor(R.color.whiteColor));
                //Convert weight to kg
                double weightKg = covertInKG(mBMIDetailsActivity.capturedBMIWeight);
                mBMIDetailsActivity.tv_kg_lb_value.setText(new DecimalFormat("##.##").format(weightKg)+" KG");

                break;
            case R.id.btn_lbs:

                mBMIDetailsActivity.btn_kg.setBackgroundResource(R.drawable.weight_white_button);
                mBMIDetailsActivity.btn_lbs.setBackgroundResource(R.drawable.weight_blue_button);
                mBMIDetailsActivity.btn_lbs.setTextColor(mBMIDetailsActivity.getResources().getColor(R.color.whiteColor));
                mBMIDetailsActivity.btn_kg.setTextColor(mBMIDetailsActivity.getResources().getColor(R.color.whiteColor));
                //Convert weight to lbs
           //     double weightLBS = covertInLBS(mBMIDetailsActivity.capturedBMIWeight);
                mBMIDetailsActivity.tv_kg_lb_value.setText(new DecimalFormat("##.##").format(mBMIDetailsActivity.capturedBMIWeight)+" LBS");

                break;

            case R.id.btn_go_next:
                mBMIDetailsActivity.goToDashboard();
                LoginShared.setWeightPageFrom(mBMIDetailsActivity,"0");
                LoginShared.setDashboardPageFrom(mBMIDetailsActivity,"0");
                break;

            case R.id.btnSkipWeight:
                mBMIDetailsActivity.goToDashboard();
                LoginShared.setWeightPageFrom(mBMIDetailsActivity,"0");
                LoginShared.setDashboardPageFrom(mBMIDetailsActivity,"0");
                break;
        }
    }

    public double covertInKG(Double weight) {
        double kg = weight*(0.453592);
        return kg;
    }

  /*  private double covertInLBS(Double weight) {
        double lbs=(weight/100)* GeneralToApp.KG_TO_LBS;
        return lbs;
    }*/
}
