package com.surefiz.dialog.universalpopup;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.surefiz.R;

import java.util.List;

public class UniversalPopup extends PopupWindow implements View.OnClickListener {
    public View popUpView;
    private Activity activity;
    private List<String> universalList;
    public EditText et;

    public UniversalPopup(Activity activity, List<String> universalList, EditText et) {
        super(activity);
        this.activity = activity;
        this.universalList = universalList;
        this.et = et;
        initXML(activity);
    }


    private void initXML(Activity activity) {
        popUpView = activity.getLayoutInflater().inflate(R.layout.popup_filter, null);
        setContentView(popUpView);
        UniversalPopUpResponsive filterPopUpResponsive = new UniversalPopUpResponsive(activity, popUpView, universalList, et, this);

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
