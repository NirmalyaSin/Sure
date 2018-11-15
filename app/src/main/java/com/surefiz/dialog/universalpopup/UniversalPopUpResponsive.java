package com.surefiz.dialog.universalpopup;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.designhelper.ResolutionHelper;
import com.surefiz.registration.RegistrationActivity;

import java.util.List;

public class UniversalPopUpResponsive extends ResolutionHelper {
    private View view;
    private RegistrationActivity activity;

    private LinearLayout llAddView;
    public int popUpWidth, popUpHeight, popUpHeightWrap;
    private TextView et;
    private PopupWindow popupWindow;

    public UniversalPopUpResponsive(RegistrationActivity activity, View popUpView, List<String> universalList,
                                    EditText et, PopupWindow universalPopup) {
        super(activity);
        this.view = popUpView;
        this.activity = activity;
        this.et = et;
        this.popupWindow = universalPopup;

        initView();
        initChild(universalList);
        setSize();
        setTextSize();
        setFonts(activity);
    }

    private void initView() {

        llAddView = view.findViewById(R.id.llAddView);
    }

    private void initChild(List<String> genderList) {

        for (int i = 0; i < genderList.size(); i++) {

            final TextView tv = new TextView(activity);
            tv.setText(genderList.get(i));
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getHeight(0.07));
            tv.setLayoutParams(llParams);

            if (i != genderList.size() - 1)
                llParams.setMargins(0, 0, 0, getHeight(0.0016));

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et.setText(tv.getText().toString());
                    popupWindow.dismiss();
                }
            });

            llAddView.addView(tv);

        }

    }

    private void setSize() {
        popUpWidth = getWidth(0.8);
        popUpHeight = getHeight(0.094);
        popUpHeightWrap = ViewGroup.LayoutParams.WRAP_CONTENT;

    }

    private void setTextSize() {

    }

    private void setFonts(Activity act) {

    }
}
