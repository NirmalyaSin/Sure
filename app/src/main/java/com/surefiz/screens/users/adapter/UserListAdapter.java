package com.surefiz.screens.users.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.users.model.UserList;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    Activity activity;
    List<UserList> userLists;
    private static DecimalFormat df2;

    public UserListAdapter(Activity activity, List<UserList> userLists) {
        this.activity = activity;
        this.userLists = userLists;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_list_item, viewGroup, false);

        return new UserListAdapter.UserListViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder userListViewHolder, int i) {
        df2 = new DecimalFormat(".##");
        userListViewHolder.tv_name.setText("Name:   " + userLists.get(i).getUserName());
        if (userLists.get(i).getUserWeight().equals("0")) {
            userListViewHolder.tv_weight.setText("Weight:  " + "0" + " lbs");
        } else {
            userListViewHolder.tv_weight.setText("Weight:  " + df2.format((Double.parseDouble
                    (userLists.get(i).getUserWeight()) * 2.20462)) + " lbs");
        }

    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_weight;
        ImageView iv_separature;

        public UserListViewHolder(@NonNull View itemView, Activity activity) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_weight = itemView.findViewById(R.id.tv_weight);
            iv_separature = itemView.findViewById(R.id.iv_separature);
        }

    }
}
