package com.surefiz.screens.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.application.MyApplicationClass;
import com.surefiz.dialog.CustomAlert;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.chat.adapter.ChatAdapter;
import com.surefiz.screens.chat.model.ChatListResponse;
import com.surefiz.screens.chat.model.Conversation;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.OnChatScrollListener {
    private final int INITIAL_PAGINATION = 0;
    //public View view;
    public Handler handler;
    private RecyclerView recyclerView;
    private LoadingData loadingData;
    private ArrayList<Conversation> arrayListConversation = new ArrayList<Conversation>();
    private ChatAdapter mChatAdapter;
    private String receiver_id;
    private int oldPagination;
    private EditText editTextMessage;
    private ImageView imageSendMsg;
    private MyApplicationClass myApplicationClass;
    private TextView tvUserName;
    private ImageLoader imageLoader;
    private CircleImageView ivUserImage;
    private int saveMessageCount = 0;
    private boolean isMessageSent = false;
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setMessage();
        }
    };

    public static String encodeToNonLossyAscii(String original) {
        Charset asciiCharset = Charset.forName("US-ASCII");
        if (asciiCharset.newEncoder().canEncode(original)) {
            return original;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < original.length(); i++) {
            char c = original.charAt(i);
            if (c < 128) {
                stringBuffer.append(c);
            } else if (c < 256) {
                String octal = Integer.toOctalString(c);
                stringBuffer.append("\\");
                stringBuffer.append(octal);
            } else {
                String hex = Integer.toHexString(c);
                stringBuffer.append("\\u");
                stringBuffer.append(hex);
            }
        }
        return stringBuffer.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        handler = new Handler();
        initializeImageLoader();
        myApplicationClass = (MyApplicationClass) getApplication();
        LoginShared.setWeightFromNotification(this, "0");
        //Receive receiverId from previous page.
        receiver_id = getIntent().getStringExtra("reciver_id");
        //Initialize Views
        initializeView();
    }

    private void initializeImageLoader() {
        DisplayImageOptions opts = new DisplayImageOptions.Builder().cacheInMemory(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).defaultDisplayImageOptions(opts)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private void initializeView() {
        setHeaderView();
        loadingData = new LoadingData(this);
        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageSendMsg = findViewById(R.id.imageSendMsg);


        setRecyclerViewItem();
        //Call Api to list all chat
        callChatListApi(receiver_id, INITIAL_PAGINATION);

        imageSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString().trim();
                if (message.equals("")) {
                    editTextMessage.setError("Please enter a message");
                } else {
                    //Send Chat message
                    try {
                        String toServerUnicodeEncoded = encodeToNonLossyAscii(message);
                        callSendChatApi(toServerUnicodeEncoded);
                        editTextMessage.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        /*handler.postDelayed(new Runnable() {
            public void run() {
                *//*draftModelArrayList.clear();
                getJsonData();*//*
                if (myApplicationClass.chatListNotification.size() > 0) {
                    arrayListConversation.addAll(myApplicationClass.chatListNotification);
                    saveMessageCount = arrayListConversation.size();
                    mChatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(arrayListConversation.size() - 1);
                    myApplicationClass.chatListNotification.clear();
                }
                handler.postDelayed(this, 500);
            }
        }, 500);*/
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(messageReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        setMessage();
        registerReceiver(messageReceiver, new IntentFilter("ON_MESSAGE_RECEIVED"));

    }

    private void setMessage() {
        if (myApplicationClass.chatListNotification.size() > 0) {
            arrayListConversation.addAll(myApplicationClass.chatListNotification);
            saveMessageCount = arrayListConversation.size();
            mChatAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(arrayListConversation.size() - 1);
            myApplicationClass.chatListNotification.clear();
        }
    }

    private void setRecyclerViewItem() {
        mChatAdapter = new ChatAdapter(this, arrayListConversation, this);
        recyclerView.setAdapter(mChatAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);
        recyclerView.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setHeaderView() {

        RelativeLayout rl_back = findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvUserName = findViewById(R.id.tvUserName);
        ivUserImage = findViewById(R.id.ivUserImage);
    }

    private void callChatListApi(final String receiverId, final int newPagination) {
        if (oldPagination < newPagination || oldPagination == 0) {
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

            Log.d("@@Sent-List-Chat : ", "token = " + "\nsenderId =" + senderId
                    + "\nreceiverId = " + receiverId + "\noldPagination = " + oldPagination
                    + "\nnewPagination = " + newPagination);

            final Call<ChatListResponse> call_ConversationListApi = apiInterface
                    .call_ConversationListApi(token, senderId, receiverId, String.valueOf(newPagination));

            call_ConversationListApi.enqueue(new Callback<ChatListResponse>() {
                @Override
                public void onResponse(Call<ChatListResponse> call, Response<ChatListResponse> response) {
                    if (loadingData != null && loadingData.isShowing()) {
                        loadingData.dismiss();
                    }
                    //Log.d("@@ChatList : ", response.body().toString());

                    if (response.body().getStatus() == 0) {
                        try {
                            tvUserName.setText(response.body().getData().getReceivername());
                            imageLoader.displayImage(response.body().getData().getReceiverphoto(), ivUserImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (response.body().getStatus() == 1) {

                        try {
                            tvUserName.setText(response.body().getData().getReceivername());
                            imageLoader.displayImage(response.body().getData().getReceiverphoto(), ivUserImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if (oldPagination == 0) {
                            arrayListConversation.clear();
                        }

                        Collections.reverse(response.body().getData().getConversations());
                        arrayListConversation.addAll(0, response.body().getData().getConversations());


                        if (!isMessageSent) {
                            mChatAdapter.notifyItemRangeChanged(0, arrayListConversation.size());
                        } else {
                            mChatAdapter.notifyDataSetChanged();
                        }
                        moveToEnd();

                        myApplicationClass.chatListNotification.clear();

                        if (arrayListConversation.size() == 0) {
                            showNoRecordsDialog(response.body().getData().getMessage());
                        }

                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(ChatActivity.this);
                        LoginShared.destroySessionTypePreference(ChatActivity.this);
                        LoginShared.setDeviceToken(ChatActivity.this, deviceToken);
                        Intent loginIntent = new Intent(ChatActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        if (!response.body().getData().getMessage().equalsIgnoreCase("No Records Found")) {
                            MethodUtils.errorMsg(ChatActivity.this, response.body().getData().getMessage());
                        }
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
                        mChatAdapter.setLoadMore(true);
                    } else {
                        mChatAdapter.setLoadMore(false);
                    }
                } else {
                    mChatAdapter.setLoadMore(false);
                }

                saveMessageCount = arrayListConversation.size();
            }
        }, 50);
    }


    public void showNoRecordsDialog(String message) {

        CustomAlert customAlert=new CustomAlert(this);
        customAlert.setSubText(message);
        customAlert.setKeyName("","Got it");
        customAlert.show();
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
            }
        });
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

        Log.d("@@Sent-Chat : ", "token = " + "\nsenderId =" + senderId
                + "\nreceiverId = " + receiver_id + "\nMessage = " + message);

        final Call<ResponseBody> call_SendChatApi = apiInterface
                .call_SendChatApi(token, senderId, receiver_id, message);

        call_SendChatApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {

                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        if (jsObject.getJSONArray("sendMsg").length() > 0) {
                            Conversation conversation = new Conversation();
                            conversation.setSenderId(Integer.valueOf(jsObject.getJSONArray("sendMsg").getJSONObject(0).getString("senderId")));
                            conversation.setReciverId(Integer.valueOf(jsObject.getJSONArray("sendMsg").getJSONObject(0).getString("receiverId")));
                            conversation.setMessageFrom(jsObject.getJSONArray("sendMsg").getJSONObject(0).getString("messageFrom"));
                            conversation.setMessage(jsObject.getJSONArray("sendMsg").getJSONObject(0).getString("message"));
                            conversation.setDateTime(jsObject.getJSONArray("sendMsg").getJSONObject(0).getString("dateTime"));
                            arrayListConversation.add(conversation);
                        }

                        isMessageSent = true;
                        mChatAdapter.notifyDataSetChanged();
                        moveToEnd();

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(ChatActivity.this);
                        LoginShared.destroySessionTypePreference(ChatActivity.this);
                        LoginShared.setDeviceToken(ChatActivity.this, deviceToken);
                        Intent loginIntent = new Intent(ChatActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(ChatActivity.this, jsObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MethodUtils.errorMsg(ChatActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
            }
        });
    }

    @Override
    public void onScrollToTop(int scrollPosition) {
        //Calculate next pagination
        int div = arrayListConversation.size() / 25;
        int pagination = div * 25;
        Log.d("@@Scroll-Call : ", "\nIntegerDivision = " + div
                + "\npagination = " + pagination);
        //Call next pagination
        callChatListApi(receiver_id, pagination);
    }
}