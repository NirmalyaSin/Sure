package com.surefiz.screens.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.MyOptionsPickerView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.bodycodition.BodyActivity;
import com.surefiz.screens.bodycodition.model.BodyItem;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.profile.model.country.CountryList;
import com.surefiz.screens.profile.model.country.Datum;
import com.surefiz.screens.profile.model.profile.ViewProfileModel;
import com.surefiz.screens.profile.model.provider.ProviderResponse;
import com.surefiz.screens.profile.model.provider.ProviderlistItem;
import com.surefiz.screens.profile.model.state.DataItem;
import com.surefiz.screens.profile.model.state.StateResponse;
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

public class ProfileClickEvent implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    static final int RC_SIGN_IN_GOOGLE = 100;
    static final int BODY_CONDITION = 101;
    ProfileActivity activity;
    String units = "", height = "";
    String[] splited;
    private int month, year, day;
    private LoadingData loader;
    private ImageLoader imageLoader;
    private ArrayList<String> prefferedList = new ArrayList<>();
    private ArrayList<String> countryList = new ArrayList<>();
    private ArrayList<Integer> countryIDList = new ArrayList<>();
    private ArrayList<String> stateList = new ArrayList<>();
    private ArrayList<String> stateIDList = new ArrayList<>();
    private ArrayList<String> genderList = new ArrayList<>();
    private ArrayList<String> lifeStyleList = new ArrayList<>();
    private ArrayList<String> providerIdList = new ArrayList<>();
    private ArrayList<String> providerNameList = new ArrayList<>();
    protected ArrayList<String> array1 = new ArrayList<>();
    protected ArrayList<String> array2 = new ArrayList<>();

    private String filePath = "";
    private String weight_value = "", time_value = "", units_value = "";
    private GoogleApiClient googleApiClient;
    private MyOptionsPickerView weigtUniversalPopupPreferred,genderPopup,doublePicker,lifeStylePopup;
    private MyOptionsPickerView countryListPopup,stateListPopup,providerPopup;
    private int selectedLifeStyle = 0;
    private String selectedCountryId = "";
    private String selectedProviderId = "";
    private boolean isState=false;

    public ProfileClickEvent(ProfileActivity activity) {
        this.activity = activity;
        loader = new LoadingData(activity);
        initializeImageLoader();
        setClickEvent();
        startFireBase();

        addPreferredListAndCall();
        addGenderListAndCall();
        addLifeStyleListAndCall();

        if (!ConnectionDetector.isConnectingToInternet(activity)) {
            MethodUtils.errorMsg(activity, activity.getString(R.string.no_internet));
        } else {
            getProfileDataAndSet();
        }





        activity.et_units.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (activity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }
            }
        });

        callCountryListApi();
        callProviderListApi();
    }

    private void startFireBase() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void addHeightListAndCall(String change) {

        setupPickerLogic(change);

        doublePicker=new MyOptionsPickerView(activity);
        doublePicker.setPicker(array1,array2,false);
        doublePicker.setCyclic(false,false,false);
        doublePicker.setSelectOptions(0,0);


        doublePicker.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {

                String[] separated = array1.get(option1).split(" ");
                String[] separated2 = array2.get(option2).split(" ");

                setValue(change,Integer.parseInt(separated[0]),Integer.parseInt(separated2[0]));
            }
        });

    }

    private void setValue(String change,int v1, int v2){
        if(change.equals("INCH")){

            int result=v1*12+v2;
            activity.et_height.setText(result+" INCH");

        }else{

            int result=v1*100+v2;
            activity.et_height.setText(result+" CM");
        }
    }

    private void setupPickerLogic(String change){

        array1.clear();
        array2.clear();

        if(change.equals("INCH")){

            for (int i = 3; i < 8; i++) {
                array1.add(i+" FT");
            }

            for (int j = 0; j < 12; j++) {
                array2.add(j+" INCH");
            }

        }else{

            for (int i = 1; i < 4; i++) {
                array1.add(i+" Meter");
            }

            for (int j = 0; j < 100; j++) {
                array2.add(j+" CM");

            }
        }
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

                        selectedCountryId=LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getCountryid();

                        if(isState==false)
                            callStateListApi();

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

        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getLifestyle().equals("0")) {
            activity.et_lifestyle.setText("");
        } else {
            String lifeStyleValue = lifeStyleList.get(Integer.valueOf(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getLifestyle()) - 1);
            activity.et_lifestyle.setText(lifeStyleValue);
            selectedLifeStyle=lifeStyleList.indexOf(lifeStyleValue)+1;
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
//        activity.et_body.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getBodycondition());
        LoginShared.setUserName(activity, LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getUserName() + " " +
                LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getMiddleName() + " " +
                LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getLastName());
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getPreferredUnits().equals("1")) {
            activity.et_units.setText("KG/CM");
        } else {
            activity.et_units.setText("LBS/INCH");
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

        activity.et_country_name.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getCountry());
        activity.et_add_line1.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getAddressline1());
        activity.et_add_line2.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getAddressline2());
        activity.et_city.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getCity());
        activity.et_state.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getState());
        activity.et_zipcode.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getZip());
        activity.et_provider.setText(LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getProviderName());
        selectedProviderId=LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getProviderid();


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

        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getIsPasswordAvailable() == 1) {
            activity.ll_add_new_password.setVisibility(View.GONE);
        } else {
            activity.ll_add_new_password.setVisibility(View.VISIBLE);
        }

        activity.profile_image.setEnabled(false);

        if (!LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            activity.tv_zip_code.setVisibility(View.GONE);
            activity.rl_zip_code.setVisibility(View.GONE);
            activity.tv_state.setVisibility(View.GONE);
            activity.rl_state.setVisibility(View.GONE);
            activity.tv_city.setVisibility(View.GONE);
            activity.rl_city.setVisibility(View.GONE);
            activity.tv_address_line2.setVisibility(View.GONE);
            activity.rl_addressLine2.setVisibility(View.GONE);
            activity.tv_address_line.setVisibility(View.GONE);
            activity.rl_addressLine1.setVisibility(View.GONE);
            activity.tv_country_name.setVisibility(View.GONE);
            activity.rl_countryName.setVisibility(View.GONE);
        } else {
            activity.tv_zip_code.setVisibility(View.VISIBLE);
            activity.rl_zip_code.setVisibility(View.VISIBLE);
            activity.tv_state.setVisibility(View.VISIBLE);
            activity.rl_state.setVisibility(View.VISIBLE);
            activity.tv_city.setVisibility(View.VISIBLE);
            activity.rl_city.setVisibility(View.VISIBLE);
            activity.tv_address_line2.setVisibility(View.VISIBLE);
            activity.rl_addressLine2.setVisibility(View.VISIBLE);
            activity.tv_address_line.setVisibility(View.VISIBLE);
            activity.rl_addressLine1.setVisibility(View.VISIBLE);
            activity.tv_country_name.setVisibility(View.VISIBLE);
            activity.rl_countryName.setVisibility(View.VISIBLE);
            activity.rl_body.setVisibility(View.VISIBLE);
        }

        setSocialAddButtonStatus();

        showImage();

        addBodyList();
    }

    private void setSocialAddButtonStatus() {
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getGoogleAccountLinked() == 0) {
            activity.btnGoogleAdd.setBackgroundColor(activity.getResources().getColor(R.color.social_add_button));
            activity.btnGoogleAdd.setText("ADD");
        } else {
            activity.btnGoogleAdd.setBackgroundColor(Color.RED);
            activity.btnGoogleAdd.setText("REMOVE");
        }


        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getFacebookAccountLinked() == 0) {
            activity.btnFacebookAdd.setBackgroundColor(activity.getResources().getColor(R.color.social_add_button));
            activity.btnFacebookAdd.setText("ADD");
        } else {
            activity.btnFacebookAdd.setBackgroundColor(Color.RED);
            activity.btnFacebookAdd.setText("REMOVE");
        }
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
        activity.btn_cancel.setOnClickListener(this);
        activity.switch_visibility.setOnClickListener(this);
        activity.btnGoogleAdd.setOnClickListener(this);
        activity.btnFacebookAdd.setOnClickListener(this);
        activity.et_country_name.setOnClickListener(this);
        activity.et_state.setOnClickListener(this);
        activity.et_lifestyle.setOnClickListener(this);
        activity.et_provider.setOnClickListener(this);
        activity.et_body.setOnClickListener(this);
        activity.findViewById(R.id.iv_weight_managment).setOnClickListener(this);

        activity.et_full.setOnClickListener(this);
        activity.et_middle.setOnClickListener(this);
        activity.et_last.setOnClickListener(this);
        activity.et_email.setOnClickListener(this);
        activity.et_phone.setOnClickListener(this);
        activity.et_DOB.setOnClickListener(this);
        activity.et_add_line1.setOnClickListener(this);
        activity.et_add_line2.setOnClickListener(this);
        activity.et_city.setOnClickListener(this);
        activity.et_zipcode.setOnClickListener(this);
    }

    //***AVIK
    private void addBodyList() {
        String temp="";
        temp=LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getBodycondition();
        Log.e("temp",":::"+temp);
        String [] stringList={"Diabetes","Depression","Heart Disease","High Blood Pressure","Osteoarthritis","High Cholesterol","None"};
        for (int i = 0; i <stringList.length ; i++) {
            BodyItem bodyItem=new BodyItem();
            bodyItem.setName(stringList[i]);

            if(temp.toLowerCase().contains(stringList[i].toLowerCase()))
                bodyItem.setSelection(true);
            else
                bodyItem.setSelection(false);

            activity.bodyList.add(bodyItem);
        }

    }
    private void addGenderListAndCall() {
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Non-binary");
        genderList.add("Prefer not to say");

        genderPopup=new MyOptionsPickerView(activity);
        genderPopup.setPicker(genderList);
        genderPopup.setCyclic(false);
        genderPopup.setSelectOptions(0);


        genderPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                activity.et_gender.setText(genderList.get(options1));
            }
        });
    }

    private void addLifeStyleListAndCall() {
        lifeStyleList.add("Sedentary");
        lifeStyleList.add("Lightly active");
        lifeStyleList.add("Moderately active");
        lifeStyleList.add("Very active");
        lifeStyleList.add("Extra active");

        lifeStylePopup=new MyOptionsPickerView(activity);
        lifeStylePopup.setPicker(lifeStyleList);
        lifeStylePopup.setCyclic(false);
        lifeStylePopup.setSelectOptions(0);

        lifeStylePopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                activity.et_lifestyle.setText(lifeStyleList.get(options1));
                selectedLifeStyle = options1 + 1;
            }
        });

    }

    private void addPreferredListAndCall() {

        prefferedList.add("LBS/INCH");
        prefferedList.add("KG/CM");

        weigtUniversalPopupPreferred=new MyOptionsPickerView(activity);
        weigtUniversalPopupPreferred.setPicker(prefferedList);
        weigtUniversalPopupPreferred.setCyclic(false);
        weigtUniversalPopupPreferred.setSelectOptions(0);

        weigtUniversalPopupPreferred.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                height = activity.et_height.getText().toString().trim();
                splited = height.split(" ");
                String value=prefferedList.get(options1);
                activity.et_units.setText(value);

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
                    if (value.equals("LBS/INCH")) {
                        units = "INCH";
                        activity.et_height.setText((Math.round(Double.parseDouble(splited[0]) * 0.393701)) + " INCH");
                    }
                }
            }
        });


    }

    private void invisibleCursor(){
        activity.et_full.setCursorVisible(false);
        activity.et_middle.setCursorVisible(false);
        activity.et_last.setCursorVisible(false);
        activity.et_email.setCursorVisible(false);
        activity.et_phone.setCursorVisible(false);
        activity.et_DOB.setCursorVisible(false);
        activity.et_add_line1.setCursorVisible(false);
        activity.et_add_line2.setCursorVisible(false);
        activity.et_city.setCursorVisible(false);
        activity.et_zipcode.setCursorVisible(false);
    }

    public void onClick(View v) {
        switch (v.getId()) {

         /*   case R.id.et_full:
                activity.et_full.setCursorVisible(true);
                break;
            case R.id.et_middle:
                activity.et_middle.setCursorVisible(true);
                break;
            case R.id.et_last:
                activity.et_last.setCursorVisible(true);
                break;
            case R.id.et_email:
                activity.et_email.setCursorVisible(true);
                break;
            case R.id.et_phone:
                activity.et_phone.setCursorVisible(true);
                break;
            case R.id.et_add_line1:
                activity.et_add_line1.setCursorVisible(true);
                break;
            case R.id.et_add_line2:
                activity.et_add_line2.setCursorVisible(true);
                break;
            case R.id.et_city:
                activity.et_city.setCursorVisible(true);
                break;
            case R.id.et_zipcode:
                activity.et_zipcode.setCursorVisible(true);
                break;*/

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
                activity.btn_cancel.setVisibility(View.VISIBLE);
                activity.iv_edit.setVisibility(View.GONE);
                activity.et_full.setEnabled(true);
                activity.et_phone.setEnabled(true);
                activity.et_middle.setEnabled(true);
                activity.et_last.setEnabled(true);
                activity.et_gender.setEnabled(true);
                activity.et_body.setEnabled(true);
                activity.et_DOB.setEnabled(true);
                activity.et_units.setEnabled(true);
                activity.et_email.setEnabled(true);
                activity.et_height.setEnabled(true);
                activity.et_country_name.setEnabled(true);
                activity.et_lifestyle.setEnabled(true);
                activity.et_provider.setEnabled(true);
                activity.et_state.setEnabled(true);
                activity.et_add_line1.setEnabled(true);
                activity.et_add_line2.setEnabled(true);
                activity.et_city.setEnabled(true);
                activity.et_zipcode.setEnabled(true);
                activity.profile_image.setEnabled(true);
                //activity.switch_visibility.setEnabled(true);
                activity.switch_lock.setVisibility(View.GONE);

                activity.et_new_password.setEnabled(true);
                activity.et_confirm_password.setEnabled(true);

                //activity.et_full.setSelection(activity.et_full.getText().length());
                activity.sv_main.setFocusable(true);

                break;
            case R.id.profile_image:
                activity.imagePicker();
                break;

            case R.id.iv_plus_add_image:
                activity.imagePicker();
                break;

            case R.id.et_height:
                hideSoftKeyBoard();
                if (weigtUniversalPopupPreferred != null && weigtUniversalPopupPreferred.isShowing()) {
                    weigtUniversalPopupPreferred.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (doublePicker != null && doublePicker.isShowing()) {
                    doublePicker.dismiss();
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
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_enter_your_First_Name));
                }
                else if (activity.et_last.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_enter_your_Last_Name));
                }
                else if (activity.et_email.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_enter_your_Email_ID));
                }
               /* else if (activity.et_body.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_select_your_Pre_Existing_Conditions));
                }*/
                else if (activity.et_phone.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_enter_your_Phone_Number));
                }
                else if (activity.et_units.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_select_your_Preferred_Units));
                }
                else if (activity.et_gender.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_select_your_Gender));
                }
                else if (activity.et_DOB.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_enter_your_Age));
                }
                else if (!isNonZeroValue(activity.et_DOB.getText().toString().trim())) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Your_age_should_be_greater_than_6));
                }
                else if (selectedLifeStyle == 0) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_select_your_Lifestyle));
                }
                else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getIsPasswordAvailable() == 0 &&
                        activity.et_new_password.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_enter_your_password));
                }
                else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getIsPasswordAvailable() == 0 &&
                        activity.et_confirm_password.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_confirm_your_password));
                }
                else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getIsPasswordAvailable() == 0 &&
                        !activity.et_confirm_password.getText().toString().equals(activity.et_new_password.getText().toString())) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.New_password_and_confirm_password_must_be_same));
                }
                else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1") && activity.et_country_name.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_select_your_Country));
                }
                else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1") && activity.et_add_line1.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Enter_Your_Address_Line_1));
                }
                else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1") && activity.et_city.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_enter_your_City));
                }
                else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1") && activity.et_state.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_select_your_State));
                }
                else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1") && activity.et_zipcode.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_enter_your_Zip_Code));
                }
                else if (activity.et_provider.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.Please_Select_Your_Provider));
                }
                else if (!ConnectionDetector.isConnectingToInternet(activity)) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.no_internet));
                }
                else {
                    if (activity.mCompressedFile != null) {
                        sendProfileImageUpdateApi();
                    } else {
                        sendProfileUpdateApi();
                    }
                }
                break;
            case R.id.btn_cancel:
                disableProfileEditMode();
                break;

            case R.id.iv_weight_managment:
                Intent weightIntent = new Intent(activity, WeightManagementActivity.class);
                weightIntent.putExtra("isInitiatedFromProfile", true);
                activity.startActivity(weightIntent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.btnGoogleAdd:

                if (!ConnectionDetector.isConnectingToInternet(activity)) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.no_internet));
                } else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getGoogleAccountLinked() == 0) {

                    activity.fbcallbackManager = null;

                    Auth.GoogleSignInApi.signOut(googleApiClient);
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    activity.startActivityForResult(intent, RC_SIGN_IN_GOOGLE);
                } else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getGoogleAccountLinked() == 1) {
                    callapiforRemoveSocial("google");
                }

                break;

            case R.id.btnFacebookAdd:
                if (!ConnectionDetector.isConnectingToInternet(activity)) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.no_internet));
                } else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getFacebookAccountLinked() == 0) {
                    activity.callFacebooklogin();
                } else if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getFacebookAccountLinked() == 1) {
                    callapiforRemoveSocial("fb");
                }
                break;
            case R.id.et_country_name:
                hideSoftKeyBoard();
                showAndDismissCountryPopup();
                break;
            case R.id.et_state:        //***AVIK
                hideSoftKeyBoard();
                showAndDismissStatePopup();
                break;
            case R.id.et_lifestyle:
                hideSoftKeyBoard();
                showAndDismissLifeStylePopup();
                break;
            case R.id.et_body:        //***AVIK
                hideSoftKeyBoard();
                showBodyPopup();
                break;
            case R.id.et_provider:        //***AVIK
                hideSoftKeyBoard();
                providerPopup.show();
                break;
        }
    }

    private void showAndDismissCountryPopup() {

        countryListPopup.show();

    }

    //***AVIK
    private void showAndDismissStatePopup() {

        stateListPopup.show();

    }

    private void disableProfileEditMode() {
        activity.iv_plus_add_image.setVisibility(View.GONE);
        activity.btn_register.setVisibility(View.GONE);
        activity.tvUserImageHint.setVisibility(View.GONE);
        activity.btn_cancel.setVisibility(View.GONE);
        activity.iv_edit.setVisibility(View.VISIBLE);
        //activity.et_full.requestFocus();
        activity.et_full.setEnabled(false);
        activity.et_phone.setEnabled(false);
        activity.et_middle.setEnabled(false);
        activity.et_last.setEnabled(false);
        activity.et_gender.setEnabled(false);
        activity.et_DOB.setEnabled(false);
        activity.et_units.setEnabled(false);
        activity.et_email.setEnabled(false);
        activity.et_height.setEnabled(false);
        activity.profile_image.setEnabled(false);
        activity.et_country_name.setEnabled(false);
        activity.et_lifestyle.setEnabled(false);
        activity.et_provider.setEnabled(false);
        activity.et_state.setEnabled(false);
        activity.et_body.setEnabled(false);
        activity.et_add_line1.setEnabled(false);
        activity.et_add_line2.setEnabled(false);
        activity.et_city.setEnabled(false);
        activity.et_zipcode.setEnabled(false);
        //activity.switch_visibility.setEnabled(false);
        activity.switch_lock.setVisibility(View.VISIBLE);


        activity.et_new_password.setEnabled(false);
        activity.et_confirm_password.setEnabled(false);

        disableAllPopups();

        getProfileDataAndSet();
    }

    private void disableAllPopups() {
        lifeStylePopup.dismiss();
        countryListPopup.dismiss();
        stateListPopup.dismiss();
        genderPopup.dismiss();
        doublePicker.dismiss();
        weigtUniversalPopupPreferred.dismiss();
        providerPopup.dismiss();
    }

    public void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String id = account.getId();
            String idToken = account.getIdToken();
            String name = account.getDisplayName();
            String email = account.getEmail();
            Uri photoUrl = account.getPhotoUrl();

            System.out.println("googleData: " + id + "\n" + email + "\n" + name + "\n" + photoUrl + "\n" + idToken);
            System.out.println("googleData: " + id + "," + idToken);

            callapiforAddSocail(id, "google");
            //callapiforSocaillogin(id, email, name, getString(R.string.google_login_type), photoUrl == null ? "" : photoUrl.toString(), "", "", idToken);
        } else {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void callapiforAddSocail(String socicalID, String medianame) {

        LoadingData loader = new LoadingData(activity);
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);

        String userID = LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId();

        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ResponseBody> socialoginapicall = apiInterface.call_socialAddApi(LoginShared.getRegistrationDataModel(activity).getData().getToken(), socicalID,
                medianame, userID);

        socialoginapicall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {
                    String responseString = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.d("@@AddSocial : ", jsonObject.toString());

                    JSONObject jsObject = jsonObject.getJSONObject("data");

                    if (jsonObject.optInt("status") == 1) {

                        MethodUtils.errorMsg(activity, jsObject.getString("message"));

                        ViewProfileModel viewProfileModel = LoginShared.getViewProfileDataModel(activity);

                        if (medianame.equalsIgnoreCase("google")) {
                            if(activity.getString(R.string.Try_Another_Google).equals(jsObject.getString("message"))) {
                                viewProfileModel.getData().getUser().get(0).setGoogleAccountLinked(0);
                            }else
                                viewProfileModel.getData().getUser().get(0).setGoogleAccountLinked(1);

                            LoginShared.setViewProfileDataModel(activity, viewProfileModel);

                        } else if (medianame.equalsIgnoreCase("fb")) {
                            if(activity.getString(R.string.Try_Another_FB).equals(jsObject.getString("message"))) {
                                viewProfileModel.getData().getUser().get(0).setFacebookAccountLinked(0);
                            }else
                                viewProfileModel.getData().getUser().get(0).setFacebookAccountLinked(1);

                            LoginShared.setViewProfileDataModel(activity, viewProfileModel);
                        }

                        setSocialAddButtonStatus();

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(activity);
                        LoginShared.destroySessionTypePreference(activity);
                        LoginShared.setDeviceToken(activity, deviceToken);
                        Intent loginIntent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(loginIntent);
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        activity.finish();
                    } else {
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


    private void callapiforRemoveSocial(String medianame) {

        LoadingData loader = new LoadingData(activity);
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);

        String userID = LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId();

        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ResponseBody> socialoginapicall = apiInterface.call_socialRemoveApi(LoginShared.getRegistrationDataModel(activity).getData().getToken(), medianame, userID);

        socialoginapicall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {
                    String responseString = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.d("@@AddSocial : ", jsonObject.toString());


                    JSONObject jsObject = jsonObject.getJSONObject("data");
                    MethodUtils.errorMsg(activity, jsObject.getString("message"));


                    if (jsonObject.optInt("status") == 1) {

                        ViewProfileModel viewProfileModel = LoginShared.getViewProfileDataModel(activity);

                        if (medianame.equalsIgnoreCase("google")) {
                            viewProfileModel.getData().getUser().get(0).setGoogleAccountLinked(0);
                            LoginShared.setViewProfileDataModel(activity, viewProfileModel);
                        } else if (medianame.equalsIgnoreCase("fb")) {
                            viewProfileModel.getData().getUser().get(0).setFacebookAccountLinked(0);
                            LoginShared.setViewProfileDataModel(activity, viewProfileModel);
                        }
                        setSocialAddButtonStatus();
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
        doublePicker.show();
    }

    private void sendProfileImageUpdateApi() {
        loader.show_with_label("Loading");
        RequestBody gender = null;
        RequestBody preffered = null;
        RequestBody mainuservisibility = null;
        RequestBody password = null;
        RequestBody country = null;

        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), activity.et_full.getText().toString().trim());
        RequestBody middleName = RequestBody.create(MediaType.parse("text/plain"), activity.et_middle.getText().toString().trim());
        RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), activity.et_last.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), activity.et_phone.getText().toString().trim());
        RequestBody lifestyle = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedLifeStyle));
        RequestBody providerid = RequestBody.create(MediaType.parse("text/plain"), selectedProviderId);
        RequestBody bodycondition = RequestBody.create(MediaType.parse("text/plain"), "0");

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


        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getIsPasswordAvailable() == 0) {
            password = RequestBody.create(MediaType.parse("text/plain"), activity.et_new_password.getText().toString().trim());
        } else {
            password = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), activity.mCompressedFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("userImage",
                activity.mCompressedFile.getName(), reqFile);


        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            try {
                int index = countryList.indexOf(activity.et_country_name.getText().toString().trim());
                country = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(countryIDList.get(index)));
            } catch (Exception e) {
                e.printStackTrace();
                country = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(countryIDList.get(0)));
            }
        } else {
            country = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        RequestBody addressLine1;
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            addressLine1 = RequestBody.create(MediaType.parse("text/plain"), activity.et_add_line1.getText().toString().trim());
        } else {
            addressLine1 = RequestBody.create(MediaType.parse("text/plain"), "");
        }
        RequestBody addressLine2;
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            addressLine2 = RequestBody.create(MediaType.parse("text/plain"), activity.et_add_line2.getText().toString().trim());
        } else {
            addressLine2 = RequestBody.create(MediaType.parse("text/plain"), "");
        }
        RequestBody city;
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            city = RequestBody.create(MediaType.parse("text/plain"), activity.et_city.getText().toString().trim());
        } else {
            city = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        //***AVIK
        RequestBody state;
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            try {
                int index = stateList.indexOf(activity.et_state.getText().toString().trim());
                state = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stateIDList.get(index)));
            } catch (Exception e) {
                e.printStackTrace();
                state = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stateIDList.get(0)));
            }
        } else {
            state = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        RequestBody zipcode;
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            zipcode = RequestBody.create(MediaType.parse("text/plain"), activity.et_zipcode.getText().toString().trim());
        } else {
            zipcode = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        Call<ResponseBody> editProfile = apiInterface.call_editprofileImageApi(LoginShared.getRegistrationDataModel(activity).getData().getToken(),
                userId, fullName, middleName, lastName, gender, phone, dob, deviceType, user_email, Height, preffered, mainuservisibility, password,
                country, addressLine1, addressLine2, city, state, zipcode, lifestyle,bodycondition,providerid, body);

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
                        //setData();
                        getProfileDataAndSet();
                        activity.showImage();

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

    private void callCountryListApi() {
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<CountryList> coutryList = apiInterface.call_countryListApi();
        coutryList.enqueue(new Callback<CountryList>() {
            @Override
            public void onResponse(Call<CountryList> call, Response<CountryList> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        addPrefferedCountryList(response.body().getData());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CountryList> call, Throwable t) {

            }
        });
    }

    private void callStateListApi() {
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<StateResponse> coutryList = apiInterface.callstateListApi(selectedCountryId);
        coutryList.enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(Call<StateResponse> call, Response<StateResponse> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        addPrefferedStateList(response.body().getData());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<StateResponse> call, Throwable t) {

            }
        });
    }

    private void callProviderListApi() {
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<ProviderResponse> call = apiInterface.callProviderListApi();
        call.enqueue(new Callback<ProviderResponse>() {
            @Override
            public void onResponse(Call<ProviderResponse> call, Response<ProviderResponse> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        addProviderList(response.body().getData().getProviderlist());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ProviderResponse> call, Throwable t) {

            }
        });
    }
    private void addProviderList(List<ProviderlistItem> data) {

        providerIdList.clear();
        providerNameList.clear();
        for (int i = 0; i < data.size(); i++) {
            providerIdList.add(data.get(i).getId());
            providerNameList.add(data.get(i).getName());
        }

        providerPopup=new MyOptionsPickerView(activity);
        providerPopup.setPicker(providerNameList);
        providerPopup.setCyclic(false);
        providerPopup.setSelectOptions(0);

        providerPopup.setOnoptionsSelectListener((options1, option2, options3) -> {
            activity.et_provider.setText(providerNameList.get(options1));
            selectedProviderId=providerIdList.get(options1) ;
        });
    }



    private void addPrefferedCountryList(List<Datum> data) {

        countryList.clear();
        countryIDList.clear();
        for (int i = 0; i < data.size(); i++) {
            countryList.add(data.get(i).getCountryName());
            countryIDList.add(data.get(i).getCountryID());
        }


        countryListPopup=new MyOptionsPickerView(activity);
        countryListPopup.setPicker(countryList);
        countryListPopup.setCyclic(false);
        countryListPopup.setSelectOptions(0);


        countryListPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                activity.et_country_name.setText(countryList.get(options1));
                selectedCountryId=countryIDList.get(options1).toString() ;
                callStateListApi();
            }
        });
    }

    private void addPrefferedStateList(List<DataItem> data) {

        stateList.clear();
        stateIDList.clear();
        for (int i = 0; i < data.size(); i++) {

            if(isState) {
                if (i == 0) {
                    activity.et_state.setText(data.get(i).getStateName());
                }
            }
            isState = true;

            stateList.add(data.get(i).getStateName());
            stateIDList.add(data.get(i).getStateID());
        }

        stateListPopup=new MyOptionsPickerView(activity);
        stateListPopup.setPicker(stateList);
        stateListPopup.setCyclic(false);
        stateListPopup.setSelectOptions(0);


        stateListPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                activity.et_state.setText(stateList.get(options1));
            }
        });

    }


    private void sendProfileUpdateApi() {
        loader.show_with_label("Loading");
        RequestBody gender = null;
        RequestBody preffered = null;
        RequestBody mainuservisibility = null;
        RequestBody password = null;

        RequestBody country = null;

        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        RequestBody fullName = RequestBody.create(MediaType.parse("text/plain"), activity.et_full.getText().toString().trim());
        RequestBody middleName = RequestBody.create(MediaType.parse("text/plain"), activity.et_middle.getText().toString().trim());
        RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), activity.et_last.getText().toString().trim());
        RequestBody lifestyle = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedLifeStyle));
        RequestBody providerid = RequestBody.create(MediaType.parse("text/plain"), selectedProviderId);
        RequestBody bodycondition = RequestBody.create(MediaType.parse("text/plain"), "0");        //***AVIK

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

        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            try {
                int index = countryList.indexOf(activity.et_country_name.getText().toString().trim());
                country = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(countryIDList.get(index)));
            } catch (Exception e) {
                e.printStackTrace();
                country = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(countryIDList.get(0)));
            }
        } else {
            country = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        RequestBody addressLine1;
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            addressLine1 = RequestBody.create(MediaType.parse("text/plain"), activity.et_add_line1.getText().toString().trim());
        } else {
            addressLine1 = RequestBody.create(MediaType.parse("text/plain"), "");
        }
        RequestBody addressLine2;
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            addressLine2 = RequestBody.create(MediaType.parse("text/plain"), activity.et_add_line2.getText().toString().trim());
        } else {
            addressLine2 = RequestBody.create(MediaType.parse("text/plain"), "");
        }
        RequestBody city;
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            city = RequestBody.create(MediaType.parse("text/plain"), activity.et_city.getText().toString().trim());
        } else {
            city = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        //***AVIK
        RequestBody state;
        /*if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            state = RequestBody.create(MediaType.parse("text/plain"), activity.et_state.getText().toString().trim());
        } else {
            state = RequestBody.create(MediaType.parse("text/plain"), "");
        }*/

        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            try {
                int index = stateList.indexOf(activity.et_state.getText().toString().trim());
                state = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stateIDList.get(index)));
            } catch (Exception e) {
                e.printStackTrace();
                state = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stateIDList.get(0)));
            }
        } else {
            state = RequestBody.create(MediaType.parse("text/plain"), "");
        }
        RequestBody zipcode;
        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getScaleUserId().equals("1")) {
            zipcode = RequestBody.create(MediaType.parse("text/plain"), activity.et_zipcode.getText().toString().trim());
        } else {
            zipcode = RequestBody.create(MediaType.parse("text/plain"), "");
        }


        if (activity.switch_visibility.isChecked()) {
            mainuservisibility = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            mainuservisibility = RequestBody.create(MediaType.parse("text/plain"), "0");
        }


        if (LoginShared.getViewProfileDataModel(activity).getData().getUser().get(0).getIsPasswordAvailable() == 0) {
            password = RequestBody.create(MediaType.parse("text/plain"), activity.et_new_password.getText().toString().trim());
        } else {
            password = RequestBody.create(MediaType.parse("text/plain"), "");
        }


        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "Android");
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId());
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), activity.et_DOB.getText().toString().trim());
        RequestBody user_email = RequestBody.create(MediaType.parse("text/plain"), activity.et_email.getText().toString().trim());
        String str = activity.et_height.getText().toString().trim();
        String[] splited = str.split(" ");
        RequestBody Height = RequestBody.create(MediaType.parse("text/plain"), splited[0]);

        Call<ResponseBody> editProfile = apiInterface.call_editprofileApi(LoginShared.getRegistrationDataModel(activity).getData().getToken(),
                userId, fullName, middleName, lastName, gender, phone, dob, deviceType, user_email, Height, preffered, mainuservisibility,
                password, country, addressLine1, addressLine2, city, state, zipcode, lifestyle,bodycondition,providerid);

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
                        //setData();
                        getProfileDataAndSet();
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
                    e.printStackTrace();
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
        weigtUniversalPopupPreferred.show();
    }

    private void ExpiryDialog() {

        Calendar mCalendar;
        mCalendar = Calendar.getInstance();
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

        genderPopup.show();

    }

    private void showBodyPopup() {
        Intent intent=new Intent(activity, BodyActivity.class);
        intent.putExtra("selectedBody",activity.bodyList);
        activity.startActivityForResult(intent,BODY_CONDITION);
    }

    private void showAndDismissLifeStylePopup() {
        lifeStylePopup.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}