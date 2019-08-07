package com.surefiz.screens.instruction;

import android.content.Intent;
import android.view.View;

import com.surefiz.R;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;

public class InstructionActivityonclick implements View.OnClickListener {
    private InstructionActivity instructionActivity;

    public InstructionActivityonclick(InstructionActivity instructionActivity) {
        this.instructionActivity = instructionActivity;
        setonclicklistner();
    }

    private void setonclicklistner() {
        instructionActivity.btn_button.setOnClickListener(this);
        instructionActivity.btn_skip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btndone:
                /*if (instructionActivity.userLists.size() > 0) {
                    if (instructionActivity.userLists.get(0) != null) {
                        if (instructionActivity.userLists.get(0).getUserWeight().equals("0")) {
                            Intent loginIntent = new Intent(instructionActivity, DashBoardActivity.class);
                            instructionActivity.startActivity(loginIntent);
                            instructionActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            instructionActivity.finish();
                        } else {
                            Intent weightdetails = new Intent(instructionActivity, WeightDetailsActivity.class);
                            instructionActivity.startActivity(weightdetails);
                            instructionActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            instructionActivity.finish();
                        }
                    } else {
                        Intent weightdetails = new Intent(instructionActivity, WeightDetailsActivity.class);
                        instructionActivity.startActivity(weightdetails);
                        instructionActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        instructionActivity.finish();
                    }
                } else {
                    Intent weightdetails = new Intent(instructionActivity, WeightDetailsActivity.class);
                    instructionActivity.startActivity(weightdetails);
                    instructionActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    instructionActivity.finish();
                }*/


                Intent dashBoardIntent = new Intent(instructionActivity, DashBoardActivity.class);
                instructionActivity.startActivity(dashBoardIntent);
                instructionActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                instructionActivity.finish();
                break;
            case R.id.btn_skip:
                Intent details = new Intent(instructionActivity, DashBoardActivity.class);
                instructionActivity.startActivity(details);
                instructionActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                instructionActivity.finish();
                break;

        }
    }
}
