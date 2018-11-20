package com.surefiz.networkutils;


import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static com.surefiz.apilist.ApiList.LOGIN;
import static com.surefiz.apilist.ApiList.REGISTRATION;
import static com.surefiz.apilist.ApiList.SENDOTP;
import static com.surefiz.apilist.ApiList.VIEWPROFILE;

public interface ApiInterface {

    @Multipart
    @POST(REGISTRATION)
    Call<ResponseBody> call_registrationImageApi(@Part("fullName") RequestBody fullName,
                                                 @Part("emailId") RequestBody emailId,
                                                 @Part("password") RequestBody password,
                                                 @Part("gender") RequestBody gender,
                                                 @Part("phoneNumber") RequestBody phoneNumber,
                                                 @Part("dob") RequestBody dob,
                                                 @Part("height") RequestBody height,
                                                 @Part("desiredWeight") RequestBody desiredWeight,
                                                 @Part("timeToloseWeight") RequestBody timeToloseWeight,
                                                 @Part("prefferedUnits") RequestBody prefferedUnits,
                                                 @Part("deviceType") RequestBody deviceType,
                                                 @Part("scaleMacId") RequestBody scaleMacId,
                                                 @Part MultipartBody.Part attachment);

    @Multipart
    @POST(REGISTRATION)
    Call<ResponseBody> call_registrationApi(@Part("fullName") RequestBody fullName,
                                            @Part("emailId") RequestBody emailId,
                                            @Part("password") RequestBody password,
                                            @Part("gender") RequestBody gender,
                                            @Part("phoneNumber") RequestBody phoneNumber,
                                            @Part("dob") RequestBody dob,
                                            @Part("height") RequestBody height,
                                            @Part("desiredWeight") RequestBody desiredWeight,
                                            @Part("timeToloseWeight") RequestBody timeToloseWeight,
                                            @Part("prefferedUnits") RequestBody prefferedUnits,
                                            @Part("scaleMacId") RequestBody scaleMacId,
                                            @Part("deviceType") RequestBody deviceType);
    @FormUrlEncoded
    @POST(SENDOTP)
    Call<ResponseBody> call_otpApi(@Header("Content-Type") String type,
                                   @Header("x-authorization") String token,
                                   @Field("userId") String userId,
                                   @Field("OTP") String OTP);

    @FormUrlEncoded
    @POST(LOGIN)
    Call<ResponseBody> call_loginApi(@Field("user_email") String user_email,
                                     @Field("user_password") String user_password);

    @FormUrlEncoded
    @POST(VIEWPROFILE)
    Call<ResponseBody> call_viewprofileApi(@Header("Content-Type") String type,
                                           @Header("x-authorization") String token,
                                           @Field("user_id") String user_id);
}
