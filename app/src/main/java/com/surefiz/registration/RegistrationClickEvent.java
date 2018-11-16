package com.surefiz.registration;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.OpenCameraOrGalleryDialog;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.registration.model.RegistrationModel;
import com.surefiz.sharedhandler.LoginShared;
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
    private String filePath = "";
    private LoadingData loader;
    private UniversalPopup genderPopup, prefferedPopup, heightPopup, weightPopup, timePopup;

    public RegistrationClickEvent(RegistrationActivity registrationActivity) {
        this.registrationActivity = registrationActivity;
        loader = new LoadingData(registrationActivity);
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
        String myFormat = "dd-MM-yyyy"; //In which you need put here
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
        registrationActivity.profile_image.setOnClickListener(this);
        registrationActivity.iv_plus_add_image.setOnClickListener(this);
        registrationActivity.btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_upload:
                break;
            case R.id.btn_register:
                if (registrationActivity.mCompressedFile != null) {
                    callRegistrationApiWithImage();
                } else {
                    callRegistrationApi();
                }
                break;
            case R.id.profile_image:
                new OpenCameraOrGalleryDialog(registrationActivity, new OnImageSet() {
                    @Override
                    public void onSuccess(String path) {
                        filePath = path;
                    }
                }).show();
                break;
            case R.id.iv_plus_add_image:
                new OpenCameraOrGalleryDialog(registrationActivity, new OnImageSet() {
                    @Override
                    public void onSuccess(String path) {
                        filePath = path;
                    }
                }).show();
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

    private void callRegistrationApi() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_full.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_email.getText().toString().trim());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_password.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_phone.getText().toString().trim());
        RequestBody scale = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_scale.getText().toString().trim());
        RequestBody preffered = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_units.getText().toString().trim());
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_DOB.getText().toString().trim());
        RequestBody height = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_height.getText().toString().trim());
        RequestBody weight = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_weight.getText().toString().trim());
        RequestBody time = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_time_loss.getText().toString().trim());
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "Android");

        Call<ResponseBody> registration_api = apiInterface.call_registrationApi(fullName, email, password, gender, phone, dob,
                height, weight, time, preffered, scale, deviceType);

        registration_api.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {
                    String responseString = response.errorBody().string();
                    Gson gson = new Gson();
                    RegistrationModel registrationModel;

                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.optInt("status") == 1) {
                        registrationModel = gson.fromJson(responseString, RegistrationModel.class);
                        LoginShared.setRegistrationDataModel(registrationActivity, registrationModel);
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
                Toast.makeText(registrationActivity, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callRegistrationApiWithImage() {
        loader.show_with_label("Loading");
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
        RequestBody preffered = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_units.getText().toString().trim());
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_DOB.getText().toString().trim());
        RequestBody height = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_height.getText().toString().trim());
        RequestBody weight = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_weight.getText().toString().trim());
        RequestBody time = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_time_loss.getText().toString().trim());
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "Android");

        Call<ResponseBody> registration_api = apiInterface.call_registrationImageApi(fullName, email, password, gender, phone, dob,
                height, weight, time, preffered, deviceType, scale, body);

        registration_api.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {
                    String responseString = response.errorBody().string();
                    Gson gson = new Gson();
                    RegistrationModel registrationModel;

                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.optInt("status") == 1) {

                        Toast.makeText(registrationActivity, "Success", Toast.LENGTH_SHORT).show();
                        registrationModel = gson.fromJson(responseString, RegistrationModel.class);
                        LoginShared.setRegistrationDataModel(registrationActivity, registrationModel);
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
