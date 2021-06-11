package com.surefiz.screens.instruction;

import android.content.Intent;
import android.view.View;

import com.surefiz.R;
import com.surefiz.screens.dashboard.DashBoardActivity;

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

                Intent dashBoardIntent = new Intent(instructionActivity, DashBoardActivity.class);
                instructionActivity.startActivity(dashBoardIntent);
                instructionActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                instructionActivity.finishAffinity();
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
