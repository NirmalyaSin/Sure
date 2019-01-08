package com.surefiz.dialog.weightpopup;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.surefiz.R;
import com.surefiz.interfaces.OnWeightCallback;

import java.util.List;

public class WeigtUniversalPopup extends PopupWindow implements View.OnClickListener {
    public View popUpView;
    private Activity activity;
    private List<String> universalList;
    public EditText et;
    public OnWeightCallback onWeightCallback;

    public WeigtUniversalPopup(Activity activity, List<String> universalList, EditText et, OnWeightCallback onWeightCallback) {
        super(activity);
        this.activity = activity;
        this.universalList = universalList;
        this.onWeightCallback=onWeightCallback;
        this.et = et;
        initXML(activity);
    }

    private void initXML(Activity activity) {
        popUpView = activity.getLayoutInflater().inflate(R.layout.popup_filter, null);
        setContentView(popUpView);
        WeightUniversalPopUpResponsive filterPopUpResponsive = new WeightUniversalPopUpResponsive(
                activity, popUpView, universalList, et, this,onWeightCallback);

        setWidth(filterPopUpResponsive.popUpWidth);
        if (universalList.size() > 5) {
            setHeight(filterPopUpResponsive.popUpHeight * 3);
        } else {
            setHeight(filterPopUpResponsive.popUpHeightWrap);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
