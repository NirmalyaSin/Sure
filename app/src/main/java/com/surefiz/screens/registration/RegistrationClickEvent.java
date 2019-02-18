package com.surefiz.screens.registration;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.OpenCameraOrGalleryDialog;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistrationClickEvent implements View.OnClickListener {
    RegistrationActivity registrationActivity;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    private List<String> genderList = new ArrayList<>();
    private List<String> prefferedList = new ArrayList<>();
    private List<String> heightList = new ArrayList<>();
    private List<String> weightList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private List<String> managementList = new ArrayList<>();
    private String filePath = "";
    private LoadingData loader;
    private int month, year, day;
    private UniversalPopup genderPopup, prefferedPopup, heightPopup, weightPopup, timePopup, managementPopup;

    public RegistrationClickEvent(RegistrationActivity registrationActivity) {
        this.registrationActivity = registrationActivity;
        loader = new LoadingData(registrationActivity);
        addGenderListAndCall();
        addPrefferedListAndCall();
        addManagementListAndCall();
        addHeightListAndCall("INCH");
        addWeightListAndCall("LB");
        addTimeListAndCall();
        setClickEvent();

        registrationActivity.et_units.addTextChangedListener(new TextWatcher() {
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
                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }

                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addWeightListAndCall("KG");
                } else {
                    addWeightListAndCall("LB");
                }
            }
        });
    }

    private void addManagementListAndCall() {
        managementList.add("Lose and Mantain Weight");
        managementList.add("Maintain Current Weight");

        managementPopup = new UniversalPopup(registrationActivity, managementList, registrationActivity.et_management);
    }

    private void addTimeListAndCall() {
        for (int i = 1; i < 261; i++) {
            timeList.add(i + " " + "Weeks");
        }
        timePopup = new UniversalPopup(registrationActivity, timeList, registrationActivity.et_time_loss);
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
        heightPopup = new UniversalPopup(registrationActivity, heightList, registrationActivity.et_height);
    }

    private void addWeightListAndCall(String change) {
        weightList.clear();
        if (change.equals("LB")) {
            for (int i = 5; i < 1001; i++) {
                weightList.add(i + " " + change);
            }
        } else {
            for (int i = 5; i < 455; i++) {
                weightList.add(i + " " + change);
            }
        }
        weightPopup = new UniversalPopup(registrationActivity, weightList, registrationActivity.et_weight);
    }

    private void addPrefferedListAndCall() {
        prefferedList.add("LB/INCH");
        prefferedList.add("KG/CM");

        prefferedPopup = new UniversalPopup(registrationActivity, prefferedList, registrationActivity.et_units);
    }

    private void addGenderListAndCall() {
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Others");

        genderPopup = new UniversalPopup(registrationActivity, genderList, registrationActivity.et_gender);
    }

    private void setClickEvent() {
        registrationActivity.tv_upload.setOnClickListener(this);
        //registrationActivity.et_DOB.setOnClickListener(this);
        registrationActivity.et_gender.setOnClickListener(this);
        registrationActivity.et_units.setOnClickListener(this);
        registrationActivity.et_height.setOnClickListener(this);
        registrationActivity.et_weight.setOnClickListener(this);
        registrationActivity.et_time_loss.setOnClickListener(this);
        registrationActivity.profile_image.setOnClickListener(this);
        registrationActivity.iv_plus_add_image.setOnClickListener(this);
        registrationActivity.btn_register.setOnClickListener(this);
        registrationActivity.rl_back.setOnClickListener(this);
        registrationActivity.et_management.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_upload:
                break;
            case R.id.btn_register:
                validationAndApiCall();
                break;
            case R.id.rl_back:
                registrationActivity.onBackPressed();
                break;
            case R.id.profile_image:
                new OpenCameraOrGalleryDialog(registrationActivity, new OnImageSet() {
                    @Override
                    public void onSuccess(String path) {
                        filePath = path;
                    }
                }, "0").show();
                break;
            case R.id.iv_plus_add_image:
                new OpenCameraOrGalleryDialog(registrationActivity, new OnImageSet() {
                    @Override
                    public void onSuccess(String path) {
                        filePath = path;
                    }
                }, "0").show();
                break;
            case R.id.et_DOB:
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
                }else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                }
                ExpiryDialog();
                break;
            case R.id.et_gender:
                hideSoftKeyBoard();
                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }

                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
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
                } else if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
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
                hideSoftKeyBoard();
//                heightList.clear();
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
                hideSoftKeyBoard();
//                weightList.clear();

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
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissWeightPopup();
                        }
                    }, 100);
                }
                break;

            case R.id.et_management:
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
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissManagementPopup();
                        }
                    }, 100);
                }

                break;
            case R.id.et_time_loss:
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
                }else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                }  else {
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

    private void ExpiryDialog() {

        Calendar mCalendar;
        mCalendar = Calendar.getInstance();
        System.out.println("Inside Dialog Box");
        final Dialog dialog = new Dialog(registrationActivity);
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
                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }

                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addWeightListAndCall("KG");
                } else {
                    addWeightListAndCall("LB");
                }
                registrationActivity.et_DOB.setText(dayInString + "-" + monthInString + "-" + year);

            }
        });
    }


    private void validationAndApiCall() {
        if (registrationActivity.et_full.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your name");
        } else if (registrationActivity.et_email.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your email");
        } else if (!MethodUtils.isValidEmail(registrationActivity.et_email.getText().toString())) {
            MethodUtils.errorMsg(registrationActivity, "Please enter a valid email address");
        } else if (registrationActivity.et_password.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your password");
        } else if (registrationActivity.et_phone.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your phone number");
        } else if (registrationActivity.et_scale.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your scale Id");
        }else if(registrationActivity.et_management.getText().toString().equals("")){
            MethodUtils.errorMsg(registrationActivity, "Please select any option");
        } else if (registrationActivity.et_units.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please select your Preffered Units");
        } else if (registrationActivity.et_gender.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please select any gender type");
        } else if (registrationActivity.et_DOB.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please select your Age");
        } else if (registrationActivity.et_height.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your height");
        } else if (registrationActivity.et_weight.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your desired weight");
        } else if (registrationActivity.et_time_loss.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please select your time to lose weight");
        } else if (!lengthCheck(registrationActivity.et_password.getText().toString().trim())) {
            MethodUtils.errorMsg(registrationActivity, "Password must be more than 8 characters");
        } else if (!lengthScale(registrationActivity.et_scale.getText().toString().trim())) {
            MethodUtils.errorMsg(registrationActivity, "Scale id must be contains 10 digit numeric number");
        } else if (!ConnectionDetector.isConnectingToInternet(registrationActivity)) {
            MethodUtils.errorMsg(registrationActivity, registrationActivity.getString(R.string.no_internet));
        } else if (!registrationActivity.checkBoxTermsCondition.isChecked()) {
            MethodUtils.errorMsg(registrationActivity, "Please accept Terms & Condition.");
        } else {
            if (registrationActivity.mCompressedFile != null) {
                callRegistrationApiWithImage();
            } else {
                callRegistrationApi();
            }
        }
    }

    private boolean lengthCheck(final String password) {
        return password.length() >= 8 && password.length() <= 20;
    }

    private boolean lengthScale(final String scale) {
        return scale.length() >= 10;
    }

    private void callRegistrationApi() {
        loader.show_with_label("Loading");
        RequestBody gender = null;
        RequestBody units = null;
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_full.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_email.getText().toString().trim());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_password.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_phone.getText().toString().trim());
        RequestBody scale = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_scale.getText().toString().trim());
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_management.getText().toString().trim());
        if (registrationActivity.et_units.getText().toString().trim().equalsIgnoreCase("KG/CM")) {
            units = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            units = RequestBody.create(MediaType.parse("text/plain"), "0");
        }
        if (registrationActivity.et_gender.getText().toString().trim().equals("Male")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else if (registrationActivity.et_gender.getText().toString().trim().equals("Female")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "0");
        } else {
            gender = RequestBody.create(MediaType.parse("text/plain"), "2");
        }
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_DOB.getText().toString().trim());
        RequestBody height = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_height.getText().toString().trim());
        RequestBody weight = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_weight.getText().toString().trim());
        RequestBody time = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_time_loss.getText().toString().trim());
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "2");
        RequestBody deviceToken = RequestBody.create(MediaType.parse("text/plain"), LoginShared.getDeviceToken(registrationActivity));

        Call<ResponseBody> registration_api = apiInterface.call_registrationApi(fullName, email, password, gender, phone, dob,
                height, weight, time, units, scale,type, deviceType, deviceToken);

        registration_api.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    RegistrationModel registrationModel;

                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.optInt("status") == 1) {
                        registrationModel = gson.fromJson(responseString, RegistrationModel.class);
                        LoginShared.setRegistrationDataModel(registrationActivity, registrationModel);
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        LoginShared.setUserPhoto(registrationActivity, LoginShared.getRegistrationDataModel(registrationActivity).getData().getUser().get(0).getUserPhoto());
                        LoginShared.setUserName(registrationActivity, LoginShared.getRegistrationDataModel(registrationActivity).getData().getUser().get(0).getUserName());
                        MethodUtils.errorMsg(registrationActivity, jsObject.getString("message"));

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent = new Intent(registrationActivity, OtpActivity.class);
                                registrationActivity.startActivity(loginIntent);
                                registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                registrationActivity.finish();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);
                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(registrationActivity);
                        LoginShared.destroySessionTypePreference(registrationActivity);
                        LoginShared.setDeviceToken(registrationActivity, deviceToken);
                        Intent loginIntent = new Intent(registrationActivity, LoginActivity.class);
                        registrationActivity.startActivity(loginIntent);
                        registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        registrationActivity.finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(registrationActivity, jsObject.getString("message"));
                    }
                } catch (Exception e) {
                    MethodUtils.errorMsg(registrationActivity, registrationActivity.getString(R.string.error_occurred));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(registrationActivity, registrationActivity.getString(R.string.error_occurred));
            }
        });
    }

    private void callRegistrationApiWithImage() {
        loader.show_with_label("Loading");
        RequestBody gender = null;
        RequestBody units = null;
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), registrationActivity.mCompressedFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("userImage",
                registrationActivity.mCompressedFile.getName(), reqFile);
        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_full.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_email.getText().toString().trim());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_password.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_phone.getText().toString().trim());
        RequestBody scale = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_scale.getText().toString().trim());
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_management.getText().toString().trim());
        if (registrationActivity.et_units.getText().toString().trim().equalsIgnoreCase("KG/CM")) {
            units = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            units = RequestBody.create(MediaType.parse("text/plain"), "0");
        }
        if (registrationActivity.et_gender.getText().toString().trim().equals("Male")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else if (registrationActivity.et_gender.getText().toString().trim().equals("Female")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "0");
        } else {
            gender = RequestBody.create(MediaType.parse("text/plain"), "2");
        }
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_DOB.getText().toString().trim());
        RequestBody height = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_height.getText().toString().trim());
        RequestBody weight = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_weight.getText().toString().trim());
        RequestBody time = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_time_loss.getText().toString().trim());
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "2");
        RequestBody deviceToken = RequestBody.create(MediaType.parse("text/plain"), LoginShared.getDeviceToken(registrationActivity));


        Call<ResponseBody> registration_api = apiInterface.call_registrationImageApi(fullName, email, password, gender, phone, dob,
                height, weight, time, units, deviceType, scale,type, deviceToken, body);

        registration_api.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    RegistrationModel registrationModel;

                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.optInt("status") == 1) {

                        registrationModel = gson.fromJson(responseString, RegistrationModel.class);
                        LoginShared.setRegistrationDataModel(registrationActivity, registrationModel);
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        LoginShared.setUserPhoto(registrationActivity, LoginShared.getRegistrationDataModel(registrationActivity).getData().getUser().get(0).getUserPhoto());
                        LoginShared.setUserName(registrationActivity, LoginShared.getRegistrationDataModel(registrationActivity).getData().getUser().get(0).getUserName());
                        MethodUtils.errorMsg(registrationActivity, jsObject.getString("message"));

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent = new Intent(registrationActivity, OtpActivity.class);
                                registrationActivity.startActivity(loginIntent);
                                registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                registrationActivity.finish();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);
                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(registrationActivity);
                        LoginShared.destroySessionTypePreference(registrationActivity);
                        LoginShared.setDeviceToken(registrationActivity, deviceToken);
                        Intent loginIntent = new Intent(registrationActivity, LoginActivity.class);
                        registrationActivity.startActivity(loginIntent);
                        registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        registrationActivity.finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        MethodUtils.errorMsg(registrationActivity, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(registrationActivity, registrationActivity.getString(R.string.error_occurred));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(registrationActivity, registrationActivity.getString(R.string.error_occurred));
            }
        });

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

    private void showAndDismissManagementPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                managementPopup.showAsDropDown(registrationActivity.et_management);
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
