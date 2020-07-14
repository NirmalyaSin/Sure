package com.surefiz.sharedhandler;

import android.content.Context;
import android.content.SharedPreferences;

public class InstructionSharedPreference {
    public static final String PREF_NAME = "InstructionSharedPreference";


    SharedPreferences mPref;
    SharedPreferences.Editor editor;

    public InstructionSharedPreference(Context mContext) {
        mPref = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mPref.edit();
    }

    public void setInstructionVisibility(Context context, String userId) {

        SharedPreferences.Editor editor = mPref.edit();
        String newStr = getInstructionVisibility(context);
        System.out.println("PrefUserId: " + newStr);

        if (!newStr.contains(userId + ",")) {
            //newStr = newStr.equalsIgnoreCase("") ? newStr + userId + "," : "";
            /*if (newStr.equalsIgnoreCase("")) {
                newStr = newStr + userId + ",";
            } else {
                newStr = "";
            }*/

            newStr = newStr + userId + ",";
        }
        editor.putString(SharedUtils.KEY_SHARED_IS_INSTRUCTION, newStr);
        editor.commit();
    }

    public String getInstructionVisibility(Context context) {
        return mPref.getString(SharedUtils.KEY_SHARED_IS_INSTRUCTION, "");
    }

    public boolean isInstructionShown(Context context, String userId) {
        String newStr = getInstructionVisibility(context);
        return newStr.contains(userId + ",");

    }

    public void setInstruction( boolean isFirst) {
        SharedPreferences.Editor editor = mPref.edit();

        editor.putBoolean(SharedUtils.ISFIRST,isFirst);
        editor.commit();
    }

    public boolean isInstruction() {
        return mPref.getBoolean(SharedUtils.ISFIRST, false);

    }

    public void setAnroidQ( boolean isFirst) {
        SharedPreferences.Editor editor = mPref.edit();

        editor.putBoolean(SharedUtils.ISFIRST,isFirst);
        editor.commit();
    }

    public boolean getAnroidQ() {
        return mPref.getBoolean(SharedUtils.ISFIRST, false);

    }

    public void setFirstText( boolean isFirst) {
        SharedPreferences.Editor editor = mPref.edit();

        editor.putBoolean(SharedUtils.ISFIRSTTEXT,isFirst);
        editor.commit();
    }

    public boolean getFirstText() {
        return mPref.getBoolean(SharedUtils.ISFIRSTTEXT, false);

    }

}
