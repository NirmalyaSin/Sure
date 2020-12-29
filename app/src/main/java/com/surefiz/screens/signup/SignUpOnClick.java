package com.surefiz.screens.signup;

import android.Manifest;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.dialog.OpenCameraOrGalleryDialog;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.screens.profile.ProfileActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;

import java.util.ArrayList;

public class SignUpOnClick implements View.OnClickListener {

    protected SignUpActivity signUpActivity;


    public SignUpOnClick(SignUpActivity signUpActivity) {
        this.signUpActivity=signUpActivity;
        setOnClick();
    }

    private void setOnClick(){
        signUpActivity.rl_back.setOnClickListener(this);
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
        signUpActivity.et_userselection.setOnClickListener(this);
        signUpActivity.et_learn_about.setOnClickListener(this);
        signUpActivity.btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rl_back:
                signUpActivity.finish();
                break;
            case R.id.profile_image:
                signUpActivity.imagePicker();
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

            case R.id.et_userselection:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                signUpActivity.showAndDismissSelectionPopup();
                break;

            case R.id.et_learn_about:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                signUpActivity.showAndDismissLearAboutPopup();
                break;

            case R.id.btn_register:

                signUpActivity.hideSoftKeyBoard();
                closeAllPopup();

                if (signUpActivity.et_first_name.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please enter your first name");
                }
                else if (signUpActivity.et_last_name.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please enter your last name");
                }
                else if (signUpActivity.et_email.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please enter your email");
                }
                else if (signUpActivity.et_confirm_email.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please re-enter your email");
                }else if (!signUpActivity.et_email.getText().toString().equals(signUpActivity.et_confirm_email.getText().toString())) {
                    MethodUtils.errorMsg(signUpActivity, "Re-enter email not match with email");
                }
                else if (signUpActivity.et_password.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Enter new password");
                }
                else if (signUpActivity.et_body.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select your pre existing condition");
                }
                else if (signUpActivity.selectedLifeStyle == 0) {
                    MethodUtils.errorMsg(signUpActivity, "Please select your lifestyle");
                }
                else if (signUpActivity.et_phone.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please enter your phone number");
                }
                else if (signUpActivity.et_units.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select your Preferred Units");
                }
                else if (signUpActivity.et_management.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select your weight management goal");
                }
                else if (signUpActivity.selectedWeightManagmentGoal==0 && signUpActivity.et_userselection.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select desired weight selection option");
                }else if (signUpActivity.selectedWeightManagmentGoal==0 && signUpActivity.selectedDesiredWeightSelection==0 && signUpActivity.et_weight.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select your desired weight");
                }
                else if (signUpActivity.selectedWeightManagmentGoal==0 && signUpActivity.selectedDesiredWeightSelection==0 && signUpActivity.et_time_loss.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select your time to lose");
                }
                else if (signUpActivity.et_height.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select your height");
                }
                else if (signUpActivity.et_gender.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select your gender");
                }
                else if (signUpActivity.age.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please enter your age");
                }
                else if (!isNonZeroValue(signUpActivity.age.getText().toString().trim())) {
                    MethodUtils.errorMsg(signUpActivity, "Age should be between 7 and 99");
                }

                else if (signUpActivity.et_country_name.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select your country");
                }
                else if (signUpActivity.et_add_line1.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Enter address line 1");
                }
                else if (signUpActivity.et_city.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please enter your city");
                }
                else if (signUpActivity.et_state.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select your state");
                }
                else if (signUpActivity.zipcode.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please enter your zip code");
                }
                else if (signUpActivity.et_learn_about.getText().toString().equals("")) {
                    MethodUtils.errorMsg(signUpActivity, "Please select how do you learn about");
                }
                else if (!signUpActivity.checkBoxTermsCondition.isChecked()) {
                    MethodUtils.errorMsg(signUpActivity, "Please accept Terms & Conditions");
                }
                else if (!ConnectionDetector.isConnectingToInternet(signUpActivity)) {
                    MethodUtils.errorMsg(signUpActivity, signUpActivity.getString(R.string.no_internet));
                } else {
                    signUpActivity.callSignUpApi();
                }

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
            signUpActivity.doublePicker.dismiss();
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
        else if (signUpActivity.selectionPopup != null && signUpActivity.selectionPopup.isShowing()) {
            signUpActivity.selectionPopup.dismiss();
        }
        else if (signUpActivity.managementPopup != null && signUpActivity.managementPopup.isShowing()) {
            signUpActivity.managementPopup.dismiss();
        }
        else if (signUpActivity.learnPopup != null && signUpActivity.learnPopup.isShowing()) {
            signUpActivity.learnPopup.dismiss();
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
}
