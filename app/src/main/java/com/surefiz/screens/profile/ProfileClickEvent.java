package com.surefiz.screens.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.profile.model.ViewProfileModel;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileClickEvent implements View.OnClickListener {
    ProfileActivity activity;
    private List<String> genderList = new ArrayList<>();
    private UniversalPopup genderPopup;
    private int month, year, day;
    private LoadingData loader;

    public ProfileClickEvent(ProfileActivity activity) {
        this.activity = activity;
        loader = new LoadingData(activity);
        setClickEvent();

        getProfileDataAndSet();

        addGenderListAndCall();
    }

    private void getProfileDataAndSet() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<ResponseBody> call_view_profile = apiInterface.call_viewprofileApi("application/json",
                LoginShared.getRegistrationDataModel(activity).getData().getToken(),
                LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId());

        call_view_profile.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    ViewProfileModel viewProfileModel;

                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        viewProfileModel = gson.fromJson(responseString, ViewProfileModel.class);
                        LoginShared.setViewProfileDataModel(activity, viewProfileModel);
                        setData();
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

    private void setData() {
        activity.et_DOB.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserDob());
        activity.et_gender.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserGender());
        activity.et_phone.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserPhoneNumber());
        activity.et_full.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserName());
    }

    private void setClickEvent() {
        activity.et_gender.setOnClickListener(this);
        activity.et_DOB.setOnClickListener(this);
    }

    private void addGenderListAndCall() {
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Others");

        genderPopup = new UniversalPopup(activity, genderList, activity.et_gender);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_gender:
                hideSoftKeyBoard();
                if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissGenderPopup();
                        }
                    }, 100);
                }
                break;
            case R.id.et_DOB:
                ExpiryDialog();
                break;
        }
    }

    private void hideSoftKeyBoard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
                activity.et_DOB.setText(dayInString + "--" + monthInString + "--" + year);

            }
        });
    }

    private void showAndDismissGenderPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                genderPopup.showAsDropDown(activity.et_gender);
            }
        }, 100);
    }
}
