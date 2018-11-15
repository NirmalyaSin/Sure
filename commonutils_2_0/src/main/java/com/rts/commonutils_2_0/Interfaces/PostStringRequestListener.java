package com.rts.commonutils_2_0.Interfaces;


public interface PostStringRequestListener {
    public void onSuccess(String result, String type);
    public void onFailure(int responseCode, String responseMessage);


}
