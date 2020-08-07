package com.surefiz.application;

import android.app.Application;
import android.content.Context;

import com.surefiz.screens.boardcast.model.BroadcastItem;
import com.surefiz.screens.chat.model.Conversation;

import java.util.ArrayList;
import java.util.List;

public class MyApplicationClass extends Application {


    private static Context context;
    public List<Conversation> chatListNotification = new ArrayList<>();
    public List<BroadcastItem> broadcastItemsNotification = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        MyApplicationClass.context = getApplicationContext();

    }

    public static Context getAppContext() {
        return MyApplicationClass.context;
    }
}


