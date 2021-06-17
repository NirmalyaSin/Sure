package com.surefiz.screens.registration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.barcodescanner.BarCodeScanner;
import com.surefiz.screens.bodycodition.BodyActivity;
import com.surefiz.screens.bodycodition.model.BodyItem;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.profile.model.profile.ViewProfileModel;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MediaUtils;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.os.Build.VERSION_CODES.M;

public class RegistrationActivity extends AppCompatActivity {
    public static final String PICTURE_NAME = "SureFIZProfile";
    public static final String FOLDER_NAME = "SureFIZ";
    public static final int CAMERA = 1, GALLERY = 2;
    public File mCompressedFile = null;
    public RegistrationClickEvent registrationClickEvent;
    public boolean isInCompleteProfile = false;
    public RegistrationModel registrationModel;
    int regType = -1;
    @BindView(R.id.tv_upload)
    TextView tv_upload;
    @BindView(R.id.tv_registration)
    TextView tv_registration;
    @BindView(R.id.tv_password)
    TextView tv_password;
    @BindView(R.id.rl_password)
    RelativeLayout rl_password;
    @BindView(R.id.et_gender)
    EditText et_gender;
    @BindView(R.id.et_units)
    EditText et_units;
    @BindView(R.id.et_height)
    EditText et_height;
    @BindView(R.id.et_lifestyle)
    EditText et_lifestyle;
    @BindView(R.id.et_weight)
    EditText et_weight;
    @BindView(R.id.et_time_loss)
    EditText et_time_loss;
    @BindView(R.id.profile_image)
    de.hdodenhof.circleimageview.CircleImageView profile_image;
    @BindView(R.id.iv_plus_add_image)
    ImageView iv_plus_add_image;
    @BindView(R.id.star_image_password)
    ImageView star_image_password;
    @BindView(R.id.btn_register)
    Button btn_register;
    @BindView(R.id.et_first_name)
    EditText et_first_name;
    @BindView(R.id.et_email)
    EditText et_email;
    /*@BindView(R.id.et_confirm_email)
    EditText et_confirm_email;*/
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;
    @BindView(R.id.checkBoxTermsCondition)
    CheckBox checkBoxTermsCondition;
    @BindView(R.id.textTermsCondition)
    TextView textTermsCondition;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.rl_weight)
    RelativeLayout rl_weight;
    @BindView(R.id.et_management)
    EditText et_management;
    @BindView(R.id.rl_main_registration)
    RelativeLayout rl_main_registration;
    @BindView(R.id.tv_weight)
    LinearLayout tv_weight;
    @BindView(R.id.tv_userSelection)
    LinearLayout tv_userSelection;
    @BindView(R.id.rl_userselection)
    RelativeLayout rl_userselection;
    @BindView(R.id.et_userselection)
    EditText et_userselection;
    @BindView(R.id.tv_time_loss)
    LinearLayout tv_time_loss;
    @BindView(R.id.rl_time_loss)
    RelativeLayout rl_time_loss;
    @BindView(R.id.et_middle_name)
    EditText et_middle_name;
    @BindView(R.id.et_last_name)
    EditText et_last_name;
    @BindView(R.id.et_age)
    EditText age;
    @BindView(R.id.et_address)
    EditText address;
    @BindView(R.id.et_city)
    EditText city;
    @BindView(R.id.et_state)
    EditText state;
    @BindView(R.id.et_zipcode)
    EditText zipcode;
    @BindView(R.id.ll_signup_member)
    LinearLayout ll_signup_member;
    @BindView(R.id.et_member)
    EditText et_member;
    @BindView(R.id.ll_scale_id)
    LinearLayout ll_scale_id;
    @BindView(R.id.ll_confirm_scale_id)
    LinearLayout ll_confirm_scale_id;
    @BindView(R.id.et_scale_id)
    EditText et_scale_id;
    @BindView(R.id.tv_scale)
    TextView tv_scale;
    @BindView(R.id.starScale)
    ImageView starScale;
    @BindView(R.id.et_confirm_scale_id)
    EditText et_confirm_scale_id;
    @BindView(R.id.ll_password)
    LinearLayout ll_password;
    @BindView(R.id.rl_street_address)
    RelativeLayout rl_street_address;
    @BindView(R.id.ll_street_address)
    LinearLayout ll_street_address;
    @BindView(R.id.rl_city)
    RelativeLayout rl_city;
    @BindView(R.id.ll_city)
    LinearLayout ll_city;
    @BindView(R.id.rl_state)
    RelativeLayout rl_state;
    @BindView(R.id.ll_state)
    LinearLayout ll_state;
    @BindView(R.id.rl_zip_code)
    RelativeLayout rl_zip_code;
    @BindView(R.id.ll_zip_code)
    LinearLayout ll_zip_code;
    @BindView(R.id.btn_skip_config)
    Button btn_skip_config;
    @BindView(R.id.toolTipScaleId)
    ImageView toolTipScaleId;
    @BindView(R.id.toolTipConfirmScaleId)
    ImageView toolTipConfirmScaleId;
    @BindView(R.id.btn_scan)
    TextView btn_scan;

    @BindView(R.id.tv_body)
    TextView tv_body;

    @BindView(R.id.rl_body)
    RelativeLayout rl_body;

    @BindView(R.id.et_body)
    EditText et_body;

    String toolTipText = "";
    private File mFile = null;
    private Uri fileUri = null;
    private OnImageSet onImageSet;
    private LoadingData loader;
    private ImageLoader imageLoader;


    private static final int REQUEST_BAR_CODE = 101;
    private static final int BODY_CONDITION = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        loader = new LoadingData(RegistrationActivity.this);
        initializeImageLoader();
        viewShowFromLogin();
        viewShowFromSignup();
        setTermsAndCondition();
        registrationClickEvent = new RegistrationClickEvent(this);

        setTextFormatter();
    }

    private void setTextFormatter() {
        et_scale_id.addTextChangedListener(new TextWatcher() {

            private boolean isEdiging = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEdiging) return;
                isEdiging = true;
                // removing old dashes
                StringBuilder sb = new StringBuilder();
                sb.append(s.toString().trim().replace("-", ""));

                if (sb.length() > 2)
                    sb.insert(2, "-");
                if (sb.length() > 7)
                    sb.insert(7, "-");
                if (sb.length() > 12)
                    sb.delete(12, sb.length());

                s.replace(0, s.length(), sb.toString());
                isEdiging = false;
            }
        });

        et_confirm_scale_id.addTextChangedListener(new TextWatcher() {

            private boolean isEdiging = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEdiging) return;
                isEdiging = true;
                // removing old dashes
                StringBuilder sb = new StringBuilder();
                sb.append(s.toString().trim().replace("-", ""));

                if (sb.length() > 2)
                    sb.insert(2, "-");
                if (sb.length() > 7)
                    sb.insert(7, "-");
                if (sb.length() > 12)
                    sb.delete(12, sb.length());

                s.replace(0, s.length(), sb.toString());
                isEdiging = false;
            }
        });
    }



    protected void callScanner(){

        if (checkStoragePermission()) {
            Intent intent=new Intent(this, BarCodeScanner.class);
            startActivityForResult(intent,REQUEST_BAR_CODE);
        } else {
            requestStoragePermission();
        }
    }

    private void initializeImageLoader() {
        DisplayImageOptions opts = new DisplayImageOptions.Builder().cacheInMemory(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(RegistrationActivity.this)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).defaultDisplayImageOptions(opts)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private void viewShowFromLogin() {
        if (getIntent().getStringExtra("completeStatus").equals("0")) {

            Gson gson = new Gson();

            if (getIntent().hasExtra("registrationModelData")) {
                registrationModel = gson.fromJson(getIntent().getStringExtra("registrationModelData"), RegistrationModel.class);
            } else {
                registrationModel = LoginShared.getRegistrationDataModel(this);
            }

            Log.d("Registration-","-isfirsttime-"+registrationModel.getData().getUser().get(0).getIsfirsttime());

            tv_password.setVisibility(View.GONE);
            star_image_password.setVisibility(View.GONE);
            ll_password.setVisibility(View.GONE);
            rl_password.setVisibility(View.GONE);
            profile_image.setVisibility(View.VISIBLE);
            iv_plus_add_image.setVisibility(View.VISIBLE);
            tv_upload.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.GONE);


            //----------Addited By Arup---------//

            ll_confirm_scale_id.setVisibility(View.VISIBLE);
            ll_scale_id.setVisibility(View.VISIBLE);

            rl_street_address.setVisibility(View.GONE);
            ll_street_address.setVisibility(View.GONE);
            rl_city.setVisibility(View.GONE);
            ll_city.setVisibility(View.GONE);
            rl_state.setVisibility(View.GONE);
            ll_state.setVisibility(View.GONE);
            rl_zip_code.setVisibility(View.GONE);
            ll_zip_code.setVisibility(View.GONE);
            ll_signup_member.setVisibility(View.GONE);

            isInCompleteProfile = true;


            getProfileDataAndSet();

            //--------------END----------------//
        } else {
            tv_password.setVisibility(View.VISIBLE);
            star_image_password.setVisibility(View.GONE);
            rl_password.setVisibility(View.VISIBLE);
            profile_image.setVisibility(View.VISIBLE);
            iv_plus_add_image.setVisibility(View.VISIBLE);
            tv_upload.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.GONE);


        }


        toolTipScaleId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SimpleTooltip.Builder(RegistrationActivity.this)
                        .anchorView(v)
                        .backgroundColor(getResources().getColor(R.color.whiteColor))
                        .arrowColor(getResources().getColor(R.color.whiteColor))
                        .text(toolTipText)
                        .gravity(Gravity.START)
                        .animated(false)
                        .transparentOverlay(true)
                        .build()
                        .show();
            }
        });

        toolTipConfirmScaleId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SimpleTooltip.Builder(RegistrationActivity.this)
                        .anchorView(v)
                        .backgroundColor(getResources().getColor(R.color.whiteColor))
                        .arrowColor(getResources().getColor(R.color.whiteColor))
                        .text(toolTipText)
                        .gravity(Gravity.START)
                        .animated(false)
                        .transparentOverlay(true)
                        .build()
                        .show();
            }
        });
    }

    private void viewShowFromSignup() {
        if (getIntent().hasExtra("membership"))
            if (getIntent().getStringExtra("membership").equals("single")) {
                regType = 1;
                ll_signup_member.setVisibility(View.GONE);
            } else if (getIntent().getStringExtra("membership").equals("group")) {
                regType = 2;
                ll_signup_member.setVisibility(View.VISIBLE);
            } else {
                regType = 3;
                ll_signup_member.setVisibility(View.VISIBLE);
            }
    }

    private void getProfileDataAndSet() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<ResponseBody> call_view_profile = apiInterface.call_viewprofileApi(registrationModel.getData().getToken(),
                registrationModel.getData().getUser().get(0).getUserId());

        System.out.println("@@ProfileInfo :" + call_view_profile);

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

                    Log.d("@@ProfileDataAndSet : ", jsonObject.toString());

                    if (jsonObject.optInt("status") == 1) {
                        viewProfileModel = gson.fromJson(responseString, ViewProfileModel.class);
                        LoginShared.setViewProfileDataModel(RegistrationActivity.this, viewProfileModel);

                        setData();

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(RegistrationActivity.this);
                        LoginShared.destroySessionTypePreference(RegistrationActivity.this);
                        LoginShared.setDeviceToken(RegistrationActivity.this, deviceToken);
                        Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        RegistrationActivity.this.startActivity(loginIntent);
                        RegistrationActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        RegistrationActivity.this.finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(RegistrationActivity.this, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(RegistrationActivity.this, RegistrationActivity.this.getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(RegistrationActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void setData() {
        registrationClickEvent.addBodyList();

        et_first_name.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserName());
        et_middle_name.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getMiddleName());
        et_last_name.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getLastName());

        et_email.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserEmail());
        et_phone.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserPhoneNumber());
        et_body.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getBodycondition());

        et_scale_id.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getScaleid());
        et_confirm_scale_id.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getScaleid());

        if (LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserGender().equals("1")) {
            et_gender.setText("Male");
        } else if (LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserGender().equals("0")) {
            et_gender.setText("Female");
        } else if (LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserGender().equals("2")) {
            et_gender.setText("Non-binary");
        } else {
            et_gender.setText("Prefer not to say");
        }
        //}

        if (!checkIsZeroValue(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserDob())) {
            age.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserDob());
        } else {
            age.setText("");
        }

        LoginShared.setUserName(RegistrationActivity.this, LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserName());

        if (LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getPreferredUnits().equalsIgnoreCase("1")) {
            et_units.setText("KG/CM");
        } else {
            et_units.setText("LBS/INCH");
        }
        String unit = "";
        if (LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getPreferredUnits().equalsIgnoreCase("1")) {
            unit = "CM";
        } else {
            unit = "INCH";
        }

        if (!checkIsZeroValue(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getHeight())) {
            et_height.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getHeight() + " " + unit);
        } else {
            et_height.setText("");
        }

        if (!checkIsZeroValue(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getTime())) {
            et_time_loss.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getTime() + " " + "Weeks");
        } else {
            et_time_loss.setText("");
        }

        Bundle bundle = new Bundle();
        bundle.putString("type", LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getType());
        bundle.putString("weight", LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getTargetWeight());
        bundle.putString("time", LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getTime());


        if (LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getScaleUserId().equalsIgnoreCase("1")) {
            btn_register.setText(getResources().getString(R.string.register));
            tv_registration.setText("Register");
        } else {
            btn_register.setText(getResources().getString(R.string.complete_sign_up));
            tv_registration.setText(getResources().getString(R.string.surefiz_signup));
        }

        registrationClickEvent.setValuesForListItem(bundle);
        registrationClickEvent.setValuesForWeightSelection(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getIsServerWeight());
        registrationClickEvent.setLifeStyle(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getLifestyle());

        showImage();
        disableViews();


        if (LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getScaleUserId().equalsIgnoreCase("1")) {
            toolTipText = "Scale ID is labeled on\nthe back of your scale.";
        } else {
            //toolTipText = "Scale ID is\nassigned by primary user";
            toolTipText = "Your primary user has provided\nthe Scale ID,there is no action\nrequired for Scale ID from you.";
            et_scale_id.setHint("");
            //et_scale_id.setHint("Scale ID is assigned by primary user");
           // et_confirm_scale_id.setHint("Scale ID is assigned by primary user");
        }
    }

    private void disableViews() {
        if (LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getScaleUserId().equalsIgnoreCase("1")) {
            et_first_name.setEnabled(false);
            et_middle_name.setEnabled(false);
            et_last_name.setEnabled(false);
            et_email.setEnabled(false);
            //et_confirm_email.setEnabled(false);
            et_lifestyle.setEnabled(false);
            et_phone.setEnabled(false);
            et_body.setEnabled(false);
            et_gender.setEnabled(false);
            age.setEnabled(false);
            et_units.setEnabled(false);
            et_management.setEnabled(false);
            et_weight.setEnabled(false);
            et_height.setEnabled(false);
            et_time_loss.setEnabled(false);
            iv_plus_add_image.setEnabled(false);
            et_userselection.setEnabled(false);
            et_scale_id.setEnabled(true);
            et_confirm_scale_id.setEnabled(true);
        } else {
            et_units.setEnabled(false);
            btn_scan.setVisibility(View.GONE);
            ll_scale_id.setVisibility(View.VISIBLE);
            et_scale_id.setEnabled(false);
            tv_scale.setTextColor(getResources().getColor(R.color.new_grey));
            et_scale_id.setTextColor(getResources().getColor(R.color.new_grey));
            et_units.setTextColor(getResources().getColor(R.color.new_grey));
            starScale.setVisibility(View.GONE);
            ll_confirm_scale_id.setVisibility(View.GONE);
            et_confirm_scale_id.setEnabled(false);

            ll_scale_id.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            ll_scale_id.setPadding(0,0,0,0);

        }
    }

    protected void showBodyPopup() {


        Intent intent=new Intent(this, BodyActivity.class);
        intent.putExtra("selectedBody",registrationClickEvent.bodyList);
        startActivityForResult(intent,BODY_CONDITION);

    }


    public boolean checkIsZeroValue(String value) {
        return value.equalsIgnoreCase("0") || value.equalsIgnoreCase("0.0") || value.equalsIgnoreCase("0.00");

    }

    private void showImage() {
        if (LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().
                get(0).getUserImage().equals("") ||
                LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().
                        get(0).getUserImage().equalsIgnoreCase("null") ||
                LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().
                        get(0).getUserImage() == null) {
            profile_image.setImageDrawable(ContextCompat.getDrawable(RegistrationActivity.this, R.drawable.prof_img_placeholder));
        } else {
            String url = LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().
                    get(0).getUserImage();
            LoginShared.setUserPhoto(RegistrationActivity.this, LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().
                    get(0).getUserImage());
            url = url.replace(" ", "20%");
            imageLoader.displayImage(url, RegistrationActivity.this.profile_image);
        }
    }

    private void setTermsAndCondition() {
        checkBoxTermsCondition.setText("");
        textTermsCondition.setText(Html.fromHtml("I have read and agree to the " +
                "<a href='com.surefiz.screens.termcondition.TermAndConditionActivity://Kode'><font color='#3981F5'>Terms and Conditions</font></a>"));
        textTermsCondition.setClickable(true);
        textTermsCondition.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void choiceMedia(final int currentChoice, OnImageSet onImageSet) {
        this.onImageSet = onImageSet;
        TedPermission permission = new TedPermission(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        switch (currentChoice) {
                            case CAMERA:
                                openCamera();
                                break;
                            case GALLERY:
                                openGallery();
                                break;
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(RegistrationActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                })
                .setDeniedMessage(getResources().getString(R.string.permission_not_given));
        switch (currentChoice) {
            case CAMERA:
                permission.setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA);
                break;
            case GALLERY:
                permission.setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
        }

        permission.check();
    }

    private void openGallery() {
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
    }

    private void openCamera() {
        generateFile();
        captureImage();
    }

    private void generateFile() {
        String fileName = PICTURE_NAME + new SimpleDateFormat("mm_dd_yyyy_HH_mm_ss").format(new Date());
        mFile = MediaUtils.getOutputMediaFile(RegistrationActivity.this, MediaUtils.MEDIA_TYPE_IMAGE, FOLDER_NAME, fileName);
    }

    /*
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri();

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA);
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri() {
        Uri photoURI;

        if (Build.VERSION.SDK_INT > M) {
            photoURI = FileProvider.getUriForFile(RegistrationActivity.this, getApplicationContext().getPackageName() +
                    ".provider", mFile);

        } else {
            photoURI = Uri.fromFile(mFile);
        }
        return photoURI;
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */

    protected String getSelectedItem(){
        String s="";
        for (int i = 0;i<registrationClickEvent.bodyList.size();i++){
            if(registrationClickEvent.bodyList.get(i).isSelection()==true){
                s=s+registrationClickEvent.bodyList.get(i).getName()+",";
            }
        }

        if(!s.equals(""))
            s=s.substring(0,s.length()-1);

        return s;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image

        switch (requestCode) {
            case CAMERA:

                if (resultCode == RESULT_OK) {
                    // successfully captured the image
                    // display it in image view
                    compressImage();
                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.image_capture_cancel_text), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_to_capture_image_text), Toast.LENGTH_SHORT)
                            .show();
                }

                break;

            case GALLERY:

                if (resultCode == RESULT_OK) {
                    // successfully captured the image
                    // display it in image view

                    try {
                        mFile = FileUtil.from(this, data.getData());
                        compressImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled Image capture
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.image_capture_cancel_text), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_to_capture_image_text), Toast.LENGTH_SHORT)
                            .show();
                }

                break;

            case REQUEST_BAR_CODE:

                //***AVIK
                if ((resultCode == RESULT_OK)) {
                    et_scale_id.setText(data.getStringExtra("barCode"));
                    et_confirm_scale_id.setText(data.getStringExtra("barCode"));
                }

                break;

            case BODY_CONDITION:
                if(resultCode==RESULT_OK){
                    registrationClickEvent.bodyList= (ArrayList<BodyItem>) data.getSerializableExtra("selectedBody");

                    String selectedBody=getSelectedItem();
                    et_body.setText(selectedBody);
                    Log.d("Selected Body","::::::::::"+selectedBody);
                }
                break;

            default:
                break;

        }


    }

    /**
     * compressing the image
     */
    private void compressImage() {
        Compressor.getDefault(this)
                .compressToFileAsObservable(mFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        mCompressedFile = file;
                        previewCapturedImage();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.image_compression_error_text), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Display image from a path to ImageView
     */
    public void previewCapturedImage() {
        try {
            // hide video preview

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 1;

            final Bitmap bitmap = BitmapFactory.decodeFile(mCompressedFile.getPath(), options);

            profile_image.setImageBitmap(bitmap);
            if (onImageSet != null)
                onImageSet.onSuccess(mCompressedFile.getPath());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //***AVIK
    protected boolean checkStoragePermission() {
        return (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    //***AVIK
    protected void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA);
    }

    //***AVIK
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent=new Intent(this, BarCodeScanner.class);
                startActivityForResult(intent,REQUEST_BAR_CODE);

            }
        }
    }

    @Override
    public void onBackPressed() {

        if(isTaskRoot()){
            startActivity(new Intent(this,LoginActivity.class));
        }else {
            super.onBackPressed();
        }
        finish();

    }
}