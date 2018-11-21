package com.surefiz.screens.instruction;

import android.content.Intent;
import android.view.View;

import com.surefiz.R;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;

public class InstructionActivityonclick  implements  View.OnClickListener{
    InstructionActivity instructionActivity;
    public InstructionActivityonclick(InstructionActivity instructionActivity) {
        this.instructionActivity=instructionActivity;
        setonclicklistner();
    }

    private void setonclicklistner() {
        instructionActivity.btn_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btndone:
                Intent weightdetails=new Intent(instructionActivity, WeightDetailsActivity.class);
                instructionActivity.startActivity(weightdetails);
                instructionActivity.finish();
                break;
        }
    }
}
