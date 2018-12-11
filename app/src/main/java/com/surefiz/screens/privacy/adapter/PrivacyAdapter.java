package com.surefiz.screens.privacy.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.screens.privacy.model.PrivacySetting;

import java.util.ArrayList;

public class PrivacyAdapter extends RecyclerView.Adapter<PrivacyAdapter.PrivacyViewHolder> {

    private Context mContext;
    private ArrayList<PrivacySetting> arrayListPrivacy = new ArrayList<PrivacySetting>();
    private OnPrivacyListener mOnPrivacyListener;

    public PrivacyAdapter(Context context, ArrayList<PrivacySetting> privacy,
                          OnPrivacyListener clickListener) {
        this.mContext = context;
        this.arrayListPrivacy = privacy;
        this.mOnPrivacyListener = clickListener;
    }

    @NonNull
    @Override
    public PrivacyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_privacy, viewGroup, false);

        return new PrivacyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivacyViewHolder holder, int position) {
    //    Log.e("@@holder: ", arrayListPrivacy.get(position).toString());
        holder.textPrivacyOption.setText(arrayListPrivacy.get(position).getPrivacyText());

        if(arrayListPrivacy.get(position).getPrivacyEnabled().equals("1")){
            holder.imagePrivacySelect.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.ic_radio_circle_checked));
        }else {
            holder.imagePrivacySelect.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.ic_radio_button));
        }
    }

    @Override
    public int getItemCount() {
        return arrayListPrivacy.size();
    }

    public class PrivacyViewHolder extends RecyclerView.ViewHolder {
        TextView textPrivacyOption;
        ImageView imagePrivacySelect;

        public PrivacyViewHolder(@NonNull View itemView) {
            super(itemView);
            textPrivacyOption = itemView.findViewById(R.id.textPrivacyOption);
            imagePrivacySelect = itemView.findViewById(R.id.imagePrivacySelect);

            imagePrivacySelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(arrayListPrivacy.get(getAdapterPosition()).getPrivacyEnabled().equals("1")){
                        //Un-Check the option
                        mOnPrivacyListener.onUnSelectd(getAdapterPosition());
                    }else {
                        //Check the option
                        mOnPrivacyListener.onSelected(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnPrivacyListener{
        void onSelected(int position);
        void onUnSelectd(int position);
    }
}
