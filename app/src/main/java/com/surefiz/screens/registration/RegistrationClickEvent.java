package com.surefiz.screens.registration;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.OpenCameraOrGalleryDialog;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.dialog.weightpopup.WeigtUniversalPopup;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.interfaces.OnWeightCallback;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.familyinvite.FamilyInviteActivity;
import com.surefiz.screens.groupinvite.GroupInviteActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Part;

public class RegistrationClickEvent implements View.OnClickListener {
    RegistrationActivity registrationActivity;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    private List<String> genderList = new ArrayList<>();
    private List<String> prefferedList = new ArrayList<>();
    private List<String> heightList = new ArrayList<>();
    private List<String> memberList = new ArrayList<>();
    private List<String> weightList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private List<String> managementList = new ArrayList<>();
    private List<String> selectionList = new ArrayList<>();
    private int user_selection_val = 0;
    private int weight_managment_goal = 0;
    private String filePath = "", userselectiontext = "", weightmanagment = "";
    private LoadingData loader;
    private UniversalPopup genderPopup, prefferedPopup, heightPopup, weightPopup, timePopup, memberPopup;
    private WeigtUniversalPopup managementPopup, selectionPopup;

    public RegistrationClickEvent(RegistrationActivity registrationActivity) {
        this.registrationActivity = registrationActivity;
        loader = new LoadingData(registrationActivity);
        addGenderListAndCall();
        addPrefferedListAndCall();
        addManagementListAndCall();
        addSelectionListAndCall();
        addHeightListAndCall("INCH");
        addWeightListAndCall("LB");
        addMemberListAndCall();
        addTimeListAndCall();
        setClickEvent();
        registrationActivity.et_units.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                heightList.clear();
                weightList.clear();
                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }

                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addWeightListAndCall("KG");
                } else {
                    addWeightListAndCall("LB");
                }
            }
        });
    }

    private void addManagementListAndCall() {
        managementList.add("Lose and Mantain Weight");
        managementList.add("Maintain Current Weight");

        managementPopup = new WeigtUniversalPopup(registrationActivity, managementList, registrationActivity.et_management, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                if (value.equals("Lose and Mantain Weight")) {
                    registrationActivity.tv_userSelection.setVisibility(View.VISIBLE);
                    registrationActivity.rl_userselection.setVisibility(View.VISIBLE);
                    registrationActivity.tv_weight.setVisibility(View.VISIBLE);
                    registrationActivity.rl_weight.setVisibility(View.VISIBLE);
                    registrationActivity.tv_time_loss.setVisibility(View.VISIBLE);
                    registrationActivity.rl_time_loss.setVisibility(View.VISIBLE);
                    weight_managment_goal = 2;
                    user_selection_val = 0;
                } else {
                    registrationActivity.tv_userSelection.setVisibility(View.GONE);
                    registrationActivity.rl_userselection.setVisibility(View.GONE);
                    registrationActivity.tv_weight.setVisibility(View.GONE);
                    registrationActivity.rl_weight.setVisibility(View.GONE);
                    registrationActivity.tv_time_loss.setVisibility(View.GONE);
                    registrationActivity.rl_time_loss.setVisibility(View.GONE);
                    registrationActivity.et_userselection.setText("");
                    weight_managment_goal = 1;
                    user_selection_val = 0;
                }
            }
        });
    }

    private void addSelectionListAndCall() {
        selectionList.add("I will Provide the Info");
        selectionList.add("I want SureFiz™ to suggest");

        selectionPopup = new WeigtUniversalPopup(registrationActivity, selectionList, registrationActivity.et_userselection, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                if (value.equals("I will Provide the Info")) {
                    registrationActivity.tv_weight.setVisibility(View.VISIBLE);
                    registrationActivity.rl_weight.setVisibility(View.VISIBLE);
                    registrationActivity.tv_time_loss.setVisibility(View.VISIBLE);
                    registrationActivity.rl_time_loss.setVisibility(View.VISIBLE);
                    user_selection_val = 1;
                } else {
                    registrationActivity.tv_weight.setVisibility(View.GONE);
                    registrationActivity.rl_weight.setVisibility(View.GONE);
                    registrationActivity.tv_time_loss.setVisibility(View.GONE);
                    registrationActivity.rl_time_loss.setVisibility(View.GONE);
                    user_selection_val = 2;
                }
            }
        });
    }

    private void addTimeListAndCall() {
        for (int i = 1; i < 261; i++) {
            timeList.add(i + " " + "Weeks");
        }
        timePopup = new UniversalPopup(registrationActivity, timeList, registrationActivity.et_time_loss);
    }

    private void addHeightListAndCall(String change) {
        heightList.clear();
        if (change.equals("INCH")) {
            for (int i = 1; i < 109; i++) {
                heightList.add(i + " " + change);
            }
        } else {
            for (int i = 1; i < 276; i++) {
                heightList.add(i + " " + change);
            }
        }
        heightPopup = new UniversalPopup(registrationActivity, heightList, registrationActivity.et_height);
    }

    private void addMemberListAndCall() {
        memberList.clear();
        for (int i = 0; i <= 4; i++) {
            memberList.add("" + i);
        }
        memberPopup = new UniversalPopup(registrationActivity, memberList, registrationActivity.et_member);
    }

    private void addWeightListAndCall(String change) {
        weightList.clear();
        if (change.equals("LB")) {
            for (int i = 5; i < 301; i++) {
                weightList.add(i + " " + change);
            }
        } else {
            for (int i = 5; i < 151; i++) {
                weightList.add(i + " " + change);
            }
        }
        weightPopup = new UniversalPopup(registrationActivity, weightList, registrationActivity.et_weight);
    }

    private void addPrefferedListAndCall() {
        prefferedList.add("LB/INCH");
        prefferedList.add("KG/CM");

        prefferedPopup = new UniversalPopup(registrationActivity, prefferedList, registrationActivity.et_units);
    }

    private void addGenderListAndCall() {
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Others");

        genderPopup = new UniversalPopup(registrationActivity, genderList, registrationActivity.et_gender);
    }

    private void setClickEvent() {
        registrationActivity.tv_upload.setOnClickListener(this);
        registrationActivity.et_gender.setOnClickListener(this);
        registrationActivity.et_units.setOnClickListener(this);
        registrationActivity.et_height.setOnClickListener(this);
        registrationActivity.et_weight.setOnClickListener(this);
        registrationActivity.et_time_loss.setOnClickListener(this);
        registrationActivity.profile_image.setOnClickListener(this);
        registrationActivity.iv_plus_add_image.setOnClickListener(this);
        registrationActivity.btn_register.setOnClickListener(this);
        registrationActivity.rl_back.setOnClickListener(this);
        registrationActivity.et_management.setOnClickListener(this);
        registrationActivity.et_userselection.setOnClickListener(this);
        registrationActivity.et_member.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_upload:
                break;
            case R.id.btn_register:
                Intent loginIntent = new Intent(registrationActivity, GroupInviteActivity.class);
                loginIntent.putExtra("count", Integer.parseInt(registrationActivity.et_member.getText().toString()));
                registrationActivity.startActivity(loginIntent);
                registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                registrationActivity.finish();
//                validationAndApiCall();
                break;
            case R.id.rl_back:
                registrationActivity.onBackPressed();
                break;
            case R.id.profile_image:
                new OpenCameraOrGalleryDialog(registrationActivity, new OnImageSet() {
                    @Override
                    public void onSuccess(String path) {
                        filePath = path;
                    }
                }, "0").show();
                break;
            case R.id.iv_plus_add_image:
                new OpenCameraOrGalleryDialog(registrationActivity, new OnImageSet() {
                    @Override
                    public void onSuccess(String path) {
                        filePath = path;
                    }
                }, "0").show();
                break;
            case R.id.et_gender:
                hideSoftKeyBoard();
                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }

                if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addWeightListAndCall("KG");
                } else {
                    addWeightListAndCall("LB");
                }
                if (prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissGenderPopup();
                        }
                    }, 100);
                }
                break;
            case R.id.et_units:
                hideSoftKeyBoard();
                if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissPrefferedPopup();
                        }
                    }, 100);
                }

                registrationActivity.et_weight.setText("");
                registrationActivity.et_height.setText("");
                registrationActivity.et_time_loss.setText("");

                break;
            case R.id.et_height:
                hideSoftKeyBoard();
//                heightList.clear();
                /*if (registrationActivity.et_units.getText().toString().equals("KG/CM")) {
                    addHeightListAndCall("CM");
                } else {
                    addHeightListAndCall("INCH");
                }*/
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissHeightPopup();
                        }
                    }, 100);
                }
                break;
            case R.id.et_member:
                dismissDialogs();
                showAndDismissMemberPopup();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showAndDismissMemberPopup();
                    }
                }, 100);
                break;
            case R.id.et_weight:
                hideSoftKeyBoard();
//                weightList.clear();

                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissWeightPopup();
                        }
                    }, 100);
                }
                break;

            case R.id.et_management:
                hideSoftKeyBoard();
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {

                    registrationActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // if (!isFinishing()) {
                            //showCustomPopupforWeightmanagemnt();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // showAndDismissManagementPopup();
                                    //showCustomPopupforWeightmanagemnt();
                                    showAndDismissManagementPopup();
                                }
                            }, 100);
                        }
                        // }
                    });


                   /* new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           // showAndDismissManagementPopup();
                            showCustomPopupforWeightmanagemnt();
                        }
                    }, 100);*/
                }

                break;
            case R.id.et_time_loss:
                hideSoftKeyBoard();
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissTimePopup();
                        }
                    }, 100);
                }
                break;
            case R.id.et_userselection:
                hideSoftKeyBoard();
                if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else if (genderPopup != null && genderPopup.isShowing()) {
                    genderPopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (heightPopup != null && heightPopup.isShowing()) {
                    heightPopup.dismiss();
                } else if (managementPopup != null && managementPopup.isShowing()) {
                    managementPopup.dismiss();
                } else if (selectionPopup != null && selectionPopup.isShowing()) {
                    selectionPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissSelectionPopup();
                        }
                    }, 100);
                }
                break;
        }
    }

    private void dismissDialogs() {
        if (prefferedPopup != null && prefferedPopup.isShowing()) {
            prefferedPopup.dismiss();
        } else if (genderPopup != null && genderPopup.isShowing()) {
            genderPopup.dismiss();
        } else if (heightPopup != null && heightPopup.isShowing()) {
            heightPopup.dismiss();
        } else if (timePopup != null && timePopup.isShowing()) {
            timePopup.dismiss();
        } else if (weightPopup != null && weightPopup.isShowing()) {
            weightPopup.dismiss();
        } else if (managementPopup != null && managementPopup.isShowing()) {
            managementPopup.dismiss();
        } else if (selectionPopup != null && selectionPopup.isShowing()) {
            selectionPopup.dismiss();
        } else if (memberPopup != null && memberPopup.isShowing()) {
            memberPopup.dismiss();
        }
    }

    private void showCustomPopupforWeightmanagemnt() {
        String[] values = new String[]{"Lose and Maintain Weight", "Maintain Current Weight"};
        String[] userselection = new String[]{"Input by me", "Server will Create that"};

        Display display = registrationActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        EditText et_select_weight_managment, et_select_user;
        Button btn_weightsave;
        ListView list, lv_userselection;
        LinearLayout ll_user_selection;

        LayoutInflater layoutInflater = (LayoutInflater) registrationActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.custom_popup_for_weight_managment, null);
        PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btn_weightsave = customView.findViewById(R.id.btn_weightsave);
        et_select_weight_managment = customView.findViewById(R.id.et_select_weight_managment);
        list = customView.findViewById(R.id.weight_choice_list);
        ll_user_selection = customView.findViewById(R.id.ll_user_selection);
        lv_userselection = customView.findViewById(R.id.lv_userselection);
        et_select_user = customView.findViewById(R.id.et_select_user);
        // Assign adapter to ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(registrationActivity, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        list.setAdapter(adapter);
        ArrayAdapter<String> adapteruser = new ArrayAdapter<String>(registrationActivity, android.R.layout.simple_list_item_1, android.R.id.text1, userselection);
        lv_userselection.setAdapter(adapteruser);
        if (weight_managment_goal != 0) {
            et_select_weight_managment.setText(weightmanagment);
        }
        if (user_selection_val != 0) {
            ll_user_selection.setVisibility(View.VISIBLE);
            et_select_user.setText(userselectiontext);
        }

        list.setOnItemClickListener((parent, view, position, id) -> {
            if (values[position].equalsIgnoreCase("Maintain Current Weight")) {
                ll_user_selection.setVisibility(View.GONE);
                et_select_weight_managment.setText(values[position]);
                list.setVisibility(View.GONE);
                weightmanagment = values[position];
                weight_managment_goal = 1;
                user_selection_val = 1;
            } else {
                et_select_weight_managment.setText(values[position]);
                ll_user_selection.setVisibility(View.VISIBLE);
                list.setVisibility(View.GONE);
                weightmanagment = values[position];
                weight_managment_goal = 2;
            }
        });
        lv_userselection.setOnItemClickListener((parent, view, position, id) -> {
            et_select_user.setText(userselection[position]);
            lv_userselection.setVisibility(View.GONE);
            if (userselection[position].equalsIgnoreCase("Input by me")) {
                user_selection_val = 1;
                userselectiontext = userselection[position];
            } else {
                user_selection_val = 2;
                userselectiontext = userselection[position];
            }
        });

        et_select_weight_managment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lv_userselection.getVisibility() == View.VISIBLE)
                    lv_userselection.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
                //addManagementListAndCall();
                //  managementPopup.showAsDropDown(et_select_weight_managment);
            }
        });

        et_select_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.getVisibility() == View.VISIBLE)
                    list.setVisibility(View.GONE);
                lv_userselection.setVisibility(View.VISIBLE);
            }
        });
        btn_weightsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weight_managment_goal != 0) {
                    if (weight_managment_goal == 1) {
                        popupWindow.dismiss();
                        updatemainviewafterpopupselection();
                    } else if (user_selection_val != 0) {
                        popupWindow.dismiss();
                        updatemainviewafterpopupselection();
                    }
                }
            }
        });
        popupWindow.setWidth(width - 35);
        registrationActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // if (!isFinishing()) {
                popupWindow.showAtLocation(registrationActivity.rl_main_registration, Gravity.CENTER, 0, 0);
            }
            // }
        });

        // popupWindow.showAtLocation(registrationActivity.rl_main_registration, Gravity.CENTER, 0, 0);

    }

    private void updatemainviewafterpopupselection() {
        if (weight_managment_goal == 1) {
            registrationActivity.tv_weight.setVisibility(View.GONE);
            registrationActivity.et_weight.setVisibility(View.GONE);
            registrationActivity.tv_time_loss.setVisibility(View.GONE);
            registrationActivity.et_time_loss.setVisibility(View.GONE);

            registrationActivity.tv_userSelection.setVisibility(View.GONE);
            registrationActivity.rl_userselection.setVisibility(View.GONE);
            registrationActivity.et_userselection.setVisibility(View.GONE);

            registrationActivity.et_management.setText(weightmanagment);

        } else if (weight_managment_goal == 2) {
            registrationActivity.tv_userSelection.setVisibility(View.VISIBLE);
            registrationActivity.rl_userselection.setVisibility(View.VISIBLE);
            registrationActivity.et_userselection.setVisibility(View.VISIBLE);
            registrationActivity.et_userselection.setText(userselectiontext);
            registrationActivity.et_management.setText(weightmanagment);
            if (user_selection_val == 1) {
                registrationActivity.tv_weight.setVisibility(View.VISIBLE);
                registrationActivity.et_weight.setVisibility(View.VISIBLE);
                registrationActivity.tv_time_loss.setVisibility(View.VISIBLE);
                registrationActivity.et_time_loss.setVisibility(View.VISIBLE);
            } else {
                registrationActivity.tv_weight.setVisibility(View.GONE);
                registrationActivity.et_weight.setVisibility(View.GONE);
                registrationActivity.tv_time_loss.setVisibility(View.GONE);
                registrationActivity.et_time_loss.setVisibility(View.GONE);
            }
        }
    }


    private void validationAndApiCall() {
        if (registrationActivity.et_first_name.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your first name");
        } /*else if (registrationActivity.et_middle_name.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your middle name");
        }*/ else if (registrationActivity.et_last_name.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your last name");
        } else if (registrationActivity.et_email.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your email");
        } else if (!MethodUtils.isValidEmail(registrationActivity.et_email.getText().toString())) {
            MethodUtils.errorMsg(registrationActivity, "Please enter a valid email address");
        } else if (registrationActivity.et_password.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your password");
        } else if (registrationActivity.et_phone.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your phone number");
        } else if (registrationActivity.et_management.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please select Weight Managment goal");
        }
         /*else if (registrationActivity.et_userselection.getVisibility()==View.VISIBLE && registrationActivity.et_userselection.getText().toString().equals("")) {
          MethodUtils.errorMsg(registrationActivity, "Please select any option");
        }*/
        else if (registrationActivity.et_units.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please select your Preffered Units");
        } else if (registrationActivity.et_gender.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please select any gender type");
        } else if (registrationActivity.age.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your Age");
        } else if (registrationActivity.et_height.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your height");
        } else if (weight_managment_goal == 2 && registrationActivity.et_weight.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your desired weight");
        } else if (weight_managment_goal == 2 && registrationActivity.et_time_loss.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please select your time to lose weight");
        } else if (!lengthCheck(registrationActivity.et_password.getText().toString().trim())) {
            MethodUtils.errorMsg(registrationActivity, "Password must be more than 8 characters");
        } else if (registrationActivity.address.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your full address");
        } else if (registrationActivity.city.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your city name");
        } else if (registrationActivity.state.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your state");
        } else if (registrationActivity.zipcode.getText().toString().equals("")) {
            MethodUtils.errorMsg(registrationActivity, "Please enter your zip code");
        } else if (!ConnectionDetector.isConnectingToInternet(registrationActivity)) {
            MethodUtils.errorMsg(registrationActivity, registrationActivity.getString(R.string.no_internet));
        } else if (!registrationActivity.checkBoxTermsCondition.isChecked()) {
            MethodUtils.errorMsg(registrationActivity, "Please accept Terms & Condition.");
        } else {
            if (registrationActivity.regType > 1) {
                if (registrationActivity.et_member.getText().toString().isEmpty()) {
                    MethodUtils.errorMsg(registrationActivity, "Please enter number of members you want to add");
                } else {
                    if (registrationActivity.mCompressedFile != null) {
                        callRegistrationApiWithImage();
                    } else {
                        callRegistrationApi();
                    }
                }
            } else {
                if (registrationActivity.mCompressedFile != null) {
                    callRegistrationApiWithImage();
                } else {
                    callRegistrationApi();
                }
            }
        }
    }

    private boolean lengthCheck(final String password) {
        return password.length() >= 8 && password.length() <= 20;
    }

    private boolean lengthScale(final String scale) {
        return scale.length() >= 10;
    }

    private void callRegistrationApi() {
        loader.show_with_label("Loading");
        RequestBody gender = null;
        RequestBody units = null;
        RequestBody type = null;
        RequestBody userselectionbody = null;
        RequestBody weight = null;
        RequestBody time = null;
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_first_name.getText().toString().trim());
        RequestBody middle_name = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_middle_name.getText().toString().trim());
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_last_name.getText().toString().trim());

        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_email.getText().toString().trim());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_password.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_phone.getText().toString().trim());

     /*   if (registrationActivity.et_management.getText().toString().trim().equalsIgnoreCase("Maintain Current Weight"))
           type = RequestBody.create(MediaType.parse("text/plain"), "2");
        else
            type = RequestBody.create(MediaType.parse("text/plain"), "1");*/

        if (weight_managment_goal == 2) {
            type = RequestBody.create(MediaType.parse("text/plain"), "2");
            if (user_selection_val == 1) {
                userselectionbody = RequestBody.create(MediaType.parse("text/plain"), "0");
                weight = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_weight.getText().toString().trim());
                time = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_time_loss.getText().toString().trim());
            } else {
                userselectionbody = RequestBody.create(MediaType.parse("text/plain"), "1");
                weight = RequestBody.create(MediaType.parse("text/plain"), "");
                time = RequestBody.create(MediaType.parse("text/plain"), "");

            }


        } else {
            type = RequestBody.create(MediaType.parse("text/plain"), "1");
            userselectionbody = RequestBody.create(MediaType.parse("text/plain"), "0");
            weight = RequestBody.create(MediaType.parse("text/plain"), "");
            time = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        if (registrationActivity.et_units.getText().toString().trim().equalsIgnoreCase("KG/CM")) {
            units = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            units = RequestBody.create(MediaType.parse("text/plain"), "0");
        }
        if (registrationActivity.et_gender.getText().toString().trim().equals("Male")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else if (registrationActivity.et_gender.getText().toString().trim().equals("Female")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "0");
        } else {
            gender = RequestBody.create(MediaType.parse("text/plain"), "2");
        }
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.age.getText().toString().trim());
        RequestBody height = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_height.getText().toString().trim().split(Pattern.quote(" "))[0]);
        // RequestBody weight = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_weight.getText().toString().trim());
        //  RequestBody time = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_time_loss.getText().toString().trim());
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "2");
        RequestBody deviceToken = RequestBody.create(MediaType.parse("text/plain"), LoginShared.getDeviceToken(registrationActivity));
        RequestBody count = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_member.getText().toString());
        RequestBody state = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.state.getText().toString());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.city.getText().toString());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.address.getText().toString());
        RequestBody zip = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.zipcode.getText().toString());
        RequestBody regtype = RequestBody.create(MediaType.parse("text/plain"), "" + registrationActivity.regType);

        Call<ResponseBody> registration_api = apiInterface.call_registrationApi(first_name, middle_name, last_name,
                email, password, gender, phone, dob, height, weight, time, units, deviceType, type, deviceToken,
                userselectionbody, count, regtype, state, city, zip, address);

        registration_api.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    RegistrationModel registrationModel;

                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.optInt("status") == 1) {
                        registrationModel = gson.fromJson(responseString, RegistrationModel.class);
                        LoginShared.setRegistrationDataModel(registrationActivity, registrationModel);
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        LoginShared.setUserPhoto(registrationActivity, LoginShared.getRegistrationDataModel(registrationActivity).getData().getUser().get(0).getUserPhoto());
                        LoginShared.setUserName(registrationActivity, LoginShared.getRegistrationDataModel(registrationActivity).getData().getUser().get(0).getUserName());
                        LoginShared.setScaleUserId(Integer.parseInt(LoginShared.getRegistrationDataModel(registrationActivity).getData().getUser().get(0).getScaleUserId()));
                        MethodUtils.errorMsg(registrationActivity, jsObject.getString("message"));

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch (registrationActivity.regType) {
                                    case 1:
                                        Intent loginIntent = new Intent(registrationActivity, OtpActivity.class);
                                        registrationActivity.startActivity(loginIntent);
                                        registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        registrationActivity.finishAffinity();
                                        break;
                                    case 2:
                                        if (registrationActivity.et_member.getText().toString().equals("0")) {
                                            loginIntent = new Intent(registrationActivity, OtpActivity.class);
                                        } else {
                                            loginIntent = new Intent(registrationActivity, GroupInviteActivity.class);
                                            loginIntent.putExtra("count", Integer.parseInt(registrationActivity.et_member.getText().toString()));
                                        }
                                        registrationActivity.startActivity(loginIntent);
                                        registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        registrationActivity.finishAffinity();
                                        break;
                                    case 3:
                                        if (registrationActivity.et_member.getText().toString().equals("0")) {
                                            loginIntent = new Intent(registrationActivity, OtpActivity.class);
                                        } else {
                                            loginIntent = new Intent(registrationActivity, FamilyInviteActivity.class);
                                            loginIntent.putExtra("count", Integer.parseInt(registrationActivity.et_member.getText().toString()));
                                        }
                                        registrationActivity.startActivity(loginIntent);
                                        registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        registrationActivity.finishAffinity();
                                        break;
                                }
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);
                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(registrationActivity);
                        LoginShared.destroySessionTypePreference(registrationActivity);
                        LoginShared.setDeviceToken(registrationActivity, deviceToken);
                        Intent loginIntent = new Intent(registrationActivity, LoginActivity.class);
                        registrationActivity.startActivity(loginIntent);
                        registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        registrationActivity.finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(registrationActivity, jsObject.getString("message"));
                    }
                } catch (Exception e) {
                    MethodUtils.errorMsg(registrationActivity, registrationActivity.getString(R.string.error_occurred));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(registrationActivity, registrationActivity.getString(R.string.error_occurred));
            }
        });
    }

    private void callRegistrationApiWithImage() {
        loader.show_with_label("Loading");
        RequestBody gender = null;
        RequestBody units = null;
        RequestBody type = null;
        RequestBody userselectionbody = null;
        RequestBody weight = null;
        RequestBody time = null;
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), registrationActivity.mCompressedFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("userImage",
                registrationActivity.mCompressedFile.getName(), reqFile);
        RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_first_name.getText().toString().trim());
        RequestBody middle_name = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_middle_name.getText().toString().trim());
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_last_name.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_email.getText().toString().trim());
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_password.getText().toString().trim());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_phone.getText().toString().trim());
        //   RequestBody type = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_management.getText().toString().trim());
        if (registrationActivity.et_units.getText().toString().trim().equalsIgnoreCase("KG/CM")) {
            units = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else {
            units = RequestBody.create(MediaType.parse("text/plain"), "0");
        }
        if (registrationActivity.et_gender.getText().toString().trim().equals("Male")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "1");
        } else if (registrationActivity.et_gender.getText().toString().trim().equals("Female")) {
            gender = RequestBody.create(MediaType.parse("text/plain"), "0");
        } else {
            gender = RequestBody.create(MediaType.parse("text/plain"), "2");
        }

        if (weight_managment_goal == 2) {
            type = RequestBody.create(MediaType.parse("text/plain"), "2");

            if (user_selection_val == 1) {
                userselectionbody = RequestBody.create(MediaType.parse("text/plain"), "0");
                weight = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_weight.getText().toString().trim());
                time = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_time_loss.getText().toString().trim());
            } else {
                userselectionbody = RequestBody.create(MediaType.parse("text/plain"), "1");
                weight = RequestBody.create(MediaType.parse("text/plain"), "");
                time = RequestBody.create(MediaType.parse("text/plain"), "");
            }
        } else {
            type = RequestBody.create(MediaType.parse("text/plain"), "1");
            userselectionbody = RequestBody.create(MediaType.parse("text/plain"), "0");
            weight = RequestBody.create(MediaType.parse("text/plain"), "");
            time = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.age.getText().toString().trim());
        RequestBody height = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_height.getText().toString().trim());
        //  RequestBody weight = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_weight.getText().toString().trim());
        // RequestBody time = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_time_loss.getText().toString().trim());
        RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "2");
        RequestBody deviceToken = RequestBody.create(MediaType.parse("text/plain"), LoginShared.getDeviceToken(registrationActivity));
        RequestBody count = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.et_member.getText().toString());
        RequestBody state = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.state.getText().toString());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.city.getText().toString());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.address.getText().toString());
        RequestBody zip = RequestBody.create(MediaType.parse("text/plain"), registrationActivity.zipcode.getText().toString());
        RequestBody regtype = RequestBody.create(MediaType.parse("text/plain"), "" + registrationActivity.regType);


        Call<ResponseBody> registration_api = apiInterface.call_registrationImageApi(first_name, middle_name, last_name,
                email, password, gender, phone, dob, height, weight, time, units, deviceType, type, deviceToken,
                userselectionbody, count, regtype, state, city, zip, address, body);

        registration_api.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    RegistrationModel registrationModel;

                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.optInt("status") == 1) {

                        registrationModel = gson.fromJson(responseString, RegistrationModel.class);
                        LoginShared.setRegistrationDataModel(registrationActivity, registrationModel);
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        LoginShared.setUserPhoto(registrationActivity, LoginShared.getRegistrationDataModel(registrationActivity).getData().getUser().get(0).getUserPhoto());
                        LoginShared.setUserName(registrationActivity, LoginShared.getRegistrationDataModel(registrationActivity).getData().getUser().get(0).getUserName());
                        LoginShared.setScaleUserId(Integer.parseInt(LoginShared.getRegistrationDataModel(registrationActivity).getData().getUser().get(0).getScaleUserId()));
                        MethodUtils.errorMsg(registrationActivity, jsObject.getString("message"));

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent = new Intent(registrationActivity, OtpActivity.class);
                                registrationActivity.startActivity(loginIntent);
                                registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                registrationActivity.finish();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);
                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(registrationActivity);
                        LoginShared.destroySessionTypePreference(registrationActivity);
                        LoginShared.setDeviceToken(registrationActivity, deviceToken);
                        Intent loginIntent = new Intent(registrationActivity, LoginActivity.class);
                        registrationActivity.startActivity(loginIntent);
                        registrationActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        registrationActivity.finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        MethodUtils.errorMsg(registrationActivity, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(registrationActivity, registrationActivity.getString(R.string.error_occurred));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(registrationActivity, registrationActivity.getString(R.string.error_occurred));
            }
        });

    }

    private void showAndDismissTimePopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timePopup.showAsDropDown(registrationActivity.et_time_loss);
            }
        }, 100);
    }

    private void showAndDismissWeightPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                weightPopup.showAsDropDown(registrationActivity.et_weight);
            }
        }, 100);
    }

    private void showAndDismissManagementPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                managementPopup.showAsDropDown(registrationActivity.et_management);
            }
        }, 100);
    }

    private void showAndDismissHeightPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                heightPopup.showAsDropDown(registrationActivity.et_height);
            }
        }, 100);
    }

    private void showAndDismissGenderPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                genderPopup.showAsDropDown(registrationActivity.et_gender);
            }
        }, 100);
    }

    private void showAndDismissPrefferedPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                prefferedPopup.showAsDropDown(registrationActivity.et_units);
            }
        }, 100);
    }

    private void showAndDismissMemberPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                memberPopup.showAsDropDown(registrationActivity.et_member);
            }
        }, 100);
    }

    private void showAndDismissSelectionPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                selectionPopup.showAsDropDown(registrationActivity.et_userselection);
            }
        }, 100);
    }

    private void hideSoftKeyBoard() {
        View view = registrationActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) registrationActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
