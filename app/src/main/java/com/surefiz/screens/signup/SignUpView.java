package com.surefiz.screens.signup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.MyOptionsPickerView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.heightpopup.DoublePicker;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.dialog.weightpopup.WeigtUniversalPopup;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.bodycodition.model.BodyItem;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.registration.RegistrationClickEvent;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.signup.response.SignUpResponse;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MediaUtils;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.RealPathUtil;
import com.surefiz.utils.progressloader.LoadingData;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.os.Build.VERSION_CODES.M;

public class SignUpView extends AppCompatActivity {

    protected static final String PICTURE_NAME = "SureFIZProfile";
    protected static final String FOLDER_NAME = "SureFIZ";
    protected static final int CAMERA = 1, GALLERY = 2;
    protected File mCompressedFile = null;
    protected RegistrationClickEvent registrationClickEvent;
    protected boolean isInCompleteProfile = false;
    protected RegistrationModel registrationModel;
    int regType = 1;
    @BindView(R.id.tv_upload)
    protected TextView tv_upload;
    @BindView(R.id.tv_registration)
    protected TextView tv_registration;
    @BindView(R.id.tv_password)
    protected TextView tv_password;
    @BindView(R.id.rl_password)
    protected RelativeLayout rl_password;
    @BindView(R.id.et_gender)
    protected EditText et_gender;
    @BindView(R.id.et_units)
    protected EditText et_units;
    @BindView(R.id.et_height)
    protected EditText et_height;
    @BindView(R.id.et_lifestyle)
    protected EditText et_lifestyle;
    @BindView(R.id.et_weight)
    protected EditText et_weight;
    @BindView(R.id.et_time_loss)
    protected EditText et_time_loss;
    @BindView(R.id.profile_image)
    protected CircleImageView profile_image;
    @BindView(R.id.iv_plus_add_image)
    protected ImageView iv_plus_add_image;
    @BindView(R.id.star_image_password)
    protected ImageView star_image_password;
    @BindView(R.id.btn_register)
    protected TextView btn_register;
    @BindView(R.id.et_first_name)
    protected EditText et_first_name;
    @BindView(R.id.et_email)
    protected EditText et_email;
    @BindView(R.id.et_confirm_email)
    protected EditText et_confirm_email;
    @BindView(R.id.et_password)
    protected EditText et_password;
    @BindView(R.id.et_phone)
    protected EditText et_phone;
    @BindView(R.id.rl_back)
    protected RelativeLayout rl_back;
    @BindView(R.id.checkBoxTermsCondition)
    protected CheckBox checkBoxTermsCondition;
    @BindView(R.id.textTermsCondition)
    protected TextView textTermsCondition;
    @BindView(R.id.linearLayout1)
    protected LinearLayout linearLayout1;
    @BindView(R.id.rl_weight)
    protected RelativeLayout rl_weight;
    @BindView(R.id.et_management)
    protected EditText et_management;
    @BindView(R.id.rl_main_registration)
    protected RelativeLayout rl_main_registration;
    @BindView(R.id.tv_weight)
    protected LinearLayout tv_weight;
    @BindView(R.id.tv_userSelection)
    protected LinearLayout tv_userSelection;
    @BindView(R.id.rl_userselection)
    protected RelativeLayout rl_userselection;
    @BindView(R.id.et_userselection)
    protected EditText et_userselection;
    @BindView(R.id.tv_time_loss)
    protected LinearLayout tv_time_loss;
    @BindView(R.id.rl_time_loss)
    protected RelativeLayout rl_time_loss;
    @BindView(R.id.et_middle_name)
    protected EditText et_middle_name;
    @BindView(R.id.et_last_name)
    protected EditText et_last_name;
    @BindView(R.id.et_age)
    protected EditText age;
    @BindView(R.id.et_add_line1)
    protected EditText et_add_line1;
    @BindView(R.id.et_add_line2)
    protected EditText et_add_line2;
    @BindView(R.id.et_city)
    protected EditText et_city;
    @BindView(R.id.et_state)
    protected EditText et_state;
    @BindView(R.id.et_country_name)
    protected EditText et_country_name;
    @BindView(R.id.et_zipcode)
    protected EditText zipcode;
    @BindView(R.id.ll_scale_id)
    protected LinearLayout ll_scale_id;
    @BindView(R.id.ll_confirm_scale_id)
    protected LinearLayout ll_confirm_scale_id;
    @BindView(R.id.et_scale_id)
    protected EditText et_scale_id;
    @BindView(R.id.et_confirm_scale_id)
    protected EditText et_confirm_scale_id;
    @BindView(R.id.ll_password)
    protected LinearLayout ll_password;
    @BindView(R.id.rl_street_address)
    protected RelativeLayout rl_street_address;
    @BindView(R.id.ll_street_address)
    protected LinearLayout ll_street_address;
    @BindView(R.id.rl_city)
    protected RelativeLayout rl_city;
    @BindView(R.id.ll_city)
    protected LinearLayout ll_city;
    @BindView(R.id.rl_state)
    protected RelativeLayout rl_state;
    @BindView(R.id.ll_state)
    protected LinearLayout ll_state;
    @BindView(R.id.rl_zip_code)
    protected RelativeLayout rl_zip_code;
    @BindView(R.id.ll_zip_code)
    protected LinearLayout ll_zip_code;
    @BindView(R.id.btn_skip_config)
    protected Button btn_skip_config;
    @BindView(R.id.toolTipScaleId)
    protected ImageView toolTipScaleId;
    @BindView(R.id.toolTipConfirmScaleId)
    protected ImageView toolTipConfirmScaleId;
    @BindView(R.id.btn_scan)
    protected TextView btn_scan;
    @BindView(R.id.tv_body)
    protected TextView tv_body;
    @BindView(R.id.rl_body)
    protected RelativeLayout rl_body;
    @BindView(R.id.et_body)
    protected EditText et_body;
    @BindView(R.id.et_learn_about)
    protected EditText et_learn_about;

    protected String toolTipText = "";
    protected File mFile = null;
    protected String filePath = "";
    protected Uri fileUri = null;
    protected OnImageSet onImageSet;
    protected LoadingData loader;

    String units = "", height = "",weight = "";
    String[] splited;

    protected ArrayList<String> genderList = new ArrayList<>();
    protected ArrayList<String> lifeStyleList = new ArrayList<>();
    protected ArrayList<String> prefferedList = new ArrayList<>();
    protected ArrayList<String> countryList = new ArrayList<>();
    protected ArrayList<Integer> countryIDList = new ArrayList<>();
    protected ArrayList<String> stateList = new ArrayList<>();
    protected ArrayList<String> stateIDList = new ArrayList<>();
    protected ArrayList<String> timeList = new ArrayList<>();
    protected ArrayList<String> weightList = new ArrayList<>();
    protected ArrayList<String> managementList = new ArrayList<>();
    protected ArrayList<String> desiredWeightSelectionList = new ArrayList<>();
    protected ArrayList<BodyItem> bodyList = new ArrayList<>();
    protected ArrayList<String> learnList = new ArrayList<>();
    protected DoublePicker doublePicker;
    protected int selectedLifeStyle = 0;
    protected String selectedCountryId = "";
    protected String selectedStateId = "";
    protected boolean isState=false;
    protected int selectedWeightManagmentGoal = 0;
    protected int selectedDesiredWeightSelection = 0;
    protected String orderId = "";
    protected String scaleId = "";
    protected MyOptionsPickerView lifeStylePopup,managementPopup,weigtUniversalPopupPreferred,countryListPopup;
    protected MyOptionsPickerView genderPopup,stateListPopup,weightPopup,timePopup,learnPopup,selectionPopup;

    protected SignUpOnClick signUpOnClick;


    protected void hideSoftKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


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
                        //tvUserImageHint.setVisibility(View.VISIBLE);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(SignUpView.this, getResources().getString(R.string.image_compression_error_text), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void previewCapturedImage() {
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = 1;

            final Bitmap bitmap = BitmapFactory.decodeFile(mCompressedFile.getPath(), options);

            profile_image.setImageBitmap(bitmap);
            if (onImageSet != null)
                onImageSet.onSuccess(mCompressedFile.getPath());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE :
                CropImage.ActivityResult cresult = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri uri = cresult.getUri();

                    String realPath= RealPathUtil.getRealPath(this, uri);
                    mFile=new File(realPath);
                    compressImage();

                    Log.d("Image_Path", "::::" + RealPathUtil.getRealPath(this, uri));


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = cresult.getError();
                    Toast.makeText(this, getResources().getString(R.string.failed_to_capture_image_text), Toast.LENGTH_SHORT).show();

                }

        }

    }

    protected void callSignUpApi() {

        loader.show_with_label("Loading");

        RequestBody gender = null;
        RequestBody prefferedUnits = null;
        RequestBody type = null;
        RequestBody maintain_Weight_By_Server = null;
        RequestBody desiredWeight = null;
        RequestBody timeToloseWeight = null;
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        MultipartBody.Part body=null;
        if(mCompressedFile!=null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), mCompressedFile);
            body = MultipartBody.Part.createFormData("userImage", mCompressedFile.getName(), reqFile);
        }

        RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), et_first_name.getText().toString().trim());
        RequestBody middle_name = RequestBody.create(MediaType.parse("text/plain"), et_middle_name.getText().toString().trim());
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), et_last_name.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), et_email.getText().toString().trim());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), et_password.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), et_phone.getText().toString().trim());

        if (et_units.getText().toString().trim().equalsIgnoreCase("KG/CM")) {
            prefferedUnits = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            prefferedUnits = RequestBody.create(MediaType.parse("text/plain"), "0");
        }
        if (et_gender.getText().toString().trim().equals("Male")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else if (et_gender.getText().toString().trim().equals("Female")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "0");
        } else if (et_gender.getText().toString().trim().equalsIgnoreCase("Non-binary")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "2");
        } else {
            gender = RequestBody.create(MediaType.parse("text/plain"), "3");
        }


        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), age.getText().toString().trim());
        String str = et_height.getText().toString().trim();
        String[] splited = str.split(" ");
        RequestBody height = RequestBody.create(MediaType.parse("text/plain"), splited[0]);
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "2");
        RequestBody deviceToken = RequestBody.create(MediaType.parse("text/plain"), LoginShared.getDeviceToken(this));
        RequestBody count = RequestBody.create(MediaType.parse("text/plain"), "0");
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), et_city.getText().toString());
        RequestBody addressLineOne = RequestBody.create(MediaType.parse("text/plain"), et_add_line1.getText().toString());
        RequestBody addressLineTwo = RequestBody.create(MediaType.parse("text/plain"), et_add_line2.getText().toString());
        RequestBody zip = RequestBody.create(MediaType.parse("text/plain"), zipcode.getText().toString());
        RequestBody regtype = RequestBody.create(MediaType.parse("text/plain"), "" + regType);
        RequestBody bodycondition = RequestBody.create(MediaType.parse("text/plain"), et_body.getText().toString().trim());
        RequestBody lifestyle = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedLifeStyle));
        RequestBody learn_about = RequestBody.create(MediaType.parse("text/plain"), et_learn_about.getText().toString().trim());


        RequestBody country = RequestBody.create(MediaType.parse("text/plain"), selectedCountryId);

        RequestBody state = RequestBody.create(MediaType.parse("text/plain"), selectedStateId);

        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody TotalAmount = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody currencycode = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody PayableAmount = RequestBody.create(MediaType.parse("text/plain"), "");

        RequestBody scaleMacId = RequestBody.create(MediaType.parse("text/plain"), scaleId);
        RequestBody OrderId = RequestBody.create(MediaType.parse("text/plain"), orderId);

        String weight = et_weight.getText().toString().trim();
        String time = et_time_loss.getText().toString().trim();

        if (!weight.equalsIgnoreCase("")) {
            String[] weights = weight.split(" ");
            weight = weights[0];
            desiredWeight = RequestBody.create(MediaType.parse("text/plain"), weight);

        }

        if (time.equalsIgnoreCase("") || time.equalsIgnoreCase("TBD")) {
            time = "0";
            timeToloseWeight = RequestBody.create(MediaType.parse("text/plain"), time);

        } else {
            String[] splittedTime = time.split(" ");
            time = splittedTime[0].trim();
            timeToloseWeight = RequestBody.create(MediaType.parse("text/plain"), time);

        }


        if (selectedWeightManagmentGoal == 0) {
            type = RequestBody.create(MediaType.parse("text/plain"), "2");

            if (selectedDesiredWeightSelection == 0) {
                maintain_Weight_By_Server = RequestBody.create(MediaType.parse("text/plain"), "0");

            } else if (selectedDesiredWeightSelection == 1) {
                maintain_Weight_By_Server = RequestBody.create(MediaType.parse("text/plain"), "1");
                weight = "";
                time = "";
                desiredWeight = RequestBody.create(MediaType.parse("text/plain"), weight);
                timeToloseWeight = RequestBody.create(MediaType.parse("text/plain"), time);
            }
        } else {
            maintain_Weight_By_Server = RequestBody.create(MediaType.parse("text/plain"), "0");
            type = RequestBody.create(MediaType.parse("text/plain"), "1");

            weight = "";
            time = "";
            desiredWeight = RequestBody.create(MediaType.parse("text/plain"), weight);
            timeToloseWeight = RequestBody.create(MediaType.parse("text/plain"), time);
        }

        Call<SignUpResponse> call;

        if(mCompressedFile!=null)
            call= apiInterface.call_signup_image(first_name, middle_name, last_name,
                    email, password, gender, phone, dob, height, desiredWeight, timeToloseWeight, prefferedUnits, deviceType, type, deviceToken,
                    maintain_Weight_By_Server,count, regtype, state, city, zip,addressLineOne,addressLineTwo,bodycondition,lifestyle,country,
                    TotalAmount,currencycode,PayableAmount,address,scaleMacId,OrderId,learn_about,body);
        else
            call = apiInterface.call_signup(first_name, middle_name, last_name,
                    email, password, gender, phone, dob, height, desiredWeight, timeToloseWeight, prefferedUnits, deviceType, type, deviceToken,
                    maintain_Weight_By_Server,count, regtype, state, city, zip,addressLineOne,addressLineTwo,bodycondition,lifestyle,country,
                    TotalAmount,currencycode,PayableAmount,address,scaleMacId,OrderId,learn_about);


        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {

                    if (response.body().getStatus() == 1) {


                        if(response.body().getData().getUser().get(0).getScaleUserId().equals("")){

                            showInfoDialog(getString(R.string.signup_completed_without_scale));

                        }else{

                           showInfoDialog(getString(R.string.signup_completed_with_scale));

                        }

                    } else {

                        MethodUtils.errorMsg(SignUpView.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(SignUpView.this, getString(R.string.error_occurred));
                }

            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(SignUpView.this, getString(R.string.error_occurred));
            }
        });

    }

    public void showInfoDialog(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name_splash));
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> {
                    dialog.dismiss();

                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finishAffinity();
                });
        alertDialog.show();
    }

}
