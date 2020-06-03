package com.surefiz.screens.signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.heightpopup.DoublePicker;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.dialog.weightpopup.WeigtUniversalPopup;
import com.surefiz.interfaces.OnWeightCallback;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.bodycodition.BodyActivity;
import com.surefiz.screens.bodycodition.model.BodyItem;
import com.surefiz.screens.profile.model.country.CountryList;
import com.surefiz.screens.profile.model.country.Datum;
import com.surefiz.screens.profile.model.state.DataItem;
import com.surefiz.screens.profile.model.state.StateResponse;
import com.surefiz.screens.weightManagement.WeightManagementActivity;
import com.surefiz.utils.progressloader.LoadingData;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends SignUpView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_layout);
        ButterKnife.bind(this);
        loader = new LoadingData(this);
        signUpOnClick=new SignUpOnClick(SignUpActivity.this);

        orderId=getIntent().getStringExtra("orderId");
        scaleId=getIntent().getStringExtra("scaleId");

        Log.e("SignUp-orderId",":::::::::::"+orderId);
        Log.e("SignUp-scaleId",":::::::::::"+scaleId);

        addPreferredListAndCall();
        addBodyList();
        addGenderListAndCall();
        addLifeStyleListAndCall();
        addManagementListAndCall();
        addTimeListAndCall();
        callCountryListApi();
        addSelectionListAndCall();
        addLearnAboutList();

        setTermsAndCondition();
    }


    private void setTermsAndCondition() {
        checkBoxTermsCondition.setText("");
        textTermsCondition.setText(Html.fromHtml("I have read and agree to the " +
                "<a href='com.surefiz.screens.termcondition.TermAndConditionActivity://Kode'><font color='#3981F5'>Terms & Conditions</font></a>"));
        textTermsCondition.setClickable(true);
        textTermsCondition.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void addLearnAboutList(){

        learnList.add("Google Search");
        learnList.add("Facebook");
        learnList.add("Instagram");
        learnList.add("Twitter");
        learnList.add("LinkedIn");
        learnList.add("YouTube");
        learnList.add("From a Friend/Relative");
        learnList.add("CES");
        learnList.add("News");
        learnList.add("Printed Media");
        learnList.add("SureFiz® Website");
        learnList.add("Others");

        learnPopup = new UniversalPopup(this, learnList, et_learn_about);

    }

    private void addBodyList() {
        String [] stringList={"Diabetes","Heart Disease","High Blood Pressure","Osteoarthritis","High Cholesterol","None"};
        for (int i = 0; i <stringList.length ; i++) {
            BodyItem bodyItem=new BodyItem();
            bodyItem.setName(stringList[i]);
            bodyItem.setSelection(false);
            bodyList.add(bodyItem);
        }

        //bodyPopup = new UniversalPopup(this, bodyList, et_body);
    }
    private void addGenderListAndCall() {
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Non-binary");
        genderList.add("Prefer not to say");

        genderPopup = new UniversalPopup(this, genderList, et_gender);
    }

    private void addLifeStyleListAndCall() {
        lifeStyleList.add("Sedentary");
        lifeStyleList.add("Lightly active");
        lifeStyleList.add("Moderately active");
        lifeStyleList.add("Very active");
        lifeStyleList.add("Extra active");

        lifeStylePopup = new WeigtUniversalPopup(this, lifeStyleList, et_lifestyle, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                et_lifestyle.setText(value);
                selectedLifeStyle = lifeStyleList.indexOf(value) + 1;
            }
        });
    }

    private void addPreferredListAndCall() {

        prefferedList.add("LBS/INCH");
        prefferedList.add("KG/CM");
        weigtUniversalPopupPreferred = new WeigtUniversalPopup(this, prefferedList, et_units, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                height = et_height.getText().toString().trim();

                if(!height.equals("")) {
                    splited = height.split(" ");
                    if (value.equals("KG/CM")) {
                        units = "CM";
                    } else {
                        units = "INCH";
                    }
                    if (units.equals(splited[1])) {
                    } else {
                        if (value.equals("KG/CM")) {
                            et_height.setText(Math.round(Double.parseDouble(splited[0]) * 2.54) + " CM");
                            units = "CM";
                        }
                        if (value.equals("LBS/INCH")) {
                            units = "INCH";
                            et_height.setText((Math.round(Double.parseDouble(splited[0]) * 0.393701)) + " INCH");
                        }
                    }
                }


                weight = et_weight.getText().toString().trim();

                if(!weight.equals("")) {
                    splited = weight.split(" ");
                    if (value.equals("KG/CM")) {
                        units = "CM";
                    } else {
                        units = "INCH";
                    }
                    if (units.equals(splited[1])) {
                    } else {
                        if (value.equals("KG/CM")) {
                            et_weight.setText(Math.round(Double.parseDouble(splited[0]) * 2.2046) + " KG");
                            units = "CM";
                        }
                        if (value.equals("LBS/INCH")) {
                            units = "INCH";
                            et_weight.setText((Math.round(Double.parseDouble(splited[0]) /2.2046)) + " LBS");
                        }
                    }
                }
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

    private void addPrefferedCountryList(List<Datum> data) {

        countryList.clear();
        countryIDList.clear();
        for (int i = 0; i < data.size(); i++) {
            countryList.add(data.get(i).getCountryName());
            countryIDList.add(data.get(i).getCountryID());
        }


        countryListPopup = new WeigtUniversalPopup(this, countryList, et_country_name, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                et_country_name.setText(value);

                int index = countryList.indexOf(et_country_name.getText().toString().trim());

                selectedCountryId=countryIDList.get(index).toString() ;
                callStateListApi();
            }
        });
    }


    private void addPrefferedStateList(List<DataItem> data) {

        stateList.clear();
        stateIDList.clear();
        for (int i = 0; i < data.size(); i++) {

            if (i == 0) {
                et_state.setText(data.get(i).getStateName());
                selectedStateId=data.get(i).getStateID();

            }

            stateList.add(data.get(i).getStateName());
            stateIDList.add(data.get(i).getStateID());
        }


        stateListPopup = new WeigtUniversalPopup(this, stateList, et_state, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {

                int index = stateList.indexOf(et_state.getText().toString().trim());

                selectedStateId=stateIDList.get(index);
                et_state.setText(value);
            }
        });
    }


    private void addManagementListAndCall() {

        managementList.add("Lose And Maintain Weight");
        managementList.add("Maintain Current Weight");

        managementPopup = new WeigtUniversalPopup(this, managementList, et_management, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                if (value.equals("Lose And Maintain Weight")) {
                    et_management.setText(managementList.get(0));

                    tv_userSelection.setVisibility(View.VISIBLE);
                    rl_userselection.setVisibility(View.VISIBLE);

                    tv_weight.setVisibility(View.GONE);
                    rl_weight.setVisibility(View.GONE);
                    tv_time_loss.setVisibility(View.GONE);
                    rl_time_loss.setVisibility(View.GONE);

                    selectedWeightManagmentGoal = 0;
                    selectedDesiredWeightSelection = -1;
                } else {
                    et_management.setText(managementList.get(1));

                    tv_userSelection.setVisibility(View.GONE);
                    rl_userselection.setVisibility(View.GONE);

                    tv_time_loss.setVisibility(View.GONE);
                    rl_time_loss.setVisibility(View.GONE);

                    tv_weight.setVisibility(View.GONE);
                    rl_weight.setVisibility(View.GONE);

                    selectedWeightManagmentGoal = 1;
                    selectedDesiredWeightSelection = -1;
                }
            }
        });
    }

    private void addSelectionListAndCall() {
        desiredWeightSelectionList.add("I Will Provide The Info");
        desiredWeightSelectionList.add("I want " + getResources().getString(R.string.app_name_splash) + " to suggest");

        selectionPopup = new WeigtUniversalPopup(this, desiredWeightSelectionList, et_userselection, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                if (value.equals("I Will Provide The Info")) {
                    et_userselection.setText(desiredWeightSelectionList.get(0));

                    if (selectedWeightManagmentGoal == 1) {
                        tv_weight.setVisibility(View.GONE);
                        rl_weight.setVisibility(View.GONE);
                        tv_time_loss.setVisibility(View.GONE);
                        rl_time_loss.setVisibility(View.GONE);
                    } else {
                        tv_weight.setVisibility(View.VISIBLE);
                        rl_weight.setVisibility(View.VISIBLE);
                        tv_time_loss.setVisibility(View.VISIBLE);
                        rl_time_loss.setVisibility(View.VISIBLE);
                        et_time_loss.setHint("Please Select");
                        et_time_loss.setText("");
                    }


                    selectedDesiredWeightSelection = 0;
                    et_time_loss.setEnabled(true);
                    et_weight.setEnabled(true);
                } else {
                    et_userselection.setText(desiredWeightSelectionList.get(1));

                    tv_weight.setVisibility(View.GONE);
                    rl_weight.setVisibility(View.GONE);
                    tv_time_loss.setVisibility(View.GONE);
                    rl_time_loss.setVisibility(View.GONE);

                    selectedDesiredWeightSelection = 1;
                }
            }
        });
    }

    private void addTimeListAndCall() {
        for (int i = 1; i <=52; i++) {
            timeList.add(i + " " + "Weeks");
        }
        timePopup = new UniversalPopup(this, timeList, et_time_loss);
    }

    protected void addWeightListAndCall(String change) {
        weightList.clear();
        if (change.equals("LBS")) {
            for (int i = 35; i <= 400; i++) {
                weightList.add(i + " " + change);
            }

        } else {
            for (int i = 20; i <= 180; i++) {
                weightList.add(i + " " + change);
            }
        }

        weightPopup = new UniversalPopup(this, weightList, et_weight);

    }




    //***************************************************POP UP DISPLAY**************************************************


    protected void showAndDismissSelectionPopup() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                selectionPopup.showAsDropDown(et_userselection);
            }
        }, 100);

    }

    protected void showTimePopup() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timePopup.showAsDropDown(et_time_loss);
            }
        }, 100);

    }

    protected void showWeightPopup() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                weightPopup.showAsDropDown(et_weight);
            }
        }, 100);

    }
    protected void showBodyPopup() {

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bodyPopup.showAsDropDown(et_body);
            }
        }, 100);*/

        Intent intent=new Intent(this, BodyActivity.class);
        intent.putExtra("selectedBody",bodyList);
        startActivityForResult(intent,101);

    }

    protected void showAndDismissCountryPopup() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (stateListPopup != null && stateListPopup.isShowing())
                    stateListPopup.dismiss();

                countryListPopup.showAsDropDown(et_country_name);
            }
        }, 100);

    }

    protected void showAndDismissStatePopup() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stateListPopup.showAsDropDown(et_state);
            }
        }, 100);

    }

    protected void showAndDismissLifeStylePopup() {
        if (lifeStylePopup.isShowing()) {
            lifeStylePopup.dismiss();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lifeStylePopup.showAsDropDown(et_lifestyle);
                }
            }, 100);
        }
    }

    protected void addHeightListAndCall(String change) {

        doublePicker=new DoublePicker(this,et_height,change);

    }

    protected void showAndDismissGenderPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                genderPopup.showAsDropDown(et_gender);
            }
        }, 100);
    }

    protected void showAndDismissPreferredPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                weigtUniversalPopupPreferred.showAsDropDown(et_units);

            }
        }, 100);
    }


    protected void showAndDismissHeightPopup() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doublePicker.Show();
            }
        }, 100);

    }

    protected void showAndDismissManagementPopup() {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    managementPopup.showAsDropDown(et_management);
                }
            }, 100);
        }

    protected void showAndDismissLearAboutPopup() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                learnPopup.showAsDropDown(et_learn_about);
            }
        }, 100);
    }

    protected String getSelectedItem(){
        String s="";
        for (int i = 0;i<bodyList.size();i++){
            if(bodyList.get(i).isSelection()==true){
                s=s+bodyList.get(i).getName()+",";
            }
        }

        if(!s.equals(""))
            s=s.substring(0,s.length()-1);

        return s;
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
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                bodyList= (ArrayList<BodyItem>) data.getSerializableExtra("selectedBody");

                String selectedBody=getSelectedItem();
                et_body.setText(selectedBody);
                Log.d("Selected Body","::::::::::"+selectedBody);
            }
        }
    }
}
