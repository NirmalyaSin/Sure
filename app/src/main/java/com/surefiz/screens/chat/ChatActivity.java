package com.surefiz.screens.chat;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.chat.adapter.ChatAdapter;
import com.surefiz.screens.chat.model.ChatListResponse;
import com.surefiz.screens.chat.model.Conversation;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatActivity extends BaseActivity implements ChatAdapter.OnChatScrollListener{
    public View view;
    private RecyclerView recyclerView;
    private LoadingData loadingData;
    private ArrayList<Conversation> arrayListConversation = new ArrayList<Conversation>();
    private ChatAdapter mChatAdapter;
    private String receiver_id;
    private final int INITIAL_PAGINATION = 0;
    private int oldPagination;
    private EditText editTextMessage;
    private ImageView imageSendMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_chat, null);
        addContentView(view);
        //Receive receiverId from previous page.
        receiver_id = getIntent().getStringExtra("reciver_id");
        //Initialize Views
        initializeView();
    }

    private void initializeView() {
        setHeaderView();
        loadingData = new LoadingData(this);
        recyclerView = view.findViewById(R.id.recyclerView);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        imageSendMsg = view.findViewById(R.id.imageSendMsg);
        setRecyclerViewItem();
        //Call Api to list all chat
        callChatListApi(receiver_id, INITIAL_PAGINATION);

        imageSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString().trim();
                if(message.equals("")){
                    editTextMessage.setError("Enter message");
                }else {
                    //Send Chat message
                    callSendChatApi(message);
                    editTextMessage.setText("");
                }
            }
        });
    }

    private void setRecyclerViewItem() {
        mChatAdapter = new ChatAdapter(this, arrayListConversation, this);
        recyclerView.setAdapter(mChatAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration((int) 10);
        recyclerView.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

    }

    private void setHeaderView() {
        tv_universal_header.setText("Chat");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void callChatListApi(final String receiverId, final int newPagination) {
        if(oldPagination <newPagination || oldPagination==0){
            //Replace Old value with newer one.
            oldPagination = newPagination;
            //Show loader
            loadingData.show_with_label("Loading...");
            //Call API Using Retrofit
            Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
            final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
            String senderId = LoginShared.getRegistrationDataModel(this).getData().getUser()
                    .get(0).getUserId();

            Log.d("@@Sent-List-Chat : ","token = " +"\nsenderId ="+senderId
                    +"\nreceiverId = "+receiverId+"\noldPagination = "+oldPagination
                    +"\nnewPagination = "+newPagination);

            final Call<ChatListResponse> call_ConversationListApi = apiInterface
                    .call_ConversationListApi(token, senderId, receiverId, String.valueOf(newPagination));

            call_ConversationListApi.enqueue(new Callback<ChatListResponse>() {
                @Override
                public void onResponse(Call<ChatListResponse> call, Response<ChatListResponse> response) {
                    if (loadingData != null && loadingData.isShowing()) {
                        loadingData.dismiss();
                    }
                    Log.d("@@ChatList : " , response.body().toString());

                    if (response.body().getStatus() == 1) {
                        if(oldPagination==0){
                            arrayListConversation.clear();
                        }
               //        Collections.sort(response.body().getData().getConversations());
                        Collections.reverse(response.body().getData().getConversations());

                       arrayListConversation.addAll(0, response.body().getData().getConversations());

                        mChatAdapter.notifyDataSetChanged();
                        if(oldPagination==0){
                            recyclerView.smoothScrollToPosition(arrayListConversation.size());
                        }
                    }else {
                        MethodUtils.errorMsg(ChatActivity.this, response.body().getData().getMessage());
                    }

                }

                @Override
                public void onFailure(Call<ChatListResponse> call, Throwable t) {
                    if (loadingData != null && loadingData.isShowing())
                        loadingData.dismiss();
                    MethodUtils.errorMsg(ChatActivity.this, getString(R.string.error_occurred));
                }
            });
        }
    }

    private void callSendChatApi(final String message) {
        //Show loader
     //   loadingData.show_with_label("Loading...");
        //Call API Using Retrofit
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
        String senderId = LoginShared.getRegistrationDataModel(this).getData().getUser()
                .get(0).getUserId();

        Log.d("@@Sent-Chat : ","token = " +"\nsenderId ="+senderId
                +"\nreceiverId = "+receiver_id+"\nMessage = "+message);

        final Call<ChatListResponse> call_SendChatApi = apiInterface
                .call_SendChatApi(token, senderId, receiver_id, message);

        call_SendChatApi.enqueue(new Callback<ChatListResponse>() {
                @Override
                public void onResponse(Call<ChatListResponse> call, Response<ChatListResponse> response) {
                  /*  if (loadingData != null && loadingData.isShowing()) {
                        loadingData.dismiss();
                    }*/

                    if (response.body().getStatus() == 1) {
                        oldPagination = INITIAL_PAGINATION;
                        callChatListApi(receiver_id, INITIAL_PAGINATION);
                    }
                }

                @Override
                public void onFailure(Call<ChatListResponse> call, Throwable t) {
                    if (loadingData != null && loadingData.isShowing())
                        loadingData.dismiss();
                    //    MethodUtils.errorMsg(ChatActivity.this, getString(R.string.error_occurred));
                }
            });
    }

    @Override
    public void onScrollToTop(int scrollPosition) {
        //Calculate next pagination
        int div = arrayListConversation.size()/25;
        int pagination = div*25;
        Log.d("@@Scroll-Call : " , "\nIntegerDivision = "+div
                +"\npagination = "+pagination);
        //Call next pagination
        callChatListApi(receiver_id, pagination);
    }
}
