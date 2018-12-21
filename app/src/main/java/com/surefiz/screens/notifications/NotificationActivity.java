package com.surefiz.screens.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.acountabiltySearch.RequestState;
import com.surefiz.screens.acountabiltySearch.models.AddToCircleResponse;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.notifications.adapter.NotificationAdapter;
import com.surefiz.screens.notifications.models.Notification;
import com.surefiz.screens.notifications.models.NotificationsResponse;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationActivity extends BaseActivity implements
        NotificationAdapter.OnNotificationClickListener {
    public View view;
    private RecyclerView recyclerView;
    private LoadingData loadingData;
    private ArrayList<Notification> arrayListNotifications = new ArrayList<Notification>();
    private NotificationAdapter mNotificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_notifications, null);
        addContentView(view);
        initializeView();
        LoginShared.setWeightFromNotification(this, "0");
    }


    private void initializeView() {
        setHeaderView();
        loadingData = new LoadingData(this);
        recyclerView = view.findViewById(R.id.recyclerView);
        setRecyclerViewItem();
        //Call Api to list all notification
        callNotificationListApi();
    }

    private void setHeaderView() {
        tv_universal_header.setText("Notification");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
    }

    private void setRecyclerViewItem() {
        mNotificationAdapter = new NotificationAdapter(this,
                arrayListNotifications, this);
        recyclerView.setAdapter(mNotificationAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration((int) 10);
        recyclerView.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void callNotificationListApi() {
        loadingData.show_with_label("Loading...");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
        String id = LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId();
        Log.e("@@Sent-Notification : ", "Token = " + token + "\nUser-ID = " + id);

        final Call<NotificationsResponse> call_NotificationListApi = apiInterface
                .call_NotificationListApi(token, id);

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
                        arrayListNotifications.addAll(response.body().getData().getNotifications());
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
                //Show dialog to show response from server
                showResponseDialog(response.body().getStatus(),
                        response.body().getData().getMessage());
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cancel the dialog.
                dialog.dismiss();
                //Call the API to list all Notifications
                callNotificationListApi();
            }
        });

        dialog.create();
        dialog.show();
    }
}
