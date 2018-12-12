package com.surefiz.screens.dashboard.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.surefiz.R;
import com.surefiz.screens.users.model.UserList;
import com.surefiz.sharedhandler.LoginShared;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder> {
    Activity activity;
    List<UserList> userLists;
    private ImageLoader imageLoader;
    private OnCircleViewClickListener mOnCircleViewClickListener;
    private int row_index = -1;
    private int row_user = -1;

    public ContactListAdapter(Activity activity, List<UserList> userLists,
                              OnCircleViewClickListener mOnCircleViewClickListener) {
        this.activity = activity;
        this.userLists = userLists;
        this.mOnCircleViewClickListener = mOnCircleViewClickListener;
        DisplayImageOptions opts = new DisplayImageOptions.Builder().cacheInMemory(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).defaultDisplayImageOptions(opts)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @NonNull
    @Override
    public ContactListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_contact_list, viewGroup, false);

        return new ContactListAdapter.ContactListViewHolder(itemView, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListViewHolder contactListViewHolder, int i) {

        if (row_user != 1) {
            if (LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId().
                    equals(userLists.get(i).getServerUserId().toString())) {
                row_index = i;
                row_user = 1;
            } else {
                System.out.print("SureFiz");
            }
        }

       /* if (userLists.get(i).getUserPhoto().equals("")) {
            contactListViewHolder.profile_image.setImageResource
                    (R.drawable.prof_img_placeholder);
        } else {
            String url = userLists.get(i).getUserPhoto();
            url = url.replace(" ", "20%");
            imageLoader.displayImage(url, contactListViewHolder.profile_image);
        }

        if (userLists.get(i).getOnlineStatus().equals("1")) {
            contactListViewHolder.iv_online.setVisibility(View.GONE);
        } else {
            contactListViewHolder.iv_online.setVisibility(View.GONE);
        }*/

        if (userLists.get(i).getUserName() == null || userLists.get(i).getUserName().equals("") ||
                userLists.get(i).getUserName().equalsIgnoreCase("null") ||
                userLists.get(i).getUserName().isEmpty()) {
            contactListViewHolder.tv_name.setText("No Name");
        } else {
            contactListViewHolder.tv_name.setText(userLists.get(i).getUserName());
        }

        contactListViewHolder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = i;
                mOnCircleViewClickListener.onViewClick(i);
                notifyDataSetChanged();
            }
        });

        if (row_index == i) {
            contactListViewHolder.view1.setBackgroundResource(R.drawable.login_edit_rounded_corner);
        } else {
            contactListViewHolder.view1.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }

    public class ContactListViewHolder extends RecyclerView.ViewHolder {
        de.hdodenhof.circleimageview.CircleImageView profile_image;
        ImageView iv_online;
        TextView tv_name;
        RelativeLayout rl_main;
        RelativeLayout view1;

        public ContactListViewHolder(@NonNull View itemView, Activity activity) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            iv_online = itemView.findViewById(R.id.iv_online);
            tv_name = itemView.findViewById(R.id.tv_name);
            rl_main = itemView.findViewById(R.id.rl_main);
            view1 = itemView.findViewById(R.id.view1);
        }
    }

    public interface OnCircleViewClickListener {
        void onViewClick(int position);
    }
}
