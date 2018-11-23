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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.surefiz.R;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.registration.RegistrationActivity;
import com.surefiz.utils.MediaUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private File mFile = null;
    private Uri fileUri = null;
    public File mCompressedFile = null;
    private OnImageSet onImageSet;
    public static final int CAMERA = 1, GALLERY = 2;
    public View view;
    ProfileClickEvent profileClickEvent;
    de.hdodenhof.circleimageview.CircleImageView profile_image;
    ImageView iv_plus_add_image;
    EditText et_DOB;
    EditText et_phone;
    EditText et_full;
    EditText et_gender;
    EditText et_units;
    Button btn_register;

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
        btn_add.setVisibility(View.GONE);
    }

    private void showViewMode() {
        iv_plus_add_image.setVisibility(View.GONE);
        btn_register.setVisibility(View.GONE);
        et_phone.setEnabled(false);
        et_full.setEnabled(false);
        et_gender.setEnabled(false);
        et_DOB.setEnabled(false);
        et_units.setEnabled(false);
    }

    private void initView() {
        profile_image = view.findViewById(R.id.profile_image);
        iv_plus_add_image = view.findViewById(R.id.iv_plus_add_image);
        et_DOB = view.findViewById(R.id.et_DOB);
        et_phone = view.findViewById(R.id.et_phone);
        et_full = view.findViewById(R.id.et_full);
        et_gender = view.findViewById(R.id.et_gender);
        et_units = view.findViewById(R.id.et_units);
        btn_register = view.findViewById(R.id.btn_register);
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
                        Toast.makeText(ProfileActivity.this, "", Toast.LENGTH_SHORT).show();
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
        mFile = MediaUtils.getOutputMediaFile(ProfileActivity.this, MediaUtils.MEDIA_TYPE_IMAGE, FOLDER_NAME, fileName);
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
            photoURI = FileProvider.getUriForFile(ProfileActivity.this, getApplicationContext().getPackageName() +
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
}
