package com.surefiz.screens.signup;

import android.Manifest;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.surefiz.R;
import com.surefiz.dialog.OpenCameraOrGalleryDialog;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.screens.profile.ProfileActivity;
import com.surefiz.utils.MethodUtils;

import java.util.ArrayList;

public class SignUpOnClick implements View.OnClickListener {

    protected SignUpActivity signUpActivity;


    public SignUpOnClick(SignUpActivity signUpActivity) {
        this.signUpActivity=signUpActivity;
        setOnClick();
    }

    private void setOnClick(){
        signUpActivity.profile_image.setOnClickListener(this);
        signUpActivity.et_gender.setOnClickListener(this);
        signUpActivity.et_units.setOnClickListener(this);
        signUpActivity.et_height.setOnClickListener(this);
        signUpActivity.btn_register.setOnClickListener(this);
        signUpActivity.et_country_name.setOnClickListener(this);
        signUpActivity.et_state.setOnClickListener(this);
        signUpActivity.et_lifestyle.setOnClickListener(this);
        signUpActivity.et_body.setOnClickListener(this);
        signUpActivity.et_management.setOnClickListener(this);
        signUpActivity.et_weight.setOnClickListener(this);
        signUpActivity.et_time_loss.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.profile_image:
                new OpenCameraOrGalleryDialog(signUpActivity, new OnImageSet() {
                    @Override
                    public void onSuccess(String path) {
                        signUpActivity.filePath = path;
                    }
                }, "2").show();
                break;

            case R.id.et_body:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                signUpActivity.showBodyPopup();
                break;
            case R.id.et_country_name:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                signUpActivity.showAndDismissCountryPopup();
                break;
            case R.id.et_state:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                if(signUpActivity.et_country_name.getText().toString().equals("")){
                    MethodUtils.errorMsg(signUpActivity, "Please select your country first");
                }else {
                    signUpActivity.showAndDismissStatePopup();
                }

                break;
            case R.id.et_lifestyle:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                signUpActivity.showAndDismissLifeStylePopup();
                break;

            case R.id.et_gender:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                signUpActivity.showAndDismissGenderPopup();

                break;
            case R.id.et_units:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                signUpActivity.showAndDismissPreferredPopup();
                break;

            case R.id.et_height:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                if(signUpActivity.et_units.getText().toString().equals("")){
                    MethodUtils.errorMsg(signUpActivity, "Please select your preferred unit");
                }else {
                    if (signUpActivity.et_units.getText().toString().equals("KG/CM")) {
                        signUpActivity.addHeightListAndCall("CM");
                    } else {
                        signUpActivity.addHeightListAndCall("INCH");
                    }
                    signUpActivity.showAndDismissHeightPopup();
                }

                break;

            case R.id.et_weight:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                if(signUpActivity.et_units.getText().toString().equals("")){
                    MethodUtils.errorMsg(signUpActivity, "Please select your preferred unit");
                }else {
                    if (signUpActivity.et_units.getText().toString().equals("KG/CM")) {
                        signUpActivity.addWeightListAndCall("KG");
                    } else {
                        signUpActivity.addWeightListAndCall("LBS");
                    }
                    signUpActivity.showWeightPopup();
                }

                break;

            case R.id.et_management:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                signUpActivity.showAndDismissManagementPopup();
                break;

            case R.id.et_time_loss:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                signUpActivity.showTimePopup();
                break;
        }

    }
    private void closeAllPopup(){

        if (signUpActivity.weigtUniversalPopupPreferred != null && signUpActivity.weigtUniversalPopupPreferred.isShowing()) {
            signUpActivity.weigtUniversalPopupPreferred.dismiss();
        }
        else if (signUpActivity.genderPopup != null && signUpActivity.genderPopup.isShowing()) {
            signUpActivity.genderPopup.dismiss();
        }
        else if (signUpActivity.doublePicker != null && signUpActivity.doublePicker.isShowing()) {
            signUpActivity.doublePicker.Dismiss();
        }
        else if (signUpActivity.bodyPopup != null && signUpActivity.bodyPopup.isShowing()) {
            signUpActivity.bodyPopup.dismiss();
        }
        else if (signUpActivity.countryListPopup != null && signUpActivity.countryListPopup.isShowing()) {
            signUpActivity.countryListPopup.dismiss();
        }
        else if (signUpActivity.lifeStylePopup != null && signUpActivity.lifeStylePopup.isShowing()) {
            signUpActivity.lifeStylePopup.dismiss();
        }
        else if (signUpActivity.stateListPopup != null && signUpActivity.stateListPopup.isShowing()) {
            signUpActivity.stateListPopup.dismiss();
        }
        else if (signUpActivity.weightPopup != null && signUpActivity.weightPopup.isShowing()) {
            signUpActivity.weightPopup.dismiss();
        }
        else if (signUpActivity.timePopup != null && signUpActivity.timePopup.isShowing()) {
            signUpActivity.timePopup.dismiss();
        }
    }

}
