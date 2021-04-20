package com.surefiz.screens.groupinvite.adapter;

import android.app.Activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.surefiz.R;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.groupinvite.ModelGroupInvite;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.sharedhandler.LoginShared;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GroupInviteAdapter extends RecyclerView.Adapter<GroupInviteAdapter.UserListViewHolder> {
    Activity activity;
    int count;
    private ArrayList<ModelGroupInvite> groupInvites = new ArrayList<>();

    public GroupInviteAdapter(Activity activity, int count) {
        this.activity = activity;
        this.count = count;
        for (int i = 0; i < count; i++)
            groupInvites.add(new ModelGroupInvite());
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_registration_group, viewGroup, false);

        return new GroupInviteAdapter.UserListViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder userListViewHolder, final int i) {
        switch (i) {
            case 0:
                userListViewHolder.txt_register_group_header.setText("First User Details");
                break;
            case 1:
                userListViewHolder.txt_register_group_header.setText("Second User Details");
                break;
            case 2:
                userListViewHolder.txt_register_group_header.setText("Third User Details");
                break;
            case 3:
                userListViewHolder.txt_register_group_header.setText("Fourth User Details");
                break;
            default:
                userListViewHolder.txt_register_group_header.setText("User Details");
        }
        userListViewHolder.edt_group_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                groupInvites.get(i).setName(userListViewHolder.edt_group_name.getText().toString().trim());
            }
        });

        userListViewHolder.edt_group_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                groupInvites.get(i).setEmail(userListViewHolder.edt_group_email.getText().toString().trim());
            }
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {

        TextView txt_register_group_header;
        EditText edt_group_name, edt_group_email;

        public UserListViewHolder(@NonNull View itemView, Activity activity) {
            super(itemView);
            txt_register_group_header = itemView.findViewById(R.id.txt_register_group_header);
            edt_group_name = itemView.findViewById(R.id.edt_group_name);
            edt_group_email = itemView.findViewById(R.id.edt_group_email);
        }
    }

    public ArrayList<ModelGroupInvite> getData() {
        return groupInvites;
    }
}
