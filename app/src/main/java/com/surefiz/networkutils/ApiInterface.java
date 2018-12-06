package com.surefiz.networkutils;


import com.surefiz.screens.accountability.models.CircleUserResponse;
import com.surefiz.screens.users.model.UserListModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

import static com.surefiz.apilist.ApiList.ADDUSER;
import static com.surefiz.apilist.ApiList.DASHBOARD;
import static com.surefiz.apilist.ApiList.API_CIRCLE_SEARCH_USER_LIST;
import static com.surefiz.apilist.ApiList.API_CIRCLE_USER_LIST;
import static com.surefiz.apilist.ApiList.EDITPROFILE;
import static com.surefiz.apilist.ApiList.FORGOTPASSWORD;
import static com.surefiz.apilist.ApiList.LOGIN;
import static com.surefiz.apilist.ApiList.LOGOUT;
import static com.surefiz.apilist.ApiList.REGISTRATION;
import static com.surefiz.apilist.ApiList.SENDOTP;
import static com.surefiz.apilist.ApiList.USERLIST;
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
                                                 @Part("device_Token") RequestBody device_Token,
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
                                            @Part("deviceType") RequestBody deviceType,
                                            @Part("device_Token") RequestBody device_Token);

    @FormUrlEncoded
    @POST(SENDOTP)
    Call<ResponseBody> call_otpApi(@Header("x-authorization") String token,
                                   @Field("userId") String userId,
                                   @Field("OTP") String OTP);

    @FormUrlEncoded
    @POST(LOGIN)
    Call<ResponseBody> call_loginApi(@Field("user_email") String user_email,
                                     @Field("user_password") String user_password,
                                     @Field("deviceType") String deviceType,
                                     @Field("device_Token") String device_Token);

    @FormUrlEncoded
    @POST(VIEWPROFILE)
    Call<ResponseBody> call_viewprofileApi(@Header("x-authorization") String token,
                                           @Field("user_id") String user_id);

    @Multipart
    @POST(EDITPROFILE)
    Call<ResponseBody> call_editprofileImageApi(@Header("x-authorization") String token,
                                                @Part("current_UserId") RequestBody current_UserId,
                                                @Part("fullName") RequestBody fullName,
                                                @Part("gender") RequestBody gender,
                                                @Part("phoneNumber") RequestBody phoneNumber,
                                                @Part("dob") RequestBody dob,
                                                @Part("prefferedUnits") RequestBody prefferedUnits,
                                                @Part("deviceType") RequestBody deviceType,
                                                @Part MultipartBody.Part attachment);

    @Multipart
    @POST(EDITPROFILE)
    Call<ResponseBody> call_editprofileApi(@Header("x-authorization") String token,
                                           @Part("current_UserId") RequestBody current_UserId,
                                           @Part("fullName") RequestBody fullName,
                                           @Part("gender") RequestBody gender,
                                           @Part("phoneNumber") RequestBody phoneNumber,
                                           @Part("dob") RequestBody dob,
                                           @Part("prefferedUnits") RequestBody prefferedUnits,
                                           @Part("deviceType") RequestBody deviceType);

    @FormUrlEncoded
    @POST(USERLIST)
    Call<UserListModel> call_userListApi(@Header("x-authorization") String token,
                                         @Field("userId") String userId);

    @GET(API_CIRCLE_SEARCH_USER_LIST + "/{keyword}")
    Call<CircleUserResponse> call_SearchCircleUserListApi(@Header("x-authorization") String token,
                                                    @Path("keyword") String keyword);

    @FormUrlEncoded
    @POST(API_CIRCLE_USER_LIST)
    Call<CircleUserResponse> call_CircleUserListApi(@Header("x-authorization") String token,
                                                    @Field("userId") String userId);

    @FormUrlEncoded
    @POST(ADDUSER)
    Call<ResponseBody> call_adduserApi(@Header("x-authorization") String token,
                                       @Field("current_UserId") String current_UserId,
                                       @Field("added_DeviceId") String added_DeviceId,
                                       @Field("fullName") String fullName,
                                       @Field("emailId") String emailId,
                                       @Field("requredTime") String requredTime,
                                       @Field("currentHeight") String currentHeight,
                                       @Field("targetWeight") String targetWeight,
                                       @Field("password") String password,
                                       @Field("gender") String Gender,
                                       @Field("phoneNumber") String phoneNumber,
                                       @Field("dob") String dob,
                                       @Field("deviceType") String deviceType,
                                       @Field("prefferedUnits") String prefferedUnits);

    @FormUrlEncoded
    @POST(FORGOTPASSWORD)
    Call<ResponseBody> call_forgotApi(@Field("user_email") String user_email);

    @FormUrlEncoded
    @POST(LOGOUT)
    Call<ResponseBody> call_logoutApi(@Header("x-authorization") String token,
                                      @Field("userId") String userId,
                                      @Field("userToken") String userToken);

    @FormUrlEncoded
    @POST(DASHBOARD)
    Call<ResponseBody> call_dashboardApi(@Header("x-authorization") String token,
                                         @Field("userId") String userId);
}
