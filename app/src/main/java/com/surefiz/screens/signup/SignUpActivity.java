package com.surefiz.screens.signup;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.heightpopup.DoublePicker;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.dialog.weightpopup.WeigtUniversalPopup;
import com.surefiz.interfaces.OnWeightCallback;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.profile.model.country.CountryList;
import com.surefiz.screens.profile.model.country.Datum;
import com.surefiz.screens.profile.model.state.DataItem;
import com.surefiz.screens.profile.model.state.StateResponse;
import com.surefiz.screens.weightManagement.WeightManagementActivity;
import com.surefiz.utils.progressloader.LoadingData;

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

        addPreferredListAndCall();
        addBodyList();
        addGenderListAndCall();
        addLifeStyleListAndCall();
        addManagementListAndCall();
        addTimeListAndCall();
        callCountryListApi();
        callStateListApi();
    }

    private void addBodyList() {
        bodyList.add("Diabetes");
        bodyList.add("Heart Disease");
        bodyList.add("High Blood Pressure");
        bodyList.add("Osteoarthritis");
        bodyList.add("High Cholesterol");
        bodyList.add("None");

        bodyPopup = new UniversalPopup(this, bodyList, et_body);
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

            if(isState) {
                if (i == 0) {
                    et_state.setText(data.get(i).getStateName());
                }
            }
            isState = true;

            stateList.add(data.get(i).getStateName());
            stateIDList.add(data.get(i).getStateID());
        }


        stateListPopup = new WeigtUniversalPopup(this, stateList, et_state, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
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

                    findViewById(R.id.tv_weight).setVisibility(View.GONE);
                    findViewById(R.id.rl_weight).setVisibility(View.GONE);
                    findViewById(R.id.tv_time_loss).setVisibility(View.GONE);
                    findViewById(R.id.rl_time_loss).setVisibility(View.GONE);

                    selectedWeightManagmentGoal = 0;
                    selectedDesiredWeightSelection = -1;
                } else {
                    et_management.setText(managementList.get(1));


                    findViewById(R.id.tv_time_loss).setVisibility(View.GONE);
                    findViewById(R.id.rl_time_loss).setVisibility(View.GONE);

                    findViewById(R.id.tv_weight).setVisibility(View.GONE);
                    findViewById(R.id.rl_weight).setVisibility(View.GONE);

                    selectedWeightManagmentGoal = 1;
                    selectedDesiredWeightSelection = -1;
                }
            }
        });
    }

    private void addTimeListAndCall() {
        for (int i = 1; i < 261; i++) {
            timeList.add(i + " " + "Weeks");
        }
        timePopup = new UniversalPopup(this, timeList, et_time_loss);
    }

    protected void addWeightListAndCall(String change) {
        weightList.clear();
        if (change.equals("LBS")) {
            for (int i = 5; i < 401; i++) {
                weightList.add(i + " " + change);
            }

        } else {
            for (int i = 5; i < 181; i++) {
                weightList.add(i + " " + change);
            }
        }

        weightPopup = new UniversalPopup(this, weightList, et_weight);

    }


    //***************************************************POP UP DISPLAY**************************************************

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bodyPopup.showAsDropDown(et_body);
            }
        }, 100);

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

}
