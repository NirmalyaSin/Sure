package com.surefiz.screens.signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;

import com.bigkoo.pickerview.MyOptionsPickerView;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;

import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.bodycodition.BodyActivity;
import com.surefiz.screens.bodycodition.model.BodyItem;
import com.surefiz.screens.profile.model.country.CountryList;
import com.surefiz.screens.profile.model.country.Datum;
import com.surefiz.screens.profile.model.state.DataItem;
import com.surefiz.screens.profile.model.state.StateResponse;
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
        addSelectionListAndCall();
        addLearnAboutList();

        setTermsAndCondition();

        callCountryListApi();

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
        learnList.add("SureFiz Website");
        learnList.add("Others");


        learnPopup=new MyOptionsPickerView(this);
        learnPopup.setPicker(learnList);
        learnPopup.setCyclic(false);
        learnPopup.setSelectOptions(0);


        learnPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                et_learn_about.setText(learnList.get(options1));
            }
        });

    }

    private void addBodyList() {
        String [] stringList={"Diabetes","Depression","Heart Disease","High Blood Pressure","Osteoarthritis","High Cholesterol","None"};
        for (int i = 0; i <stringList.length ; i++) {
            BodyItem bodyItem=new BodyItem();
            bodyItem.setName(stringList[i]);
            bodyItem.setSelection(false);
            bodyList.add(bodyItem);
        }

    }
    private void addGenderListAndCall() {
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Non-binary");
        genderList.add("Prefer not to say");


        genderPopup=new MyOptionsPickerView(this);
        genderPopup.setPicker(genderList);
        genderPopup.setCyclic(false);
        genderPopup.setSelectOptions(0);


        genderPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                et_gender.setText(genderList.get(options1));
            }
        });
    }

    private void addLifeStyleListAndCall() {
        lifeStyleList.add("Sedentary");
        lifeStyleList.add("Lightly active");
        lifeStyleList.add("Moderately active");
        lifeStyleList.add("Very active");
        lifeStyleList.add("Extra active");

        lifeStylePopup=new MyOptionsPickerView(this);
        lifeStylePopup.setPicker(lifeStyleList);
        lifeStylePopup.setCyclic(false);
        lifeStylePopup.setSelectOptions(0);


        lifeStylePopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                et_lifestyle.setText(lifeStyleList.get(options1));
                selectedLifeStyle = options1 + 1;
            }
        });
    }

    private void addPreferredListAndCall() {

        prefferedList.add("LBS/INCH");
        prefferedList.add("KG/CM");

        weigtUniversalPopupPreferred=new MyOptionsPickerView(this);
        weigtUniversalPopupPreferred.setPicker(prefferedList);
        weigtUniversalPopupPreferred.setCyclic(false);
        weigtUniversalPopupPreferred.setSelectOptions(0);


        weigtUniversalPopupPreferred.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                height = et_height.getText().toString().trim();
                String value=prefferedList.get(options1);
                et_units.setText(value);

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


        countryListPopup=new MyOptionsPickerView(this);
        countryListPopup.setPicker(countryList);
        countryListPopup.setCyclic(false);
        countryListPopup.setSelectOptions(0);


        countryListPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                et_country_name.setText(countryList.get(options1));
                selectedCountryId=countryIDList.get(options1).toString() ;
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

        stateListPopup=new MyOptionsPickerView(this);
        stateListPopup.setPicker(stateList);
        stateListPopup.setCyclic(false);
        stateListPopup.setSelectOptions(0);


        stateListPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                et_state.setText(stateList.get(options1));
                selectedStateId=stateIDList.get(options1);
            }
        });

    }


    private void addManagementListAndCall() {

        managementList.add("Lose And Maintain Weight");
        managementList.add("Maintain Current Weight");

        managementPopup=new MyOptionsPickerView(this);
        managementPopup.setPicker(managementList);
        managementPopup.setCyclic(false);
        managementPopup.setSelectOptions(0);


        managementPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                if (managementList.get(options1).equals("Lose And Maintain Weight")) {
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
        desiredWeightSelectionList.add("I Want " + getResources().getString(R.string.app_name_splash) + " To Suggest");

        selectionPopup=new MyOptionsPickerView(this);
        selectionPopup.setPicker(desiredWeightSelectionList);
        selectionPopup.setCyclic(false);
        selectionPopup.setSelectOptions(0);


        selectionPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                if (options1==0) {
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
        for (int i = 1; i <=30; i++) {
            timeList.add(i + " " + "Weeks");
        }

        timePopup=new MyOptionsPickerView(this);
        timePopup.setPicker(timeList);
        timePopup.setCyclic(false);
        timePopup.setSelectOptions(0);


        timePopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                et_time_loss.setText(timeList.get(options1));
            }
        });
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

        weightPopup=new MyOptionsPickerView(this);
        weightPopup.setPicker(weightList);
        weightPopup.setCyclic(false);
        weightPopup.setSelectOptions(0);


        weightPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                et_weight.setText(weightList.get(options1));
            }
        });

    }

    protected void addHeightListAndCall(String change) {

        setupPickerLogic(change);


        doublePicker=new MyOptionsPickerView(this);
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
            et_height.setText(result+" INCH");

        }else{

            int result=v1*100+v2;
            et_height.setText(result+" CM");
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


    //***************************************************POP UP DISPLAY**************************************************


    protected void showAndDismissSelectionPopup() {

        selectionPopup.show();

    }

    protected void showTimePopup() {

        timePopup.show();

    }

    protected void showWeightPopup() {

        weightPopup.show();

    }
    protected void showBodyPopup() {


        Intent intent=new Intent(this, BodyActivity.class);
        intent.putExtra("selectedBody",bodyList);
        startActivityForResult(intent,101);

    }

    protected void showAndDismissCountryPopup() {

        countryListPopup.show();

    }

    protected void showAndDismissStatePopup() {
        if(stateListPopup!=null)
            stateListPopup.show();
    }

    protected void showAndDismissLifeStylePopup() {

       lifeStylePopup.show();
    }



    protected void showAndDismissGenderPopup() {

        genderPopup.show();

    }

    protected void showAndDismissPreferredPopup() {
        weigtUniversalPopupPreferred.show();

    }


    protected void showAndDismissHeightPopup() {
        doublePicker.show();
    }

    protected void showAndDismissManagementPopup() {
        managementPopup.show();

    }

    protected void showAndDismissLearAboutPopup() {
        learnPopup.show();
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
