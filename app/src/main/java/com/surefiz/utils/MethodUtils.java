package com.surefiz.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.surefiz.R;
import com.surefiz.dialog.ErrorMessageDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MethodUtils {

    public static void fullScreen(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void setStickyBar(Activity activity) {

        if (activity.getActionBar() != null && activity.getActionBar().isShowing())
            activity.getActionBar().hide();

        Window window = activity.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.statusBarColor));
    }

    public static void errorMsg(Context context, String msg) {
        ErrorMessageDialog.getInstant(context).show(msg);
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String profileDOB(String date) {
        String converted_date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date0 = sdf.parse(date);
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            converted_date = dateFormat.format(date0);
            System.out.println("Converted String: " + converted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return converted_date;
    }
    public static int randInt(int min, int max) {
//    public static int randInt() {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
//        int randomNum = (1000000 + rand.nextInt(9000000));
        return randomNum;
    }

    public static void showInfoDialog(Activity activity,String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getResources().getString(R.string.app_name_splash));
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> {
                    dialog.dismiss();
                });


        alertDialog.show();
    }
}
