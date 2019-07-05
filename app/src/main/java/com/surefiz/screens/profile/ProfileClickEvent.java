package com.surefiz.screens.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.OpenCameraOrGalleryDialog;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.dialog.weightpopup.WeigtUniversalPopup;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.interfaces.OnWeightCallback;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.profile.model.ViewProfileModel;
import com.surefiz.screens.weightManagement.WeightManagementActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileClickEvent implements View.OnClickListener {
    ProfileActivity activity;
    String units = "", height = "";
    String[] splited;
    private List<String> genderList = new ArrayList<>();
    private UniversalPopup genderPopup, prefferedPopup;
    private int month, year, day;
    private LoadingData loader;
    private ImageLoader imageLoader;
    private List<String> prefferedList = new ArrayList<>();
    private String filePath = "";
    private List<String> heightList = new ArrayList<>();
    private UniversalPopup heightPopup;
    private String weight_value = "", time_value = "", units_value = "";
    private WeigtUniversalPopup weigtUniversalPopupPreferred;


    public ProfileClickEvent(ProfileActivity activity) {
        this.activity = activity;
        loader = new LoadingData(activity);
        initializeImageLoader();
        setClickEvent();
        addPreferredListAndCall();
        //addHeightListAndCall("INCH");

        if (!ConnectionDetector.isConnectingToInternet(activity)) {
            MethodUtils.errorMsg(activity, activity.getString(R.string.no_internet));
        } else {
            getProfileDataAndSet();
        }

        addGenderListAndCall();

        activity.et_units.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                heightList.clear();
                if (activity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }
            }
        });
    }

    private void addHeightListAndCall(String change) {
        heightList.clear();
        if (change.equals("INCH")) {
            for (int i = 35; i <= 110; i++) {
                heightList.add(i + " " + change);
            }
        } else {
            for (int i = 88; i <= 280; i++) {
                heightList.add(i + " " + change);
            }
        }
        heightPopup = new UniversalPopup(activity, heightList, activity.et_height);
    }

    private void initializeImageLoader() {
        DisplayImageOptions opts = new DisplayImageOptions.Builder().cacheInMemory(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).defaultDisplayImageOptions(opts)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private void getProfileDataAndSet() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<ResponseBody> call_view_profile = apiInterface.call_viewprofileApi(LoginShared.getRegistrationDataModel(activity).getData().getToken(),
                LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId());


        System.out.println("@@@ProfileInfo :" + call_view_profile.request().body());


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

                    Log.d("@@ProfileInfo : ", jsonObject.toString());

                    if (jsonObject.optInt("status") == 1) {
                        viewProfileModel = gson.fromJson(responseString, ViewProfileModel.class);
                        LoginShared.setViewProfileDataModel(activity, viewProfileModel);
                        setData();
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

    @SuppressLint("SetTextI18n")
    private void setData() {

        if (!checkIsZeroValue(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserDob())) {
            activity.et_DOB.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserDob());
        } else {
            activity.et_DOB.setText("");
        }


        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserGender().equals("1")) {
            activity.et_gender.setText("Male");
        } else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserGender().equals("0")) {
            activity.et_gender.setText("Female");
        } else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserGender().equals("2")) {
            activity.et_gender.setText("Non-binary");
        } else {
            activity.et_gender.setText("Prefer not to say");
        }


        activity.et_phone.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserPhoneNumber());
        activity.et_full.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserName());
        activity.et_middle.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getMiddleName());
        activity.et_last.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getLastName());
        LoginShared.setUserName(activity, LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserName() + " " +
                LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getMiddleName() + " " +
                LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getLastName());
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getPreferredUnits().equals("1")) {
            activity.et_units.setText("KG/CM");
        } else {
            activity.et_units.setText("LB/INCH");
        }
        String unit = "";
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getPreferredUnits().equals("1")) {
            unit = "CM";
        } else {
            unit = "INCH";
        }
        activity.et_email.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserEmail());

        if (!checkIsZeroValue(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getHeight())) {
            activity.et_height.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getHeight() + " " + unit);
        } else {
            activity.et_height.setText("");
        }


        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            activity.rl_visibility.setVisibility(View.GONE);
            activity.switch_visibility.setChecked(true);
        } else {
            if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getMainuservisibility().equals("1")) {
                activity.switch_visibility.setChecked(true);
            } else {
                activity.switch_visibility.setChecked(false);
            }
            activity.rl_visibility.setVisibility(View.VISIBLE);
        }

        activity.profile_image.setEnabled(false);
        showImage();
    }

    private boolean checkIsZeroValue(String value) {
        return value.equalsIgnoreCase("0") || value.equalsIgnoreCase("0.0") || value.equalsIgnoreCase("0.00");

    }

    private void showImage() {
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().
                get(0).getUserImage().equals("") ||
                LoginShared.getViewProfileDataModel(activity).getData().getUser().
                        get(0).getUserImage().equalsIgnoreCase("null") ||
                LoginShared.getViewProfileDataModel(activity).getData().getUser().
                        get(0).getUserImage() == null) {
            activity.profile_image.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.prof_img_placeholder));
        } else {
            String url = LoginShared.getViewProfileDataModel(activity).getData().getUser().
                    get(0).getUserImage();
            LoginShared.setUserPhoto(activity, LoginShared.getViewProfileDataModel(activity).getData().getUser().
                    get(0).getUserImage());
            url = url.replace(" ", "20%");
            imageLoader.displayImage(url, activity.profile_image);
        }
    }


    private void setClickEvent() {
        activity.et_gender.setOnClickListener(this);
        //activity.et_DOB.setOnClickListener(this);
        activity.iv_edit.setOnClickListener(this);
        activity.profile_image.setOnClickListener(this);
        activity.iv_plus_add_image.setOnClickListener(this);
        activity.et_units.setOnClickListener(this);
        activity.et_height.setOnClickListener(this);
        activity.btn_register.setOnClickListener(this);
        activity.switch_visibility.setOnClickListener(this);
        activity.findViewById(R.id.iv_weight_managment).setOnClickListener(this);
    }

    private void addGenderListAndCall() {
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Non-binary");
        genderList.add("Prefer not to say");

        genderPopup = new UniversalPopup(activity, genderList, activity.et_gender);
    }

    private void addPreferredListAndCall() {

        prefferedList.add("LB/INCH");
        prefferedList.add("KG/CM");
        weigtUniversalPopupPreferred = new WeigtUniversalPopup(activity, prefferedList, activity.et_units, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                height = activity.et_height.getText().toString().trim();
                splited = height.split(" ");
                if (value.equals("KG/CM")) {
                    units = "CM";
                } else {
                    units = "INCH";
                }
                if (units.equals(splited[1])) {
                } else {
                    if (value.equals("KG/CM")) {
                        activity.et_height.setText(Math.round(Double.parseDouble(splited[0]) * 2.54) + " CM");
                        units = "CM";
                    }
                    if (value.equals("LB/INCH")) {
                        units = "INCH";
                        activity.et_height.setText((Math.round(Double.parseDouble(splited[0]) * 0.393701)) + " INCH");
                    }
                }
            }
        });
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_gender:
                hideSoftKeyBoard();
                if (activity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }
                if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (weigtUniversalPopupPreferred != null && weigtUniversalPopupPreferred.isShowing()) {
                    weigtUniversalPopupPreferred.dismiss();
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
            case R.id.et_units:
                hideSoftKeyBoard();
                if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (weigtUniversalPopupPreferred != null && weigtUniversalPopupPreferred.isShowing()) {
                    weigtUniversalPopupPreferred.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissPreferredPopup();
                        }
                    }, 100);
                }
                break;
            case R.id.iv_edit:
                activity.iv_plus_add_image.setVisibility(View.VISIBLE);
                activity.btn_register.setVisibility(View.VISIBLE);
                activity.iv_edit.setVisibility(View.GONE);
                activity.et_full.requestFocus();
                activity.et_phone.setEnabled(true);
                activity.et_full.setEnabled(true);
                activity.et_middle.setEnabled(true);
                activity.et_last.setEnabled(true);
                activity.et_gender.setEnabled(true);
                activity.et_DOB.setEnabled(true);
                activity.et_units.setEnabled(true);
                activity.et_email.setEnabled(true);
                activity.et_height.setEnabled(true);
                activity.profile_image.setEnabled(true);
                activity.switch_visibility.setEnabled(true);
                activity.et_full.setSelection(activity.et_full.getText().length());
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(activity.et_full, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.profile_image:
                new OpenCameraOrGalleryDialog(activity, new OnImageSet() {
                    @Override
                    public void onSuccess(String path) {
                        filePath = path;
                    }
                }, "1").show();
                break;
            case R.id.iv_plus_add_image:
                new OpenCameraOrGalleryDialog(activity, new OnImageSet() {
                    @Override
                    public void onSuccess(String path) {
                        filePath = path;
                    }
                }, "1").show();
                break;
            case R.id.et_height:
                hideSoftKeyBoard();
                if (weigtUniversalPopupPreferred != null && weigtUniversalPopupPreferred.isShowing()) {
                    weigtUniversalPopupPreferred.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissHeightPopup();
                        }
                    }, 100);
                }
                break;
            case R.id.btn_register:
                if (activity.et_full.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter your name");
                } else if (activity.et_phone.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter your phone number");
                } else if (activity.et_units.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please select your Preferred Units");
                } else if (activity.et_gender.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please select any gender type");
                } else if (activity.et_DOB.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter your Age");
                } else if (!isNonZeroValue(activity.et_DOB.getText().toString().trim())) {
                    MethodUtils.errorMsg(activity, "Age should be between 7 and 99");
                } else if (!ConnectionDetector.isConnectingToInternet(activity)) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.no_internet));
                } else {
                    if (activity.mCompressedFile != null) {
                        sendProfileImageUpdateApi();
                    } else {
                        sendProfileUpdateApi();
                    }
                }
                break;
            case R.id.iv_weight_managment:
                Intent weightIntent = new Intent(activity, WeightManagementActivity.class);
                weightIntent.putExtra("isInitiatedFromProfile", true);
                activity.startActivity(weightIntent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    private boolean isNonZeroValue(String value) {
        int nonZeroValue = 0;
        try {
            nonZeroValue = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        System.out.println("nonZeroValue: " + nonZeroValue);
        return (nonZeroValue >= 9 && nonZeroValue <= 99);
    }


    private void showAndDismissHeightPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                heightPopup.showAsDropDown(activity.et_height);
            }
        }, 100);
    }

    private void sendProfileImageUpdateApi() {
        loader.show_with_label("Loading");
        RequestBody gender = null;
        RequestBody preffered = null;
        RequestBody mainuservisibility = null;
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), activity.et_full.getText().toString().trim());
        RequestBody middleName = RequestBody.create(MediaType.parse("text/plain"), activity.et_middle.getText().toString().trim());
        RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), activity.et_last.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), activity.et_phone.getText().toString().trim());
        if (activity.et_units.getText().toString().trim().equals("KG/CM")) {
            preffered = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            preffered = RequestBody.create(MediaType.parse("text/plain"), "0");
        }
        if (activity.et_gender.getText().toString().trim().equals("Male")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else if (activity.et_gender.getText().toString().trim().equals("Female")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "0");
        } else if (activity.et_gender.getText().toString().trim().equalsIgnoreCase("Non-binary")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "2");
        } else {
            gender = RequestBody.create(MediaType.parse("text/plain"), "3");
        }
        RequestBody user_email = RequestBody.create(MediaType.parse("text/plain"), activity.et_email.getText().toString().trim());
        String str = activity.et_height.getText().toString().trim();
        String[] splited = str.split(" ");
        RequestBody Height = RequestBody.create(MediaType.parse("text/plain"), splited[0]);
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "Android");
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId());
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), activity.et_DOB.getText().toString().trim());

        if (activity.switch_visibility.isChecked()) {
            mainuservisibility = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            mainuservisibility = RequestBody.create(MediaType.parse("text/plain"), "0");
        }

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), activity.mCompressedFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("userImage",
                activity.mCompressedFile.getName(), reqFile);


        /*MultipartBody.Part body1 = MultipartBody.Part.createFormData("middleName",
                activity.mCompressedFile.getName(), reqFile);
        MultipartBody.Part body2 = MultipartBody.Part.createFormData("middleName",
                activity.mCompressedFile.getName(), reqFile);*/


        Call<ResponseBody> editProfile = apiInterface.call_editprofileImageApi(LoginShared.getRegistrationDataModel(activity).getData().getToken(),
                userId, fullName, middleName, lastName, gender, phone, dob, deviceType, user_email, Height, preffered, mainuservisibility, body);

        editProfile.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                activity.iv_edit.setVisibility(View.VISIBLE);
                try {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    ViewProfileModel viewProfileModel;
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        viewProfileModel = gson.fromJson(responseString, ViewProfileModel.class);
                        LoginShared.setViewProfileDataModel(activity, viewProfileModel);
                        JSONObject jObject = jsonObject.getJSONObject("data");
                        LoginShared.setUserPhoto(activity, LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserImage());
                        LoginShared.setUserName(activity, LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserName() + " " +
                                LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getMiddleName() + " " +
                                LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getLastName());
                        MethodUtils.errorMsg(activity, jObject.getString("message"));
                        activity.iv_edit.setVisibility(View.VISIBLE);
                        activity.showViewMode();
                        setData();
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

    private void sendProfileUpdateApi() {
        loader.show_with_label("Loading");
        RequestBody gender = null;
        RequestBody preffered = null;
        RequestBody mainuservisibility = null;
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), activity.et_full.getText().toString().trim());
        RequestBody middleName = RequestBody.create(MediaType.parse("text/plain"), activity.et_middle.getText().toString().trim());
        RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), activity.et_last.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"),
                activity.et_phone.getText().toString().trim());
        if (activity.et_units.getText().toString().trim().equals("KG/CM")) {
            preffered = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            preffered = RequestBody.create(MediaType.parse("text/plain"), "0");
        }
        if (activity.et_gender.getText().toString().trim().equals("Male")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else if (activity.et_gender.getText().toString().trim().equals("Female")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "0");
        } else if (activity.et_gender.getText().toString().trim().equalsIgnoreCase("Non-binary")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "2");
        } else {
            gender = RequestBody.create(MediaType.parse("text/plain"), "3");
        }


        if (activity.switch_visibility.isChecked()) {
            mainuservisibility = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            mainuservisibility = RequestBody.create(MediaType.parse("text/plain"), "0");
        }


        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "Android");
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId());
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), activity.et_DOB.getText().toString().trim());
        RequestBody user_email = RequestBody.create(MediaType.parse("text/plain"), activity.et_email.getText().toString().trim());
        String str = activity.et_height.getText().toString().trim();
        String[] splited = str.split(" ");
        RequestBody Height = RequestBody.create(MediaType.parse("text/plain"), splited[0]);
        Call<ResponseBody> editProfile = apiInterface.call_editprofileApi(LoginShared.getRegistrationDataModel(activity).getData().getToken(),
                userId, fullName, middleName, lastName, gender, phone, dob, deviceType, user_email, Height, preffered, mainuservisibility);

        editProfile.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                activity.iv_edit.setVisibility(View.VISIBLE);
                try {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    ViewProfileModel viewProfileModel;
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        viewProfileModel = gson.fromJson(responseString, ViewProfileModel.class);
                        LoginShared.setViewProfileDataModel(activity, viewProfileModel);
                        JSONObject jObject = jsonObject.getJSONObject("data");
                        LoginShared.setUserPhoto(activity, LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserImage());
                        LoginShared.setUserName(activity, LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserName() + " " +
                                LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getMiddleName() + " " +
                                LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getLastName());
                        MethodUtils.errorMsg(activity, jObject.getString("message"));
                        activity.showViewMode();
                        setData();
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

    private void hideSoftKeyBoard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showAndDismissPreferredPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                weigtUniversalPopupPreferred.showAsDropDown(activity.et_units);
            }
        }, 100);
    }

    private void ExpiryDialog() {

        Calendar mCalendar;
        mCalendar = Calendar.getInstance();
        System.out.println("Inside Dialog Box");
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_date_picker);
        dialog.show();
        final DatePicker datePicker = dialog.findViewById(R.id.date_picker);
        Button date_time_set = dialog.findViewById(R.id.date_time_set);
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
                activity.et_DOB.setText(dayInString + "-" + monthInString + "-" + year);
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
