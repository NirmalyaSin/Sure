package com.surefiz.screens.dashboard.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.surefiz.R;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.users.model.UserListItem;
import com.surefiz.sharedhandler.LoginShared;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder> {
    DashBoardActivity activity;
    List<UserListItem> userLists;
    private ImageLoader imageLoader;
    private OnCircleViewClickListener mOnCircleViewClickListener;
    private int row_index = -1;
    private int row_user = -1;
    private int row_other = -1;

    public ContactListAdapter(DashBoardActivity activity, List<UserListItem> userLists,
                              OnCircleViewClickListener mOnCircleViewClickListener, int row_user) {
        this.activity = activity;
        this.userLists = userLists;
        this.mOnCircleViewClickListener = mOnCircleViewClickListener;
        this.row_user = row_user;
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
                    equals("" + userLists.get(i).getServerUserId())) {
                row_index = i;
                row_user = 1;
            } else {
                System.out.print("SureFiz");
            }
        }

        if (row_other != 1) {
            if (activity.id.equals("" + userLists.get(i).getServerUserId())) {
                row_index = i;
                row_user = 1;
                row_other = 1;
            } else {
                System.out.print("SureFiz");
            }
        }
        /*if (activity.id.equals(userLists.get(i).getServerUserId().toString())) {
            row_index = i;
        } else {
            System.out.print("SureFiz");
        }*/


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


        try {
            if (!userLists.get(i).getUser_image().equals("")) {
                imageLoader.displayImage(userLists.get(i).getUser_image(), contactListViewHolder.profile_image);
            } else {
                contactListViewHolder.profile_image.setImageResource
                        (R.drawable.prof_img_placeholder);
            }
        } catch (Exception e) {
            e.printStackTrace();
            contactListViewHolder.profile_image.setImageResource
                    (R.drawable.prof_img_placeholder);
        }


        /*if (LoginShared.getUserPhoto(activity).equals("") || LoginShared.getUserPhoto(activity) == null ||
                LoginShared.getUserPhoto(activity).equalsIgnoreCase("null") ||
                LoginShared.getUserPhoto(activity).isEmpty()) {
            contactListViewHolder.profile_image.setImageResource
                    (R.drawable.prof_img_placeholder);
        } else {

            if (LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId().equals(""+userLists.get(i).getServerUserId())) {
                //imageLoader.displayImage(LoginShared.getUserPhoto(activity), contactListViewHolder.profile_image);
                imageLoader.displayImage(userLists.get(i).getUser_image(), contactListViewHolder.profile_image);
            } else {
                contactListViewHolder.profile_image.setImageResource
                        (R.drawable.prof_img_placeholder);
            }
        }*/

        if (userLists.get(i).getUserName() == null || userLists.get(i).getUserName().equals("") || userLists.get(i).getUserName().equalsIgnoreCase("null") || userLists.get(i).getUserName().isEmpty()) {
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

    public interface OnCircleViewClickListener {
        void onViewClick(int position);
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
}
