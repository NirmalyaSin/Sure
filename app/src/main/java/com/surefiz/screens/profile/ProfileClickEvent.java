package com.surefiz.screens.profile;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
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
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.profile.model.ViewProfileModel;
import com.surefiz.screens.registration.model.RegistrationModel;
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
    private List<String> genderList = new ArrayList<>();
    private UniversalPopup genderPopup, prefferedPopup;
    private int month, year, day;
    private LoadingData loader;
    private ImageLoader imageLoader;
    private List<String> prefferedList = new ArrayList<>();
    private String filePath = "";

    public ProfileClickEvent(ProfileActivity activity) {
        this.activity = activity;
        loader = new LoadingData(activity);
        initializeImageLoader();
        setClickEvent();

        if (!ConnectionDetector.isConnectingToInternet(activity)) {
            MethodUtils.errorMsg(activity, activity.getString(R.string.no_internet));
        } else {
            getProfileDataAndSet();
        }

        addGenderListAndCall();
        addPrefferedListAndCall();
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
        activity.et_DOB.setText(MethodUtils.profileDOB(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserDob()));
        activity.et_gender.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserGender());
        activity.et_phone.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserPhoneNumber());
        activity.et_full.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserName());
        activity.profile_image.setEnabled(false);
        showImage();
    }

    private void showImage() {
        String url = LoginShared.getViewProfileDataModel(activity).getData().getUser().
                get(0).getUserImage();
        url = url.replace(" ", "20%");
        imageLoader.displayImage(url, activity.profile_image);
    }

    private void setClickEvent() {
        activity.et_gender.setOnClickListener(this);
        activity.et_DOB.setOnClickListener(this);
        activity.iv_edit.setOnClickListener(this);
        activity.profile_image.setOnClickListener(this);
        activity.iv_plus_add_image.setOnClickListener(this);
        activity.et_units.setOnClickListener(this);
        activity.btn_register.setOnClickListener(this);
    }

    private void addGenderListAndCall() {
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Others");

        genderPopup = new UniversalPopup(activity, genderList, activity.et_gender);
    }

    private void addPrefferedListAndCall() {
        prefferedList.add("KG/CM");
        prefferedList.add("LB/INCH");

        prefferedPopup = new UniversalPopup(activity, prefferedList, activity.et_units);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_gender:
                hideSoftKeyBoard();
                if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
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
                } else if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissPrefferedPopup();
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
                activity.et_gender.setEnabled(true);
                activity.et_DOB.setEnabled(true);
                activity.et_units.setEnabled(true);
                activity.profile_image.setEnabled(true);
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
            case R.id.btn_register:
                if (activity.et_full.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter your name");
                } else if (activity.et_phone.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter your phone number");
                } else if (activity.et_units.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please select your Preffered Units");
                } else if (activity.et_gender.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please select any gender type");
                } else if (activity.et_DOB.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please select your DOB");
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
        }
    }

    private void sendProfileImageUpdateApi() {
        loader.show_with_label("Loading");
        RequestBody gender = null;
        RequestBody preffered = null;
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), activity.et_full.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), activity.et_phone.getText().toString().trim());
        if (activity.et_units.getText().toString().trim().equals("KG/CM")) {
            preffered = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            preffered = RequestBody.create(MediaType.parse("text/plain"), "2");
        }
        if (activity.et_gender.getText().toString().trim().equals("Male")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "0");
        } else if (activity.et_gender.getText().toString().trim().equals("Female")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            gender = RequestBody.create(MediaType.parse("text/plain"), "2");
        }
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "Android");
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId());
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), activity.et_DOB.getText().toString().trim());
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), activity.mCompressedFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("userImage",
                activity.mCompressedFile.getName(), reqFile);

        Call<ResponseBody> editProfile = apiInterface.call_editprofileImageApi(LoginShared.getRegistrationDataModel(activity).getData().getToken(),
                userId, fullName, gender, phone, dob, preffered, deviceType, body);

        editProfile.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                activity.iv_edit.setVisibility(View.VISIBLE);
                try {
                    String responseString = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jObject = jsonObject.getJSONObject("data");

                        MethodUtils.errorMsg(activity, jObject.getString("message"));
                        activity.iv_edit.setVisibility(View.VISIBLE);
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
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), activity.et_full.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), activity.et_phone.getText().toString().trim());
        if (activity.et_units.getText().toString().trim().equals("KG/CM")) {
            preffered = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            preffered = RequestBody.create(MediaType.parse("text/plain"), "2");
        }
        if (activity.et_gender.getText().toString().trim().equals("Male")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "0");
        } else if (activity.et_gender.getText().toString().trim().equals("Female")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            gender = RequestBody.create(MediaType.parse("text/plain"), "2");
        }
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "Android");
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId());
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), activity.et_DOB.getText().toString().trim());

        Call<ResponseBody> editProfile = apiInterface.call_editprofileApi(LoginShared.getRegistrationDataModel(activity).getData().getToken(),
                userId, fullName, gender, phone, dob, preffered, deviceType);

        editProfile.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                activity.iv_edit.setVisibility(View.VISIBLE);
                try {
                    String responseString = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jObject = jsonObject.getJSONObject("data");

                        MethodUtils.errorMsg(activity, jObject.getString("message"));
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

    private void showAndDismissPrefferedPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                prefferedPopup.showAsDropDown(activity.et_units);
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
