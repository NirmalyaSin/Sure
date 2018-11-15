package com.rts.commonutils_2_0.deviceinfo;

import android.app.Activity;
import android.provider.Settings.Secure;
import android.util.Log;

public class AppDeviceIdDetection {

	/**
	 * Method: getAppId
	 * @param contex
	 * @return String
	 * Purpose: Fetching Android Device id
	 * Developer: Hari Narayan Jha
     * Date: 22-11-2014
	 */
    public String getAppId(Activity contex){
    	String android_id = Secure.getString(contex.getContentResolver(),
                Secure.ANDROID_ID);
    	Log.d("AppIdDetection", "AppId: " + android_id);
    	
    	return android_id;
    }

}
