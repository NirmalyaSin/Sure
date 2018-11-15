package com.rts.commonutils_2_0.deviceinfo;

/**
 * Developer: Hari Narayan Jha
 * Date: 04.02.2015
 * Copyright by RTS
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

/**
 * <p>Class: StatusBarConfig</p>
 * Purpose: To implement different conditions based on device status bar configuration.
 */
public class StatusBarConfig {
	
	private Context context;

	public StatusBarConfig(Context context){
		this.context = context;
	}
	
	/**Get Height of Status Bar of the Device*/
	public int getStatusBarHeight() {
	    int result = 0;
	    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
	    if (resourceId > 0) {
	        result = context.getResources().getDimensionPixelSize(resourceId);
	    }
	    Log.d("Like Activity", "Statusbar Height: " + result);
	    
	    return result;
	}


	/**Check for LOLLIPOP or Above Version*/
	public static boolean isLollipopOrAboveVersion(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return true;
		} else {
			return false;
		}
	}
	/**Check for KitKat or Above Version*/
	public static boolean isKitKatOrAboveVersion(){
		if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
			return true;
		}
		return false;
	}
	/**Check for HONEYCOMB or Above Version*/
	public static boolean isHoneycomOrAboveVersion(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Use for status color
	 * @param color
	 * @return
	 */
	public static int darkenColor(int color) {
		/* if(color == primaryColor)
	            return primaryDarkColor;*/

		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		return Color.HSVToColor(hsv);
	}
}
