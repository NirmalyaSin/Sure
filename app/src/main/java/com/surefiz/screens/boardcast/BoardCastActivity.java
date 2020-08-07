package com.surefiz.screens.boardcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.rts.commonutils_2_0.emoji.EmojiFormatter;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.application.MyApplicationClass;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.boardcast.adapter.MesgAdapter;
import com.surefiz.screens.boardcast.model.BroadcastItem;
import com.surefiz.screens.boardcast.model.Message;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BoardCastActivity extends BaseActivity implements View.OnClickListener, MesgAdapter.OnChatScrollListener {

    private final int INITIAL_PAGINATION = 0;
    public View view;
    EditText et_message;
    ImageView btn_boardcast;
    private LoadingData loader;
    private RecyclerView recyclerView;
    private ArrayList<BroadcastItem> arrayListConversation = new ArrayList<BroadcastItem>();
    private MesgAdapter mesgAdapter;
    private String receiver_id;
    private int oldPagination;
    private int saveMessageCount = 0;
    private boolean isMessageSent = false;
    private MyApplicationClass myApplicationClass;
    private BroadcastReceiver broadCastmessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setMessage();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_board_cast, null);
        addContentView(view);


        loader = new LoadingData(BoardCastActivity.this);

        myApplicationClass = (MyApplicationClass) getApplication();

        receiver_id = getIntent().getStringExtra("reciver_id");

        LoginShared.setWeightFromNotification(this, "0");


        setHeaderView();
        viewBind();
        clickEventFunc();

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadCastmessageReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        setMessage();
        registerReceiver(broadCastmessageReceiver, new IntentFilter("ON_BROAD_CAST_MESSAGE_RECEIVED"));

    }

    private void setMessage() {
        if (myApplicationClass.broadcastItemsNotification.size() > 0) {
            for (BroadcastItem broadcastItem : myApplicationClass.broadcastItemsNotification)
                if (!broadcastItem.getSenderId().equals(receiver_id)) {
                    arrayListConversation.add(broadcastItem);
                }
        }
        saveMessageCount = arrayListConversation.size();
        mesgAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(arrayListConversation.size() - 1);
        myApplicationClass.broadcastItemsNotification.clear();
    }


    private void viewBind() {

        et_message = findViewById(R.id.et_message);
        btn_boardcast = findViewById(R.id.btn_boardcast);
        recyclerView = view.findViewById(R.id.recyclerView);

        setRecyclerViewItem();
        callChatListApi(receiver_id, INITIAL_PAGINATION);


    }

    private void setRecyclerViewItem() {
        mesgAdapter = new MesgAdapter(this, arrayListConversation, this);
        recyclerView.setAdapter(mesgAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        recyclerView.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void clickEventFunc() {
        btn_boardcast.setOnClickListener(this);
    }

    private void callChatListApi(final String receiverId, final int newPagination) {



        if (oldPagination < newPagination || oldPagination == 0) {
            //Replace Old value with newer one.
            oldPagination = newPagination;

            loader.show_with_label("Loading...");
            //Call API Using Retrofit
            Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
            final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
            String senderId = LoginShared.getRegistrationDataModel(this).getData().getUser()
                    .get(0).getUserId();

            Log.d("@@Sent-List-Chat : ", "token = " + "\nsenderId =" + senderId);

            final Call<Message> call_BroadcastMessageApi = apiInterface
                    .call_BroadcastMessageApi(token, senderId, String.valueOf(oldPagination));

            call_BroadcastMessageApi.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    if (loader != null && loader.isShowing()) {
                        loader.dismiss();
                    }

                    if (response.body().getStatus() == 1) {

                        if (response.body().getData().getBroadcast() != null) {

                            mesgAdapter.setLoadMore(false);

                            if (arrayListConversation.size() == 0) {
                                arrayListConversation.clear();
                                Collections.reverse(response.body().getData().getBroadcast());
                                arrayListConversation.addAll(response.body().getData().getBroadcast());
                            } else {
                                Collections.reverse(response.body().getData().getBroadcast());
                                arrayListConversation.addAll(0, response.body().getData().getBroadcast());
                            }

                           // Log.d("@@ListSize : ", "" + arrayListConversation.size());

                            if (!isMessageSent) {
                                mesgAdapter.notifyItemRangeChanged(0, arrayListConversation.size());
                            } else {
                                mesgAdapter.notifyDataSetChanged();
                            }
                            moveToEnd();

                            if (response.body().getData().getBroadcast().size() == 0) {
                                MethodUtils.errorMsg(BoardCastActivity.this, response.body().getData().getMessage());
                            }
                        }
                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(BoardCastActivity.this);
                        LoginShared.destroySessionTypePreference(BoardCastActivity.this);
                        LoginShared.setDeviceToken(BoardCastActivity.this, deviceToken);
                        Intent loginIntent = new Intent(BoardCastActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        if (!response.body().getData().getMessage().equalsIgnoreCase("No Records Found")) {
                            MethodUtils.errorMsg(BoardCastActivity.this, response.body().getData().getMessage());
                        }
                    }

                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    if (loader != null && loader.isShowing())
                        loader.dismiss();
                    MethodUtils.errorMsg(BoardCastActivity.this, getString(R.string.error_occurred));
                }
            });
        }

    }

    private void moveToEnd() {

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isMessageSent) {
                    recyclerView.smoothScrollToPosition(arrayListConversation.size() - 1);
                    isMessageSent = false;
                    System.out.println("First: " + 1);
                } else {
                    if (oldPagination == 0) {
                        if (arrayListConversation.size() > 0) {
                            recyclerView.smoothScrollToPosition(arrayListConversation.size() - 1);
                        }
                        System.out.println("First: " + 2);
                    } else {
                        if (arrayListConversation.size() > 0) {
                            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(arrayListConversation.size() - saveMessageCount - 1, 0);
                        }
                        System.out.println("First: " + 3);
                    }
                }

                if (saveMessageCount < arrayListConversation.size()) {
                    if (arrayListConversation.size() > 24) {
                        mesgAdapter.setLoadMore(true);
                    } else {
                        mesgAdapter.setLoadMore(false);
                    }
                } else {
                    mesgAdapter.setLoadMore(false);
                }

                saveMessageCount = arrayListConversation.size();
            }
        }, 50);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        arrayListConversation.clear();
        oldPagination = INITIAL_PAGINATION;
        isMessageSent = true;
        saveMessageCount = 0;
        callChatListApi(receiver_id, INITIAL_PAGINATION);
    }

    private void callBoardCastApi() {

        //loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        String toServerUnicodeEncoded = EmojiFormatter.encodeToNonLossyAscii(et_message.getText().toString().trim());

        final Call<ResponseBody> call_boardcastApi = apiInterface.call_boardcastApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(), toServerUnicodeEncoded);

        call_boardcastApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        Gson gson = new Gson();
                        BroadcastItem broadcastItem = gson.fromJson(jsObject.toString(), BroadcastItem.class);

                        arrayListConversation.add(broadcastItem);
                        isMessageSent = true;
                        mesgAdapter.notifyDataSetChanged();
                        moveToEnd();
                        et_message.setText("");

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(BoardCastActivity.this);
                        LoginShared.destroySessionTypePreference(BoardCastActivity.this);
                        LoginShared.setDeviceToken(BoardCastActivity.this, deviceToken);
                        Intent loginIntent = new Intent(BoardCastActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(BoardCastActivity.this, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(BoardCastActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(BoardCastActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void setHeaderView() {
        tv_universal_header.setText("Message Board");
        img_topbar_menu.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);

        rl_back.setVisibility(View.VISIBLE);

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_boardcast:

                if (et_message.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(BoardCastActivity.this, "Please enter some text to BroadCast.");
                } else if (!ConnectionDetector.isConnectingToInternet(BoardCastActivity.this)) {
                    MethodUtils.errorMsg(BoardCastActivity.this, BoardCastActivity.this.getString(R.string.no_internet));
                } else {
                    callBoardCastApi();
                }
                break;

        }

    }

    @Override
    public void onScrollToTop(int scrollPosition) {
        int div = arrayListConversation.size() / 25;
        int pagination = div * 25;
        callChatListApi(receiver_id, pagination);
    }
}
