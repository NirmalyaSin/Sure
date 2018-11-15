package com.surefiz.registration;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.surefiz.R;
import com.surefiz.dialog.universalpopup.UniversalPopup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegistrationClickEvent implements View.OnClickListener {
    RegistrationActivity registrationActivity;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    public String prefferedValue = "";
    private List<String> genderList = new ArrayList<>();
    private List<String> prefferedList = new ArrayList<>();
    private List<String> heightList = new ArrayList<>();
    private List<String> weightList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private UniversalPopup genderPopup, prefferedPopup, heightPopup, weightPopup, timePopup;

    public RegistrationClickEvent(RegistrationActivity registrationActivity) {
        this.registrationActivity = registrationActivity;
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        addGenderListAndCall();
        addPrefferedListAndCall();
        addHeightListAndCall("INCH");
        addWeightListAndCall("LB");
        addTimeListAndCall();
        setClickEvent();
    }

    private void addTimeListAndCall() {
        for (int i = 40; i < 301; i++) {
            timeList.add(i + " " + "Weeks");
        }
        timePopup = new UniversalPopup(registrationActivity, timeList, registrationActivity.et_time_loss);
    }

    private void addHeightListAndCall(String change) {

        for (int i = 1; i < 241; i++) {
            heightList.add(i + " " + change);
        }
        heightPopup = new UniversalPopup(registrationActivity, heightList, registrationActivity.et_height);
    }

    private void addWeightListAndCall(String change) {
        for (int i = 5; i < 141; i++) {
            weightList.add(i + " " + change);
        }
        weightPopup = new UniversalPopup(registrationActivity, weightList, registrationActivity.et_weight);
    }

    private void addPrefferedListAndCall() {
        prefferedList.add("KG/CM");
        prefferedList.add("LB/INCH");

        prefferedPopup = new UniversalPopup(registrationActivity, prefferedList, registrationActivity.et_units);
    }

    private void addGenderListAndCall() {
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Others");

        genderPopup = new UniversalPopup(registrationActivity, genderList, registrationActivity.et_gender);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        registrationActivity.et_DOB.setText(sdf.format(myCalendar.getTime()));
    }

    private void setClickEvent() {
        registrationActivity.tv_upload.setOnClickListener(this);
        registrationActivity.et_DOB.setOnClickListener(this);
        registrationActivity.et_gender.setOnClickListener(this);
        registrationActivity.et_units.setOnClickListener(this);
        registrationActivity.et_height.setOnClickListener(this);
        registrationActivity.et_weight.setOnClickListener(this);
        registrationActivity.et_time_loss.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_upload:
                Toast.makeText(registrationActivity, "Hello", Toast.LENGTH_SHORT).show();
                break;
            case R.id.et_DOB:
                new DatePickerDialog(registrationActivity, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.et_gender:
                hideSoftKeyBoard();
                if (prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup.isShowing()) {
                    timePopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissGenderPopup();
                        }
                    }, 100);
                }
                break;
            case R.id.et_units:
                hideSoftKeyBoard();
                if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissPrefferedPopup();
                        }
                    }, 100);
                }

                registrationActivity.et_weight.setText("");
                registrationActivity.et_height.setText("");
                registrationActivity.et_time_loss.setText("");

                break;
            case R.id.et_height:
                heightList.clear();
                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissHeightPopup();
                        }
                    }, 100);
                }
                break;
            case R.id.et_weight:
                weightList.clear();
                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addWeightListAndCall("KG");
                } else {
                    addWeightListAndCall("LB");
                }

                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissWeightPopup();
                        }
                    }, 100);
                }
                break;
            case R.id.et_time_loss:
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissTimePopup();
                        }
                    }, 100);
                }
                break;

        }
    }

    private void showAndDismissTimePopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timePopup.showAsDropDown(registrationActivity.et_time_loss);
            }
        }, 100);
    }

    private void showAndDismissWeightPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                weightPopup.showAsDropDown(registrationActivity.et_weight);
            }
        }, 100);
    }

    private void showAndDismissHeightPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                heightPopup.showAsDropDown(registrationActivity.et_height);
            }
        }, 100);
    }

    private void showAndDismissGenderPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                genderPopup.showAsDropDown(registrationActivity.et_gender);
            }
        }, 100);
    }

    private void showAndDismissPrefferedPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                prefferedPopup.showAsDropDown(registrationActivity.et_units);
            }
        }, 100);
    }

    private void hideSoftKeyBoard() {
        View view = registrationActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) registrationActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
