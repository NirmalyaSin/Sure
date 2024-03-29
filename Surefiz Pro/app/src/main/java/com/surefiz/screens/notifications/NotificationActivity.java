package com.surefiz.screens.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.CustomAlert;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.acountabiltySearch.RequestState;
import com.surefiz.screens.bmidetails.BMIDetailsActivity;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.notifications.adapter.NotificationAdapter;
import com.surefiz.screens.notifications.models.Notification;
import com.surefiz.screens.notifications.models.NotificationsResponse;
import com.surefiz.screens.progressstatus.ProgressStatusActivity;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MessagDateConverter;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationActivity extends BaseActivity implements
        NotificationAdapter.OnNotificationClickListener, View.OnClickListener {
    public View view;
    private RecyclerView recyclerView;
    private LoadingData loadingData;
    private ArrayList<Notification> arrayListNotifications = new ArrayList<Notification>();
    private NotificationAdapter mNotificationAdapter;
    private LinearLayout ll_notification_tab;
    private TextView txt_stepped, txt_performance, txt_battery;
    private boolean isFromDashboard;
    private int selectedTab = 0;
    private  boolean isOK=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_notifications, null);
        addContentView(view);
        initializeView();
        LoginShared.setWeightFromNotification(this, "0");
    }


    private void initializeView() {
        loadingData = new LoadingData(this);
        recyclerView = view.findViewById(R.id.recyclerView);
        ll_notification_tab = findViewById(R.id.ll_notification_tab);
        txt_stepped = findViewById(R.id.txt_notification_stepped);
        txt_battery = findViewById(R.id.txt_notification_battery);
        txt_performance = findViewById(R.id.txt_notification_performance);
        txt_stepped.setOnClickListener(this);
        txt_performance.setOnClickListener(this);
        txt_battery.setOnClickListener(this);
        setRecyclerViewItem();
        //Call Api to list all notification
        isFromDashboard = getIntent().getBooleanExtra("fromDashboard", false);
        if (isFromDashboard) {
            ll_notification_tab.setVisibility(View.GONE);
            callNotificationListApi("4");
            selectedTab = 4;
        } else {
            ll_notification_tab.setVisibility(View.VISIBLE);
            //txt_stepped.performClick();
            //selectedTab = 1;

            txt_battery.performClick();
            selectedTab = 3;
        }
        setHeaderView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        isFromDashboard = intent.getBooleanExtra("fromDashboard", false);
        if (isFromDashboard) {
            ll_notification_tab.setVisibility(View.GONE);
            callNotificationListApi("4");
            selectedTab = 4;
        } else {
            ll_notification_tab.setVisibility(View.VISIBLE);
            txt_stepped.performClick();
            selectedTab = 1;
        }

        tv_universal_header.setText(isFromDashboard ? "Friend Requests" : "Notification");
    }

    private void setHeaderView() {
        tv_universal_header.setText(isFromDashboard ? "Friend Requests" : "Notification");
        btn_add.setVisibility(View.VISIBLE);
        rlUserSearch.setVisibility(View.GONE);
        btn_add.setText("Clear");
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callClearNotification();
            }
        });
        iv_edit.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        rlFriendRequest.setVisibility(View.GONE);

        if (isFromDashboard) {
            img_topbar_menu.setVisibility(View.GONE);
            rl_back.setVisibility(View.VISIBLE);
            rl_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBackPressed();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromDashboard) {
            callBackPressed();
        } else {
            super.onBackPressed();
        }

    }

    private void callBackPressed() {
        startActivity(new Intent(NotificationActivity.this, DashBoardActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finishAffinity();
    }

    private void setRecyclerViewItem() {
        mNotificationAdapter = new NotificationAdapter(this,
                arrayListNotifications, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);
        recyclerView.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mNotificationAdapter);
    }

    private void callNotificationListApi(String type) {
        loadingData.show_with_label("Loading...");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
        String id = LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId();
        Log.e("@@Sent-Notification : ", "Token = " + token + "\nUser-ID = " + id);

        final Call<NotificationsResponse> call_NotificationListApi = apiInterface
                .call_NotificationListApi(token, id, type, "1");

        call_NotificationListApi.enqueue(new Callback<NotificationsResponse>() {
            @Override
            public void onResponse(Call<NotificationsResponse> call, Response<NotificationsResponse> response) {
                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                    arrayListNotifications.clear();
                }
                Log.d("@@NotificationList : ", response.body().toString());

                try {
                    if (response.body().getStatus() == 1) {
                        arrayListNotifications.clear();
                        if (response.body().getData().getNotifications() != null)
                            arrayListNotifications.addAll(response.body().getData().getNotifications());
                        else {
                            if (type.equals("4")) {
                                if(!isOK)
                                    MethodUtils.errorMsg(NotificationActivity.this, "No friend request pending.");
                            } else {
                                MethodUtils.errorMsg(NotificationActivity.this, "No notification found.");
                            }
                        }
                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(NotificationActivity.this);
                        LoginShared.destroySessionTypePreference(NotificationActivity.this);
                        LoginShared.setDeviceToken(NotificationActivity.this, deviceToken);
                        Intent loginIntent = new Intent(NotificationActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        MethodUtils.errorMsg(NotificationActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(NotificationActivity.this, getString(R.string.error_occurred));
                }

                mNotificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NotificationsResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                MethodUtils.errorMsg(NotificationActivity.this, getString(R.string.error_occurred));
            }
        });
    }


    //Service call to send/cancel request to add in circle
    private void acceptRejectRequestApi(final int listPosition, final String type) {
        loadingData.show_with_label("Processing...");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
        String user_id = LoginShared.getRegistrationDataModel(this).getData().getUser()
                .get(0).getUserId();
        String senderId = arrayListNotifications.get(listPosition).getNotificationSenderId();
        Log.e("@@Sent-Accept : ", "Token = " + token + "\nUser-ID = " + user_id
                + "\nsenderId = " + senderId + "\nType = " + type);

        final Call<NotificationsResponse> call_AcceptRejectFriendRequestApi = apiInterface.call_AcceptRejectFriendRequestApi(
                token.trim(), user_id.trim(), senderId.trim(), type.trim());

        call_AcceptRejectFriendRequestApi.enqueue(new Callback<NotificationsResponse>() {
            @Override
            public void onResponse(Call<NotificationsResponse> call, Response<NotificationsResponse> response) {
                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }
                Log.d("@@AcceptRequest : ", response.body().toString());

                if (response.body().getStatus() == 1) {
                    //Show dialog to show response from server
                    showResponseDialog(response.body().getStatus(), response.body().getData().getMessage());
                } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                    String deviceToken = LoginShared.getDeviceToken(NotificationActivity.this);
                    LoginShared.destroySessionTypePreference(NotificationActivity.this);
                    LoginShared.setDeviceToken(NotificationActivity.this, deviceToken);
                    Intent loginIntent = new Intent(NotificationActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finishAffinity();
                } else {
                    //Show error dialog
                    MethodUtils.errorMsg(NotificationActivity.this, response.body().getData().getMessage());
                }
            }

            @Override
            public void onFailure(Call<NotificationsResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }
                //Show error dialog
                MethodUtils.errorMsg(NotificationActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    @Override
    public void onViewClick(int position) {
        if (arrayListNotifications.size() >= position) {
            Notification item = arrayListNotifications.get(position);

            if(item.getNotificationType().equals("1") || item.getNotificationType().equals("10"))
                callReadNotification(item.getNotificationId(), item.getNotificationType(), position);
            else
                notificationClick(item);
        }
    }

    @Override
    public void onAccept(int position) {
        //Accept request Api Call
        acceptRejectRequestApi(position, RequestState.SEND_ACCEPT_FRIEND_REQUEST);
    }

    @Override
    public void onReject(int position) {
        //Reject request Api Call
        acceptRejectRequestApi(position, RequestState.SEND_REJECT_FRIEND_REQUEST);
    }

    public void showResponseDialog(int status, String message) {

        CustomAlert customAlert=new CustomAlert(this);
        customAlert.setSubText(message);
        customAlert.show();
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
                //Call the API to list all Notifications
                isOK=true;
                callNotificationListApi("4");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_notification_stepped:
                selectedTab = 1;
                txt_battery.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
                txt_performance.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
                txt_stepped.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue));

                txt_battery.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                txt_performance.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                txt_stepped.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
                callNotificationListApi("1");
                break;
            case R.id.txt_notification_performance:
                selectedTab = 2;
                txt_battery.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
                txt_performance.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue));
                txt_stepped.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

                txt_battery.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                txt_performance.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
                txt_stepped.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                callNotificationListApi("2");
                break;
            case R.id.txt_notification_battery:
                selectedTab = 3;
                txt_battery.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue));
                txt_performance.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
                txt_stepped.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

                txt_battery.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
                txt_performance.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                txt_stepped.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                callNotificationListApi("3");
                break;
            default:
                super.onClick(v);
        }


    }

    private void callClearNotification() {

        CustomAlert customAlert=new CustomAlert(this);
        customAlert.setSubText("Do you want to clear all notifications?");
        customAlert.setCancelVisible();
        customAlert.show();
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
                callReadNotification("", "" + selectedTab, 0);

            }
        });

        customAlert.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
            }
        });
    }

    private void callReadNotification(String notificationId, String notificationType, final int adapterPosition) {
        loadingData.show_with_label("Loading...");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
        String id = LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId();
        Log.e("@@Sent-Notification : ", "Token = " + token + "\nUser-ID = " + id);

        final Call<NotificationsResponse> call_NotificationListApi = apiInterface
                .call_readNotification(token, id, notificationType, notificationId);

        call_NotificationListApi.enqueue(new Callback<NotificationsResponse>() {
            @Override
            public void onResponse(Call<NotificationsResponse> call, Response<NotificationsResponse> response) {
                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }
                try {
                    if (response.body().getStatus() == 1) {
                        if (notificationId.isEmpty()) {
                            arrayListNotifications.clear();
                            mNotificationAdapter.notifyDataSetChanged();
                        } else {
                            Notification item = arrayListNotifications.get(adapterPosition);
                            arrayListNotifications.remove(adapterPosition);
                            mNotificationAdapter.notifyItemRemoved(adapterPosition);
                            notificationClick(item);
                        }
                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(NotificationActivity.this);
                        LoginShared.destroySessionTypePreference(NotificationActivity.this);
                        LoginShared.setDeviceToken(NotificationActivity.this, deviceToken);
                        Intent loginIntent = new Intent(NotificationActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        MethodUtils.errorMsg(NotificationActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(NotificationActivity.this, getString(R.string.error_occurred));
                }

                mNotificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NotificationsResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                MethodUtils.errorMsg(NotificationActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void notificationClick(Notification item) {
        switch (item.getNotificationType()) {
            case "10":
                if (item.getNotificationDate() != null && item.getNotificationTime() != null) {
                    String dateStr = item.getNotificationDate()  + " " + item.getNotificationTime();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    try {
                        /*Date date = dateFormat.parse(MessagDateConverter.getConvertedNotificationDate(item.getNotificationDate() + " " + item.getNotificationTime()));
                        Date currentDate = new Date();
                        long diff = currentDate.getTime() - date.getTime();
                        int diffSecond = (int) (diff / 1000);
                        if (diffSecond < 115) {*/

                        Date date = dateFormat.parse(MessagDateConverter.getConvertedNotificationDate(dateStr));
                        Date currentDate = new Date();
                        long diff = currentDate.getTime() - date.getTime();
                        //*********************AVIK
                        int diffSecond = (int) (diff / 1000);
                        if (diffSecond < 120) {
                            int remainingTime=120-diffSecond;

                            Intent intent = new Intent(this, WeightDetailsActivity.class);
                            intent.putExtra("timerValue", remainingTime);
                            intent.putExtra("fromPush", "1");

                            intent.putExtra("shouldOpenWeightAssignView", true);
                            intent.putExtra("userWeight", item.getUserWeight());
                            intent.putExtra("scaleMacAddress", item.getScaleMacAddress());
                            intent.putExtra("text", item.getNotificationText());

                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        } else {
                            MethodUtils.errorMsg(this, "Your notification is expired.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case "1":
                if (item.getNotificationDate() != null && item.getNotificationTime() != null) {
                    String dateStr = item.getNotificationDate()  + " " + item.getNotificationTime();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    try {
                        Date date = dateFormat.parse(MessagDateConverter.getConvertedNotificationDate(dateStr));
                        Date currentDate = new Date();
                        long diff = currentDate.getTime() - date.getTime();
                        //*********************AVIK
                        int diffSecond = (int) (diff / 1000);
                        if (diffSecond < 120) {
                            int remainingTime=120-diffSecond;

                            Intent intent = new Intent(this, WeightDetailsActivity.class);
                            intent.putExtra("timerValue", remainingTime);
                            intent.putExtra("fromPush", "1");
                            intent.putExtra("shouldOpenWeightAssignView", false);

                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        } else {
                            MethodUtils.errorMsg(this, "Your notification is expired.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "2":

                if (item.getContentId() != null) {
                    Intent intent = new Intent(this, ProgressStatusActivity.class);
                    intent.putExtra("userId", LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId());
                    intent.putExtra("contentId", item.getContentId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case "3":
                if (item.getContentId() != null) {

                    Intent intent = new Intent(this, BMIDetailsActivity.class);
                    intent.putExtra("ScaleUserId", LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getScaleUserId());
                    intent.putExtra("serverUserId", item.getserverUserId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
        }
    }

}
