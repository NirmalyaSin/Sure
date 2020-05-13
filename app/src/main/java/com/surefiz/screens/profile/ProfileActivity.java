package com.surefiz.screens.profile;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.surefiz.R;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.screens.bodycodition.model.BodyItem;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.utils.MediaUtils;
import com.surefiz.utils.RealPathUtil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.os.Build.VERSION_CODES.M;

public class ProfileActivity extends BaseActivity {
    public static final String PICTURE_NAME = "SureFIZProfile";
    public static final String FOLDER_NAME = "SureFIZ";
    public static final int CAMERA = 1, GALLERY = 2;
    public File mCompressedFile = null;
    public View view;
    public CallbackManager fbcallbackManager;
    ProfileClickEvent profileClickEvent;
    de.hdodenhof.circleimageview.CircleImageView profile_image;
    ImageView iv_plus_add_image;
    ScrollView sv_main;
    EditText et_DOB;
    EditText et_phone;
    EditText et_full;
    EditText et_middle;
    EditText et_last;
    EditText et_gender;
    EditText et_units;
    EditText et_email;
    EditText et_height;
    EditText et_lifestyle;
    EditText et_body;
    EditText et_country_name;
    EditText et_add_line1;
    EditText et_add_line2;
    EditText et_city;
    EditText et_state;
    EditText et_zipcode;
    Button btn_register;
    Button btn_cancel;
    Switch switch_visibility;
    RelativeLayout rl_visibility;
    TextView btnGoogleAdd;
    TextView btnFacebookAdd;
    LinearLayout ll_add_new_password;
    EditText et_new_password;
    EditText et_confirm_password;
    TextView tvUserImageHint;
    private File mFile = null;
    private Uri fileUri = null;
    private OnImageSet onImageSet;

     TextView tv_zip_code;
     RelativeLayout rl_zip_code;
     TextView tv_state;
     RelativeLayout rl_state;
     TextView tv_city;
     RelativeLayout rl_city;
     TextView tv_address_line2;
     RelativeLayout rl_addressLine2;
     TextView tv_address_line;
     RelativeLayout rl_addressLine1;
     TextView tv_country_name;
     RelativeLayout rl_countryName;
     RelativeLayout rl_body;

    public  ArrayList<BodyItem> bodyList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        view = View.inflate(this, R.layout.activity_profile, null);
        addContentView(view);
        initView();
        setHeaderView();
        showViewMode();
        profileClickEvent = new ProfileClickEvent(this);
    }

    private void setHeaderView() {
        tv_universal_header.setText("Profile");
        iv_edit.setVisibility(View.VISIBLE);
        findViewById(R.id.iv_weight_managment).setVisibility(View.VISIBLE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.VISIBLE);
    }

    public void showViewMode() {
        iv_plus_add_image.setVisibility(View.GONE);
        tvUserImageHint.setVisibility(View.GONE);
        btn_register.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.GONE);
        et_phone.setEnabled(false);
        et_full.setEnabled(false);
        et_middle.setEnabled(false);
        et_last.setEnabled(false);
        et_gender.setEnabled(false);
        et_DOB.setEnabled(false);
        et_units.setEnabled(false);
        et_email.setEnabled(false);
        et_height.setEnabled(false);
        et_lifestyle.setEnabled(false);

        et_body.setEnabled(false);
        et_country_name.setEnabled(false);
        et_state.setEnabled(false);
        et_add_line1.setEnabled(false);
        et_add_line2.setEnabled(false);
        et_city.setEnabled(false);
        et_zipcode.setEnabled(false);

        profile_image.setEnabled(false);
        switch_visibility.setEnabled(false);
        et_new_password.setEnabled(false);
        et_confirm_password.setEnabled(false);
    }

    private void initView() {
        sv_main = view.findViewById(R.id.sv_main);
        profile_image = view.findViewById(R.id.profile_image);
        iv_plus_add_image = view.findViewById(R.id.iv_plus_add_image);
        tvUserImageHint = view.findViewById(R.id.tvUserImageHint);
        et_DOB = view.findViewById(R.id.et_DOB);
        et_phone = view.findViewById(R.id.et_phone);
        et_full = view.findViewById(R.id.et_full);
        et_middle = view.findViewById(R.id.et_middle);
        et_last = view.findViewById(R.id.et_last);
        et_gender = view.findViewById(R.id.et_gender);
        et_units = view.findViewById(R.id.et_units);
        et_email = view.findViewById(R.id.et_email);
        et_height = view.findViewById(R.id.et_height);
        et_lifestyle = view.findViewById(R.id.et_lifestyle);
        et_country_name = view.findViewById(R.id.et_country_name);
        et_add_line1 = view.findViewById(R.id.et_add_line1);
        et_add_line2 = view.findViewById(R.id.et_add_line2);
        et_city = view.findViewById(R.id.et_city);
        et_state = view.findViewById(R.id.et_state);
        et_body = view.findViewById(R.id.et_body);
        et_zipcode = view.findViewById(R.id.et_zipcode);

        btn_register = view.findViewById(R.id.btn_register);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        switch_visibility = view.findViewById(R.id.switch_visibility);
        rl_visibility = view.findViewById(R.id.rl_visibility);
        btnGoogleAdd = view.findViewById(R.id.btnGoogleAdd);
        btnFacebookAdd = view.findViewById(R.id.btnFacebookAdd);
        ll_add_new_password = view.findViewById(R.id.ll_add_new_password);
        ll_add_new_password.setVisibility(View.GONE);
        et_new_password = view.findViewById(R.id.et_new_password);
        et_confirm_password = view.findViewById(R.id.et_confirm_password);

        tv_zip_code = view.findViewById(R.id.tv_zip_code);
        rl_zip_code = view.findViewById(R.id.rl_zip_code);

        tv_state = view.findViewById(R.id.tv_state);
        rl_state = view.findViewById(R.id.rl_state);

        tv_city = view.findViewById(R.id.tv_city);
        rl_city = view.findViewById(R.id.rl_city);

        tv_address_line2 = view.findViewById(R.id.tv_address_line2);
        rl_addressLine2 = view.findViewById(R.id.rl_addressLine2);

        tv_address_line = view.findViewById(R.id.tv_address_line);
        rl_addressLine1 = view.findViewById(R.id.rl_addressLine1);

        tv_country_name = view.findViewById(R.id.tv_country_name);
        rl_countryName = view.findViewById(R.id.rl_countryName);

        rl_body = view.findViewById(R.id.rl_body);
    }

    protected void imagePicker(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1, 1)
                .start(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        // if the result is capturing Image

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

                break;

            case ProfileClickEvent.RC_SIGN_IN_GOOGLE:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                profileClickEvent.handleSignInResult(result);

                break;

            case ProfileClickEvent.BODY_CONDITION:
                if(resultCode==RESULT_OK){
                    bodyList= (ArrayList<BodyItem>) data.getSerializableExtra("selectedBody");

                    String selectedBody=getSelectedItem();
                    et_body.setText(selectedBody);
                    Log.d("Selected Body","::::::::::"+selectedBody);

                }
                break;
            default:
                if (fbcallbackManager != null) {
                    fbcallbackManager.onActivityResult(requestCode, resultCode, data);
                }
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
                        //tvUserImageHint.setVisibility(View.VISIBLE);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(ProfileActivity.this, getResources().getString(R.string.image_compression_error_text), Toast.LENGTH_SHORT).show();
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


    public void callFacebooklogin() {
        if (AccessToken.getCurrentAccessToken() != null) {
            requestData();
            return;
        }
        fbcallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(ProfileActivity.this, Arrays.asList("email,public_profile"));
        //LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(fbcallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        requestData();
                        loginResult.getAccessToken();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println();
                        exception.printStackTrace();
                    }
                });
    }


    private void requestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();

                parseFacebookJsonAndAPiCall(json, AccessToken.getCurrentAccessToken());

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture.width(750).height(750)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void parseFacebookJsonAndAPiCall(final JSONObject jsonObjectFbResult, final AccessToken currentAccessToken) {
        if (jsonObjectFbResult == null)
            return;

        final String socialEmail = jsonObjectFbResult.optString("email");
        String socialId = jsonObjectFbResult.optString("id");
        String socialName = jsonObjectFbResult.optString("name");


        System.out.println("facebookData: " + socialId + "," + socialName);

        profileClickEvent.callapiforAddSocail(socialId, "fb");
    }

    protected String getSelectedItem(){
        String s="";
        for (int i = 0;i<bodyList.size();i++){
            if(bodyList.get(i).isSelection()==true){
                s=s+bodyList.get(i).getName()+", ";
            }
        }

        return s;
    }

}