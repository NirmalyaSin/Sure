package com.surefiz.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.dialog.weightpopup.WeigtUniversalPopup;
import com.surefiz.interfaces.MoveTutorial;
import com.surefiz.interfaces.OnWeightCallback;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.wificonfig.WifiConfigActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddUserDialogForOTP extends Dialog {
    OtpActivity activity;
    ImageView iv_cross;
    EditText et_name;
    EditText et_email, et_phone, et_units, et_gender, et_DOB, et_height, et_weight, et_time_loss, et_middle, et_last, et_management, et_userselection;
    Button btn_submit;
    RelativeLayout rl_userselection, rl_weight, rl_time_loss;
    TextView tv_userSelection, tv_management, tv_weight, tv_time_loss, tv_termsnCon;
    private WeigtUniversalPopup managementPopup, selectionPopup;
    LoadingData loader;
    MoveTutorial moveTutorial;
    private int user_selection_val = 0;
    private int weight_managment_goal = 0;
    private UniversalPopup genderPopup, prefferedPopup, heightPopup, weightPopup, timePopup;
    private List<String> managementList = new ArrayList<>();
    private List<String> selectionList = new ArrayList<>();
    private List<String> genderList = new ArrayList<>();
    private List<String> prefferedList = new ArrayList<>();
    private List<String> heightList = new ArrayList<>();
    private List<String> weightList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private int month, year, day;

    public AddUserDialogForOTP(OtpActivity activity) {
        super(activity, R.style.DialogStyle);
        this.activity = activity;
        this.moveTutorial = moveTutorial;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_user);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        loader = new LoadingData(activity);
        iv_cross = findViewById(R.id.iv_cross);
        btn_submit = findViewById(R.id.btn_submit);
        et_name = findViewById(R.id.et_name);
        et_middle = findViewById(R.id.et_middle);
        et_last = findViewById(R.id.et_last);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_units = findViewById(R.id.et_units);
        et_gender = findViewById(R.id.et_gender);
        et_DOB = findViewById(R.id.et_DOB);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        et_time_loss = findViewById(R.id.et_time_loss);
        et_management = findViewById(R.id.et_management);
        et_userselection = findViewById(R.id.et_userselection);
        rl_userselection = findViewById(R.id.rl_userselection);
        tv_userSelection = findViewById(R.id.tv_userSelection);
        tv_management = findViewById(R.id.tv_management);
        tv_weight = findViewById(R.id.tv_weight);
        rl_weight = findViewById(R.id.rl_weight);
        tv_time_loss = findViewById(R.id.tv_time_loss);
        rl_time_loss = findViewById(R.id.rl_time_loss);
        tv_termsnCon = findViewById(R.id.tv_termsnCon);

        hideSoftKeyBoard();

        addGenderListAndCall();
        addPreferredListAndCall();
        addManagementListAndCall();
        addSelectionListAndCall();
        addHeightListAndCall("INCH");
        addWeightListAndCall("LB");
        addTimeListAndCall();

        et_units.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                heightList.clear();
                weightList.clear();
                if (et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }

                if (et_units.getText().toString().equals("KG/CM")) {
                    addWeightListAndCall("KG");
                } else {
                    addWeightListAndCall("LB");
                }
            }
        });

        iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter user name");
                } else if (et_last.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter User Last name");
                } else if (et_email.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter user email");
                } else if (!MethodUtils.isValidEmail(et_email.getText().toString())) {
                    MethodUtils.errorMsg(activity, "Please enter a valid email address");
                } else if (et_phone.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter your phone number");
                } else if (et_units.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please select your Preferred Units");
                } else if (et_management.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please select Weight Managment goal");
                } else if (et_gender.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please select any gender type");
                } else if (et_DOB.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please select your Age");
                } else if (et_height.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter your height");
                } else if (et_weight.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter your desired weight");
                } else if (et_time_loss.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please select your time to lose weight");
                } else if (!ConnectionDetector.isConnectingToInternet(activity)) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.no_internet));
                } else {
                    addUserApi();
                }
            }
        });

        et_DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyBoard();
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                }
                ExpiryDialog();

            }
        });
        et_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyBoard();
                if (et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }

                if (et_units.getText().toString().equals("KG/CM")) {
                    addWeightListAndCall("KG");
                } else {
                    addWeightListAndCall("LB");
                }
                if (prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissGenderPopup();
                        }
                    }, 100);
                }
            }
        });
        et_units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyBoard();
                if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissPreferredPopup();
                        }
                    }, 100);
                }

                et_weight.setText("");
                et_height.setText("");
                et_time_loss.setText("");

            }
        });
        et_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyBoard();
                heightList.clear();
                /*if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }*/
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissHeightPopup();
                        }
                    }, 100);
                }
            }
        });
        et_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyBoard();
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // if (!isFinishing()) {
                            //showCustomPopupforWeightmanagemnt();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // showAndDismissManagementPopup();
                                    //showCustomPopupforWeightmanagemnt();
                                    showAndDismissManagementPopup();
                                }
                            }, 100);
                        }
                        // }
                    });


                   /* new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           // showAndDismissManagementPopup();
                            showCustomPopupforWeightmanagemnt();
                        }
                    }, 100);*/
                }
            }
        });
        et_userselection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyBoard();
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissSelectionPopup();
                        }
                    }, 100);
                }
            }
        });
        et_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyBoard();
                weightList.clear();

                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissWeightPopup();
                        }
                    }, 100);
                }
            }
        });

        et_time_loss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyBoard();
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissTimePopup();
                        }
                    }, 100);
                }
            }
        });



    }





    private void addManagementListAndCall() {
        managementList.add("Lose and Mantain Weight");
        managementList.add("Maintain Current Weight");

        managementPopup = new WeigtUniversalPopup(activity, managementList, et_management, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                if (value.equals("Lose and Mantain Weight")) {
                    tv_userSelection.setVisibility(View.VISIBLE);
                    rl_userselection.setVisibility(View.VISIBLE);
                    tv_weight.setVisibility(View.VISIBLE);
                    rl_weight.setVisibility(View.VISIBLE);
                    tv_time_loss.setVisibility(View.VISIBLE);
                    rl_time_loss.setVisibility(View.VISIBLE);
                    weight_managment_goal = 2;
                    user_selection_val = 0;
                } else {
                    tv_userSelection.setVisibility(View.GONE);
                    rl_userselection.setVisibility(View.GONE);
                    tv_weight.setVisibility(View.GONE);
                    rl_weight.setVisibility(View.GONE);
                    tv_time_loss.setVisibility(View.GONE);
                    rl_time_loss.setVisibility(View.GONE);
                    et_userselection.setText("");
                    weight_managment_goal = 1;
                    user_selection_val = 0;
                }
            }
        });
    }

    private void addSelectionListAndCall() {
        selectionList.add("I Will Provide the Info");
        //selectionList.add("I want SureFiz™ to suggest");
        selectionList.add("I want "+activity.getResources().getString(R.string.app_name_splash) +" to suggest");

        selectionPopup = new WeigtUniversalPopup(activity, selectionList, et_userselection, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                if (value.equals("I Will Provide the Info")) {
                    tv_weight.setVisibility(View.VISIBLE);
                    rl_weight.setVisibility(View.VISIBLE);
                    tv_time_loss.setVisibility(View.VISIBLE);
                    rl_time_loss.setVisibility(View.VISIBLE);
                    user_selection_val = 1;
                } else {
                    tv_weight.setVisibility(View.GONE);
                    rl_weight.setVisibility(View.GONE);
                    tv_time_loss.setVisibility(View.GONE);
                    rl_time_loss.setVisibility(View.GONE);
                    user_selection_val = 2;
                }
            }
        });
    }

    private void showAndDismissTimePopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timePopup.showAsDropDown(et_time_loss);
            }
        }, 100);
    }

    private void showAndDismissSelectionPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                selectionPopup.showAsDropDown(et_userselection);
            }
        }, 100);
    }

    private void showAndDismissWeightPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                weightPopup.showAsDropDown(et_weight);
            }
        }, 100);
    }

    private void showAndDismissHeightPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                heightPopup.showAsDropDown(et_height);
            }
        }, 100);
    }

    private void showAndDismissManagementPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                managementPopup.showAsDropDown(et_management);
            }
        }, 100);
    }

    private void showAndDismissGenderPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                genderPopup.showAsDropDown(et_gender);
            }
        }, 100);
    }

    private void showAndDismissPreferredPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                prefferedPopup.showAsDropDown(et_units);
            }
        }, 100);
    }

    private void addUserApi() {
        String gender = "";
        String units = "";
        String type = "";
        String mantain_Weight_By_Server = "";
        String weight = "";
        String time = "";
        if (et_gender.getText().toString().trim().equals("Male")) {
            gender = "1";
        } else if (et_gender.getText().toString().trim().equals("Female")) {
            gender = "0";
        } else {
            gender = "2";
        }

        if (et_units.getText().toString().trim().equalsIgnoreCase("KG/CM")) {
            units = "0";
        } else {
            units = "1";
        }

        if (weight_managment_goal == 2) {
            type = "2";

            if (user_selection_val == 1) {
                mantain_Weight_By_Server = "0";
                weight = et_weight.getText().toString().trim();
                time = et_time_loss.getText().toString().trim();
            } else {
                mantain_Weight_By_Server = "1";
                weight = "";
                time = "";
            }
        } else {
            type = "1";
            mantain_Weight_By_Server = "0";
            weight = "";
            time = "";
        }


        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<ResponseBody> call_addUser = apiInterface.call_adduserApi(
                LoginShared.getRegistrationDataModel(activity).getData().getToken(),
                LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId(), LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserMac(),
                et_name.getText().toString().trim(), et_middle.getText().toString().trim(), et_last.getText().toString().trim(), et_email.getText().toString().trim(), time,
                et_height.getText().toString().trim(), weight, "12345678", gender, et_phone.getText().toString().trim(),
                et_DOB.getText().toString().trim(), "2", units,
                LoginShared.getDeviceToken(activity), type, mantain_Weight_By_Server);
        call_addUser.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jObject = jsonObject.getJSONObject("data");
                        dismiss();
                        activity.otpClickEvent.showUserAddDialog(
                                "User Added Successfully.Do you want to Add More Users?", "Yes", "No");
                        /*Intent dashBoardIntent = new Intent(activity, WifiConfigActivity.class);
                        activity.startActivity(dashBoardIntent);
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        activity.finish();*/
                        /*int scaleUserId = jObject.optInt("scaleUserId");
                        LoginShared.setScaleUserId(scaleUserId);
                        if (LoginShared.getDashboardPageFrom(activity).equals("0")) {
                            LoginShared.setWeightPageFrom(activity, "3");
                        } else {
                            LoginShared.setWeightPageFrom(activity, "2");
                        }

                        moveTutorial.onSuccess("1");*/
                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(activity);
                        LoginShared.destroySessionTypePreference(activity);
                        LoginShared.setDeviceToken(activity, deviceToken);
                        Intent loginIntent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(loginIntent);
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        activity.finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(activity, jsObject.getString("message"));
                    }
                } catch (Exception e) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(activity, activity.getString(R.string.error_occurred));
            }
        });
    }

    public void showConfirmDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(R.string.app_name_otp);
        alertDialog.setMessage("Do you want to Cancel?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dismiss();
                Intent dashBoardIntent = new Intent(activity, WifiConfigActivity.class);
                activity.startActivity(dashBoardIntent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                activity.finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.create();

        alertDialog.show();
    }

    private void ExpiryDialog() {

        Calendar mCalendar;
        mCalendar = Calendar.getInstance();
        System.out.println("Inside Dialog Box");
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_date_picker);
        dialog.show();
        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker);
        Button date_time_set = (Button) dialog.findViewById(R.id.date_time_set);
        datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), null);
        datePicker.setMaxDate(System.currentTimeMillis());
        LinearLayout ll = (LinearLayout) datePicker.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

       /* if (currentapiVersion > 23) {
            ll2.getChildAt(1).setVisibility(View.GONE);
        } else if (currentapiVersion == 23) {
            ll2.getChildAt(0).setVisibility(View.GONE);
        } else {
            ll2.getChildAt(1).setVisibility(View.GONE);
        }*/

        date_time_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                month = datePicker.getMonth() + 1;
                year = datePicker.getYear();
                day = datePicker.getDayOfMonth();
                String monthInString = "" + month;
                String dayInString = "" + day;
                if (monthInString.length() == 1)
                    monthInString = "0" + monthInString;
                if (dayInString.length() == 1) {
                    dayInString = "0" + dayInString;
                }
                et_DOB.setText(dayInString + "-" + monthInString + "-" + year);
                if (et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }

                if (et_units.getText().toString().equals("KG/CM")) {
                    addWeightListAndCall("KG");
                } else {
                    addWeightListAndCall("LB");
                }
            }
        });
    }

    private void addTimeListAndCall() {
        for (int i = 1; i < 261; i++) {
            timeList.add(i + " " + "Weeks");
        }
        timePopup = new UniversalPopup(activity, timeList, et_time_loss);
    }

    private void addHeightListAndCall(String change) {
        heightList.clear();
        if (change.equals("INCH")) {
            for (int i = 1; i < 109; i++) {
                heightList.add(i + " " + change);
            }
        } else {
            for (int i = 1; i < 276; i++) {
                heightList.add(i + " " + change);
            }
        }
        heightPopup = new UniversalPopup(activity, heightList, et_height);
    }

    private void addWeightListAndCall(String change) {
        weightList.clear();
        if (change.equals("LB")) {
            for (int i = 5; i < 301; i++) {
                weightList.add(i + " " + change);
            }
        } else {
            for (int i = 5; i < 151; i++) {
                weightList.add(i + " " + change);
            }
        }
        weightPopup = new UniversalPopup(activity, weightList, et_weight);
    }

    private void addPreferredListAndCall() {
        prefferedList.add("LB/INCH");
        prefferedList.add("KG/CM");

        prefferedPopup = new UniversalPopup(activity, prefferedList, et_units);
    }

    private void addGenderListAndCall() {
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Others");

        genderPopup = new UniversalPopup(activity, genderList, et_gender);
    }

    private void hideSoftKeyBoard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
