package com.surefiz.screens.instruction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.surefiz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstructionActivity extends AppCompatActivity {
    @BindView(R.id.btndone)
    Button btn_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        ButterKnife.bind(this);
        InstructionActivityonclick instructionActivityonclick=new InstructionActivityonclick(this);
    }
}
