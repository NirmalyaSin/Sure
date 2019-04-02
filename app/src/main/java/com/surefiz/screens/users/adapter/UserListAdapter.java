package com.surefiz.screens.users.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.interfaces.OnUiEventClick;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.progressstatus.ProgressStatusActivity;
import com.surefiz.screens.users.model.UserListItem;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.sharedhandler.LoginShared;

import java.text.DecimalFormat;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    Activity activity;
    List<UserListItem> userLists;
    private static DecimalFormat df2;
    private int row_index = -1;
    private OnUiEventClick onUiEventClick;

    public UserListAdapter(Activity activity, List<UserListItem> userLists,
                           OnUiEventClick onUiEventClick) {
        this.activity = activity;
        this.userLists = userLists;
        this.onUiEventClick = onUiEventClick;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_list_item, viewGroup, false);

        return new UserListAdapter.UserListViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder userListViewHolder, int i) {
        df2 = new DecimalFormat(".##");
        if (userLists.get(i).getUserName() == null || userLists.get(i).getUserName().equals("") ||
                userLists.get(i).getUserName().equalsIgnoreCase("null") ||
                userLists.get(i).getUserName().isEmpty()) {
            userListViewHolder.tv_name.setText("No Name");
        } else {
            userListViewHolder.tv_name.setText("Name:   " + userLists.get(i).getUserName());
        }

        if (userLists.get(i).getUserWeight() == null || userLists.get(i).getUserWeight().equals("") ||
                userLists.get(i).getUserWeight().equalsIgnoreCase("null") ||
                userLists.get(i).getUserName().isEmpty()) {
            userListViewHolder.tv_weight.setText("No Weight");
        } else {
            userListViewHolder.tv_weight.setText("Weight:   " + userLists.get(i).getUserWeight());
        }

        if (userLists.get(i).getIsUserHaveCompleteInfo() == 0) {
            userListViewHolder.tv_status.setText("Profile");
        } else {
            userListViewHolder.tv_status.setText("Weight:   " + userLists.get(i).getUserWeight());
        }

        userListViewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Delete user Confirmation")
                        .setMessage("Do you want to delete " + userLists.get(i).getUserName() + "?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("user", userLists.get(i));
                                intent.putExtra("bundle", bundle);
                                onUiEventClick.onUiClick(intent, 1);
                            }

                        }).setNegativeButton("No!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        /*if (userLists.get(i).getUserWeight().equals("0")) {
            userListViewHolder.tv_weight.setText("Weight:  " + "0" + " lbs");
        } else {
            userListViewHolder.tv_weight.setText("Weight:  " + df2.format((Double.parseDouble
                    (userLists.get(i).getUserWeight()) / 100) * 2.20462) + " lbs");
        }*/

        userListViewHolder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = i;
                if (LoginShared.getDashboardPageFrom(activity).equals("0")) {
                    userListViewHolder.rl_main.setBackgroundColor(Color.parseColor("#D8D8D8"));
                    Intent intent = new Intent(activity, DashBoardActivity.class);
                    intent.putExtra("id", "" + userLists.get(i).getServerUserId());
                    intent.putExtra("page", "0");
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    activity.finish();
                } else {
                    userListViewHolder.rl_main.setBackgroundColor(Color.parseColor("#D8D8D8"));
                    LoginShared.setScaleUserId(userLists.get(i).getScaleUserId());
                    LoginShared.setWeightPageFrom(activity, "2");
                    Intent loginIntent = new Intent(activity, WeightDetailsActivity.class);
                    activity.startActivity(loginIntent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    activity.finish();
                }

                notifyDataSetChanged();
            }
        });

        if (row_index == i) {
            userListViewHolder.rl_main.setBackgroundColor(Color.parseColor("#D8D8D8"));
        } else {
            userListViewHolder.rl_main.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_weight, tv_status;
        ImageView iv_separature;
        RelativeLayout rl_main;
        Button btn_delete;

        public UserListViewHolder(@NonNull View itemView, Activity activity) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_weight = itemView.findViewById(R.id.tv_weight);
            iv_separature = itemView.findViewById(R.id.iv_separature);
            rl_main = itemView.findViewById(R.id.rl_main);
            tv_status = itemView.findViewById(R.id.tv_status);
        }

    }
}
