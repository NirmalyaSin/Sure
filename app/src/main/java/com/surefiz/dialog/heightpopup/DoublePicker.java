package com.surefiz.dialog.heightpopup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.surefiz.R;

public class DoublePicker extends NumberPicker {

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private int decimals;
    private EditText editText;
    private TextView txtNumber,txtFraction;
    private String change;
    private NumberPicker integerPicker;
    private NumberPicker fractionPicker;

    public DoublePicker(Context context,EditText editText,String change) {
        super(context);

        this.editText=editText;
        this.change=change;

        builder= new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.double_picker, null);
        integerPicker =  view.findViewById(R.id.integer_picker);
        fractionPicker =  view.findViewById(R.id.fraction_picker);
        txtNumber =  view.findViewById(R.id.txtNumber);
        txtFraction =  view.findViewById(R.id.txtFraction);

        setupPickerLogic();

        builder.setCancelable(false);
        builder.setView(view).setPositiveButton("Done",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("HEIGHT",":::::::"+integerPicker.getValue() + "."+fractionPicker.getValue());

                setValue(integerPicker.getValue(),fractionPicker.getValue());

                alertDialog.dismiss();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                alertDialog.dismiss();

            }
        });

        alertDialog = builder.create();
    }


    private void setupPickerLogic(){

        if(change.equals("INCH")){

            integerPicker.setMinValue(1);
            integerPicker.setMaxValue(7);

            fractionPicker.setMinValue(0);
            fractionPicker.setMaxValue(11);

            txtNumber.setText("FT");
            txtFraction.setText("INCH");

        }else{
            integerPicker.setMinValue(1);
            integerPicker.setMaxValue(3);

            fractionPicker.setMinValue(0);
            fractionPicker.setMaxValue(99);

            txtNumber.setText("Metre");
            txtFraction.setText("CM");
        }
    }

    private void setValue(int v1, int v2){
        if(change.equals("INCH")){

            int result=v1*12+v2;
            editText.setText(result+" INCH");

        }else{

            int result=v1*100+v2;
            editText.setText(result+" CM");
        }
    }
    public void Show(){

        if(alertDialog!=null)
            alertDialog.show();
    }
    public void Dismiss(){

        if(alertDialog!=null)
            alertDialog.dismiss();
    }

    public boolean isShowing(){
        return alertDialog.isShowing();
    }
}