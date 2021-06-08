package com.surefiz.utils.progressloader;

import android.content.Context;

class Helper {

    private static float scale;

    static int dpToPixel(float dp, Context context) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * scale);
    }
}
