package com.surefiz.screens.signup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.surefiz.R;
import com.surefiz.dialog.heightpopup.DoublePicker;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.dialog.weightpopup.WeigtUniversalPopup;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.screens.profile.ProfileActivity;
import com.surefiz.screens.profile.ProfileClickEvent;
import com.surefiz.screens.registration.RegistrationClickEvent;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.utils.MediaUtils;
import com.surefiz.utils.progressloader.LoadingData;

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
    int regType = -1;
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
    protected EditText city;
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

    protected String toolTipText = "";
    protected File mFile = null;
    protected String filePath = "";
    protected Uri fileUri = null;
    protected OnImageSet onImageSet;
    protected LoadingData loader;
    protected ImageLoader imageLoader;

    String units = "", height = "",weight = "";
    String[] splited;
    protected List<String> genderList = new ArrayList<>();
    protected List<String> lifeStyleList = new ArrayList<>();
    protected int month, year, day;
    protected List<String> prefferedList = new ArrayList<>();
    protected List<String> countryList = new ArrayList<>();
    protected List<Integer> countryIDList = new ArrayList<>();
    protected List<String> stateList = new ArrayList<>();
    protected List<String> stateIDList = new ArrayList<>();
    protected List<String> timeList = new ArrayList<>();
    protected List<String> weightList = new ArrayList<>();
    protected List<String> managementList = new ArrayList<>();
    protected List<String> desiredWeightSelectionList = new ArrayList<>();
    protected List<String> bodyList = new ArrayList<>();
    protected String weight_value = "", time_value = "", units_value = "";
    protected DoublePicker doublePicker;
    protected UniversalPopup bodyPopup,genderPopup,weightPopup,timePopup;
    protected WeigtUniversalPopup managementPopup, selectionPopup;
    protected WeigtUniversalPopup countryListPopup, lifeStylePopup,stateListPopup,weigtUniversalPopupPreferred;
    protected int selectedLifeStyle = 0;
    protected String selectedCountryId = "";
    protected boolean isState=false;
    protected int selectedWeightManagmentGoal = 0;
    protected int selectedDesiredWeightSelection = 0;

    protected SignUpOnClick signUpOnClick;


    protected void hideSoftKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
                        Toast.makeText(SignUpView.this, "", Toast.LENGTH_SHORT).show();
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
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
    }

    private void openCamera() {
        generateFile();
        captureImage();
    }

    private void generateFile() {
        String fileName = PICTURE_NAME + new SimpleDateFormat("mm_dd_yyyy_HH_mm_ss").format(new Date());
        mFile = MediaUtils.getOutputMediaFile(SignUpView.this, MediaUtils.MEDIA_TYPE_IMAGE, FOLDER_NAME, fileName);
    }

    /*
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri();

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAMERA);
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri() {
        Uri photoURI;

        if (Build.VERSION.SDK_INT > M) {
            photoURI = FileProvider.getUriForFile(SignUpView.this, getApplicationContext().getPackageName() +
                    ".provider", mFile);
        } else {
            photoURI = Uri.fromFile(mFile);
        }
        return photoURI;
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


}
