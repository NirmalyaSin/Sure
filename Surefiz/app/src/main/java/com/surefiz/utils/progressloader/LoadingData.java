package com.surefiz.utils.progressloader;

import android.app.Activity;

import com.surefiz.R;


public class LoadingData {
    private ProgressLoader progressLoader;
    private Activity activity;

    public LoadingData(Activity activity) {
        progressLoader = ProgressLoader.create(activity);
        progressLoader.setStyle(ProgressLoader.Style.SPIN_INDETERMINATE);
        progressLoader.setCancellable(false);
        progressLoader.setAnimationSpeed(2);
        this.activity = activity;
    }


    public void show() {

        if (progressLoader != null && !progressLoader.isShowing())
            progressLoader.setStyle(ProgressLoader.Style.SPIN_INDETERMINATE);
        progressLoader.setWindowColor(activity.getResources().getColor(R.color.kprogresshud_default_color));
            progressLoader.show();
    }

    public boolean isShowing() {
        if (progressLoader == null)
            return false;
        return progressLoader.isShowing();
    }

    public void show_with_label(String message) {

        if (progressLoader != null && !progressLoader.isShowing())
            progressLoader.setStyle(ProgressLoader.Style.SPIN_INDETERMINATE);
        progressLoader.setWindowColor(activity.getResources().getColor(R.color.kprogresshud_default_color));
        progressLoader.setLabel(message);
        progressLoader.show();
    }

    public void dismiss() {
        if (progressLoader != null && progressLoader.isShowing())
            progressLoader.dismiss();
    }
}