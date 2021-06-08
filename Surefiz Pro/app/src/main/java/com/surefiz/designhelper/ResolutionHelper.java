package com.surefiz.designhelper;

import android.app.Activity;
import android.graphics.Typeface;

import com.rts.commonutils_2_0.deviceinfo.DeviceResolution;


public class ResolutionHelper extends DeviceResolution {

//    public Typeface typefaceRegular, typefaceSemiBold, typefaceBold, typeLight, typeThin,
//            typefaceBlack,typefaceBook, typefaceMedium,typefaceHeavy;
    public int globalTextSize, globalTextSizeRegistrationListing, globalAboutMetext,
            globalHeaderText, textSizeQuestion;

    public ResolutionHelper(Activity activity) {
        super(activity);

        initializeGlobalTextSize();
        initializeTypeface(activity);
    }

    private void initializeGlobalTextSize() {
        final double GLOBAL_TEXT_SIZE = 3.5;
        globalTextSize = (int) getTextSize(GLOBAL_TEXT_SIZE);

        final double GLOBAL_TEXT_SIZE_REGISTRATION_LISTING = 3;
        globalTextSizeRegistrationListing = (int) getTextSize(GLOBAL_TEXT_SIZE_REGISTRATION_LISTING);

        final double GLOBAL_TABOUT_ME_TEXT = 4;
        globalAboutMetext = (int) getTextSize(GLOBAL_TABOUT_ME_TEXT);

        final double GLOBAL_HEADER_TEXT = 5;
        globalHeaderText = (int) getTextSize(GLOBAL_HEADER_TEXT);

        final double TEXT_SIZE_QUESTION = 2.7;
        textSizeQuestion = (int) getTextSize(TEXT_SIZE_QUESTION);
    }

    private void initializeTypeface(Activity activity) {
//        typefaceRegular = Typeface.createFromAsset(activity.getAssets(), "font/Roboto-Regular.ttf");
//        typefaceSemiBold = Typeface.createFromAsset(activity.getAssets(), "font/Roboto-Medium.ttf");
//        typefaceBold = Typeface.createFromAsset(activity.getAssets(), "font/Roboto-Bold.ttf");
//        typeLight = Typeface.createFromAsset(activity.getAssets(), "font/Roboto-Light.ttf");
//        typeThin = Typeface.createFromAsset(activity.getAssets(), "font/Roboto-Thin.ttf");
//
//        typefaceBlack =Typeface.createFromAsset(activity.getAssets(), "font/AvenirBlack.ttf");
//        typefaceBook =Typeface.createFromAsset(activity.getAssets(), "font/AvenirBook.ttf");
//        typefaceMedium =Typeface.createFromAsset(activity.getAssets(), "font/Avenir-Medium-09.ttf");
//        typefaceHeavy =Typeface.createFromAsset(activity.getAssets(), "font/Avenir-Heavy-05.ttf");
    }
}
