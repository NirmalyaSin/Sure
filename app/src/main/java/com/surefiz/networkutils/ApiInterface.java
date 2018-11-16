package com.surefiz.networkutils;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static com.surefiz.apilist.ApiList.REGISTRATION;

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
}
