package com.surefiz.screens.reminders;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.reminders.model.ReminderListResponse;
import com.surefiz.screens.reminders.model.User;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddEditReminderActivity extends BaseActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public View view;
    private LoadingData loadingData;
    private EditText editReminderText, editReminderDate, editReminderTime;
    private Button buttonSaveReminder;
    private User mReminder;
    private String type;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_add_edit_reminder, null);
        addContentView(view);
        initializeView();
    }

    private void initializeView() {
        setHeaderView();
        loadingData = new LoadingData(this);
        editReminderText = view.findViewById(R.id.editReminderText);
        editReminderDate = view.findViewById(R.id.editReminderDate);
        editReminderTime = view.findViewById(R.id.editReminderTime);
        buttonSaveReminder = view.findViewById(R.id.buttonSaveReminder);
        //Get values from previous page
        setValuesToFields();

        buttonSaveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remiderText = editReminderText.getText().toString().trim();
                String date = editReminderDate.getText().toString().trim();
                String time = editReminderTime.getText().toString().trim();

                if(remiderText.equals("")){
                    editReminderText.setError("Enter reminder message");
                }else if(date.equals("")){
                    editReminderDate.setError("Select date");
                }else if(time.equals("")){
                    editReminderTime.setError("Select Time");
                }else {
                    String finalDateTime = date+" "+time;
                    switch (type){
                        case "add":
                            //Add new reminder
                            callAddUpdateReminderApi("", "Add", remiderText,
                                    finalDateTime);
                            break;
                        case "edit":
                            //Update existing reminder
                            callAddUpdateReminderApi(mReminder.getId(), "Edit", remiderText,
                                    finalDateTime);
                            break;
                    }
                }
            }
        });
    }

    private void setValuesToFields() {
        type = getIntent().getStringExtra("action_type");
        if(type.equals("add")){
            buttonSaveReminder.setVisibility(View.VISIBLE);
            tv_universal_header.setText("Create Reminder");
            //set field with Current date
            editReminderDate.setText(getCurrentDate());
            //set field with Current time
            editReminderTime.setText(getCurrentTime());
            //Enable Date-Time Picker
            enableDateTimeSelection();
        }else {
            mReminder = getIntent().getParcelableExtra("reminder");
            if(type.equals("view")){
                buttonSaveReminder.setVisibility(View.GONE);
                tv_universal_header.setText("Reminder Details");

                if(mReminder!=null){
                    editReminderText.setText(mReminder.getMessage());
                    editReminderDate.setText(mReminder.getDate());
                    editReminderTime.setText(mReminder.getTime());
                    editReminderDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    editReminderTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                    //Disable Edit Functionality
                    editReminderText.setFocusable(false);
                    editReminderText.setFocusableInTouchMode(false);
                }
            }else if(type.equals("edit")){
                buttonSaveReminder.setVisibility(View.VISIBLE);
                tv_universal_header.setText("Update Reminder");

                if(mReminder!=null){
                    editReminderText.setText(mReminder.getMessage());
                    editReminderDate.setText(mReminder.getDate());
                    editReminderTime.setText(mReminder.getTime());
                    //Enable Date-Time Picker
                    enableDateTimeSelection();

                    editReminderText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonSaveReminder.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }
    }

    private String getCurrentTime() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        String currentTime = hour + ":" + minute;

        return currentTime;
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        String currentDate = day+" "+monthName+" "+year;

        return currentDate;
    }

    public void enableDateTimeSelection(){
        editReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });

        editReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePickerDialog();
            }
        });
    }

    private void setHeaderView() {
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);
        img_topbar_menu.setVisibility(View.GONE);

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    private void callAddUpdateReminderApi(final String reminderId, final String type,
                                          final String reminderText, final String dateTime) {
        //Show loader
        loadingData.show_with_label("Saving...");
        //Call API Using Retrofit
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
        String userId = LoginShared.getRegistrationDataModel(this).getData().getUser()
                .get(0).getUserId();

        Log.d("@@Sent-Add-Reminder : ","token = " +"\nuserId ="+userId);

        final Call<ReminderListResponse> call_AddUpdateReminderApi = apiInterface
                .call_AddUpdateReminderApi(token, userId, reminderText,dateTime, type, reminderId);
        call_AddUpdateReminderApi.enqueue(new Callback<ReminderListResponse>() {
            @Override
            public void onResponse(Call<ReminderListResponse> call,
                                   Response<ReminderListResponse> response) {

                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }

                if(response.body().getStatus()==1){
                    //Open dialog to show success message and close the page
                    showResponseDialog(response.body().getStatus(),
                            response.body().getData().getMessage());
                }else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                    String deviceToken = LoginShared.getDeviceToken(AddEditReminderActivity.this);
                    LoginShared.destroySessionTypePreference(AddEditReminderActivity.this);
                    LoginShared.setDeviceToken(AddEditReminderActivity.this, deviceToken);
                    Intent loginIntent = new Intent(AddEditReminderActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }else {
                    if(type.equals("Add")) {
                        editReminderText.setText("");
                        editReminderDate.setText("");
                        editReminderTime.setText("");
                    }
                    //Show dialog with proper message
                    MethodUtils.errorMsg(AddEditReminderActivity.this, response.body()
                            .getData().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ReminderListResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                //Show Error dialog
                MethodUtils.errorMsg(AddEditReminderActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void openTimePickerDialog() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddEditReminderActivity.this,
                this, hour, minute, true);//Yes 24 hour time

        mTimePicker.setTitle("Select Time");
        mTimePicker.setCancelable(false);
        mTimePicker.show();
    }

    private void openDatePickerDialog() {
        //Create instance of Calender
        calendar = Calendar.getInstance(TimeZone.getDefault());
        //Create DatePicker instance with current date as default
        DatePickerDialog datePicker = new DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        //Disable previous dates for selection
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        //Set title of the picker
        datePicker.setTitle("Select date");
        datePicker.setCancelable(false);

        datePicker.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        //set field with selected date
        editReminderDate.setText(dayOfMonth+" "+monthName+" "+year);
        buttonSaveReminder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Set field with selected time
        editReminderTime.setText( hourOfDay + ":" + minute);
        buttonSaveReminder.setVisibility(View.VISIBLE);
    }

    public void showResponseDialog(int status, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cancel the dialog.
                dialog.dismiss();
                finish();
            }
        });

        dialog.create();
        dialog.show();
    }
}
