package com.surefiz.screens.registration;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.profile.model.ViewProfileModel;
import com.surefiz.screens.termcondition.TermAndConditionActivity;
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
    int regType = -1;
    @BindView(R.id.tv_upload)
    TextView tv_upload;
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
    @BindView(R.id.et_confirm_email)
    EditText et_confirm_email;
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
    @BindView(R.id.rl_scale_id)
    RelativeLayout rl_scale_id;
    @BindView(R.id.et_scale_id)
    EditText et_scale_id;
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

    private File mFile = null;
    private Uri fileUri = null;
    private OnImageSet onImageSet;
    private LoadingData loader;
    private ImageLoader imageLoader;

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
            tv_password.setVisibility(View.GONE);
            star_image_password.setVisibility(View.GONE);
            ll_password.setVisibility(View.GONE);
            rl_password.setVisibility(View.GONE);
            profile_image.setVisibility(View.VISIBLE);
            iv_plus_add_image.setVisibility(View.VISIBLE);
            tv_upload.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.GONE);

            //tv_scale.setVisibility(View.GONE);
            //rl_scale.setVisibility(View.GONE);

            //----------Addited By Arup---------//

            ll_scale_id.setVisibility(View.VISIBLE);
            rl_scale_id.setVisibility(View.VISIBLE);

            rl_street_address.setVisibility(View.GONE);
            ll_street_address.setVisibility(View.GONE);
            rl_city.setVisibility(View.GONE);
            ll_city.setVisibility(View.GONE);
            rl_state.setVisibility(View.GONE);
            ll_state.setVisibility(View.GONE);
            rl_zip_code.setVisibility(View.GONE);
            ll_zip_code.setVisibility(View.GONE);
            ll_signup_member.setVisibility(View.GONE);

            btn_register.setText("Update");
            //btn_register.setText("Register");
            isInCompleteProfile = true;
            //btn_skip_config.setVisibility(View.GONE);


            getProfileDataAndSet();

            //--------------END----------------//
        } else {
            tv_password.setVisibility(View.VISIBLE);
            star_image_password.setVisibility(View.GONE);
            rl_password.setVisibility(View.VISIBLE);
            profile_image.setVisibility(View.VISIBLE);
            iv_plus_add_image.setVisibility(View.VISIBLE);
            tv_upload.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.VISIBLE);
            //btn_skip_config.setVisibility(View.GONE);

            //tv_scale.setVisibility(View.VISIBLE);
            //rl_scale.setVisibility(View.VISIBLE);
        }
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

        final Call<ResponseBody> call_view_profile = apiInterface.call_viewprofileApi(LoginShared.getRegistrationDataModel(RegistrationActivity.this).getData().getToken(),
                LoginShared.getRegistrationDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserId());

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
        et_first_name.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserName());
        et_middle_name.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getMiddleName());
        et_last_name.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getLastName());

        et_email.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserEmail());
        et_confirm_email.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserEmail());
        et_phone.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserPhoneNumber());
        et_gender.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserGender());

        if (!checkIsZeroValue(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserDob())) {
            age.setText(LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserDob());
        } else {
            age.setText("");
        }

        LoginShared.setUserName(RegistrationActivity.this, LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getUserName());

        if (LoginShared.getViewProfileDataModel(RegistrationActivity.this).getData().getUser().get(0).getPreferredUnits().equalsIgnoreCase("1")) {
            et_units.setText("KG/CM");
        } else {
            et_units.setText("LB/INCH");
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

        registrationClickEvent.setValuesForListItem(bundle);

        showImage();
    }


    public boolean checkIsZeroValue(String value) {
        if (value.equalsIgnoreCase("0") || value.equalsIgnoreCase("0.0") || value.equalsIgnoreCase("0.00")) {
            return true;
        }

        return false;
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
                "<a href='com.surefiz.screens.termcondition.TermAndConditionActivity://Kode'><font color='#3981F5'>Terms & Condition</font></a>"));
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
}
