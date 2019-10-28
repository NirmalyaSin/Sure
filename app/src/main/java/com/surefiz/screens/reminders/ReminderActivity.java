package com.surefiz.screens.reminders;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.notificationclasses.ReminderNotification;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.reminders.adapter.ReminderAdapter;
import com.surefiz.screens.reminders.model.ReminderListResponse;
import com.surefiz.screens.reminders.model.User;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReminderActivity extends BaseActivity implements ReminderAdapter.OnReminderListener {
    public static final int EDIT_REMINDER_REQUEST_CODE = 1000;
    public View view;
    Intent addIntent = null;
    private RecyclerView recyclerView;
    private LoadingData loadingData;
    private ArrayList<User> arrayListReminder = new ArrayList<User>();
    private ReminderAdapter mReminderAdapter;
    private RelativeLayout rlRemiderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_reminder, null);
        addContentView(view);
        initializeView();

    }

    private void initializeView() {
        setHeaderView();
        loadingData = new LoadingData(this);
        recyclerView = view.findViewById(R.id.recyclerView);
        rlRemiderView = view.findViewById(R.id.rlRemiderView);
        //Setup RecyclerView
        setRecyclerViewItem();
        //Api Call to fetch all reminder list
        callReminderListApi();
    }

    private void setRecyclerViewItem() {

        mReminderAdapter = new ReminderAdapter(this, arrayListReminder, this);
        recyclerView.setAdapter(mReminderAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration(1);
        recyclerView.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        //ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(ItemTouchHelper.LEFT, ItemTouchHelper.LEFT, this);
        //new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

    }

    private void setHeaderView() {
        tv_universal_header.setText("Reminder");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.GONE);
        rl_back.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.VISIBLE);

        iv_AddPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ReminderActivity.this, AddEditReminderActivity.class);
                intent.putExtra("action_type", "add");
                startActivityForResult(intent, EDIT_REMINDER_REQUEST_CODE);
            }
        });
    }

    private void callReminderListApi() {
        //Show loader
        loadingData.show_with_label("Loading...");
        //Call API Using Retrofit
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
        String userId = LoginShared.getRegistrationDataModel(this).getData().getUser()
                .get(0).getUserId();

        Log.d("@@Sent-Reminder : ", "token = " + "\nuserId =" + userId);

        final Call<ReminderListResponse> call_ReminderListApi = apiInterface
                .call_ReminderListApi(token, userId);
        call_ReminderListApi.enqueue(new Callback<ReminderListResponse>() {
            @Override
            public void onResponse(Call<ReminderListResponse> call,
                                   Response<ReminderListResponse> response) {

                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }
                //Clear the previous list
                arrayListReminder.clear();

                if (response.body().getStatus() == 1) {
                    //Add Items to the list
                    arrayListReminder.addAll(response.body().getData().getReminderList());
                } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                    String deviceToken = LoginShared.getDeviceToken(ReminderActivity.this);
                    LoginShared.destroySessionTypePreference(ReminderActivity.this);
                    LoginShared.setDeviceToken(ReminderActivity.this, deviceToken);
                    Intent loginIntent = new Intent(ReminderActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    //Show dialog with proper message
                    MethodUtils.errorMsg(ReminderActivity.this, response.body()
                            .getData().getMessage());
                }
                //Refresh the list
                mReminderAdapter.notifyDataSetChanged();

                //Creates notification for Newly Added Reminder Only
                if (addIntent != null && addIntent.getStringExtra("action_type").equalsIgnoreCase("add")) {
                    int lastPosition = arrayListReminder.size() - 1;
                    String finalDateTime = arrayListReminder.get(lastPosition).getDate() + " " + arrayListReminder.get(lastPosition).getTime();
                    new ReminderNotification(ReminderActivity.this, arrayListReminder.get(lastPosition).getId(),
                            arrayListReminder.get(lastPosition).getMessage(), finalDateTime);
                }
            }

            @Override
            public void onFailure(Call<ReminderListResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                //Show Error dialog
                MethodUtils.errorMsg(ReminderActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    @Override
    public void onReminderSelected(int position) {
        Intent viewIntent = new Intent(this, AddEditReminderActivity.class);
        viewIntent.putExtra("action_type", "view");
        viewIntent.putExtra("reminder", arrayListReminder.get(position));
        startActivity(viewIntent);
    }

    @Override
    public void onEditEdit(int position) {
        Intent editIntent = new Intent(this, AddEditReminderActivity.class);
        editIntent.putExtra("action_type", "edit");
        editIntent.putExtra("reminder", arrayListReminder.get(position));
        startActivityForResult(editIntent, EDIT_REMINDER_REQUEST_CODE);
    }

    @Override
    public void onRemiderDeleted(int position) {
        removeItem(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REMINDER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            addIntent = data;
            callReminderListApi();
        } else {
            addIntent = null;
        }
    }

    public void removeItem(int position) {

        removeReminderApi(position);
    }

    public void restoreItem(User item, int position) {
        arrayListReminder.add(position, item);
        // notify item added by position
        mReminderAdapter.notifyItemInserted(position);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mReminderAdapter != null) {
            mReminderAdapter.saveStates(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mReminderAdapter != null) {
            mReminderAdapter.restoreStates(savedInstanceState);
        }
    }


    private void removeReminderApi(int position) {
        //Show loader
        loadingData.show_with_label("Loading...");
        //Call API Using Retrofit
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
        String reminderId = arrayListReminder.get(position).getId();

        Log.d("@@Delete-Reminder : ", "token = " + "\nuserId =" + reminderId);

        final Call<ResponseBody> call_ReminderListApi = apiInterface.call_RemoveReminderApi(token, reminderId);
        call_ReminderListApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    System.out.println("removeReminder: " + jsonObject.toString());

                    if (jsonObject.optInt("status") == 1) {

                        // Cancelling the Notification
                        new ReminderNotification(ReminderActivity.this, reminderId);
                        arrayListReminder.remove(position);
                        mReminderAdapter.notifyItemRemoved(position);

                        MethodUtils.errorMsg(ReminderActivity.this, jsonObject.getJSONObject("data").getString("message"));

                        /*Snackbar snackbar = Snackbar.make(rlRemiderView, jsonObject.getJSONObject("data").getString("message"), Snackbar.LENGTH_LONG);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.show();*/

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(ReminderActivity.this);
                        LoginShared.destroySessionTypePreference(ReminderActivity.this);
                        LoginShared.setDeviceToken(ReminderActivity.this, deviceToken);
                        Intent loginIntent = new Intent(ReminderActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        //Show dialog with proper message
                        MethodUtils.errorMsg(ReminderActivity.this, jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                //Show Error dialog
                MethodUtils.errorMsg(ReminderActivity.this, getString(R.string.error_occurred));
            }
        });
    }


}
