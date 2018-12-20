package com.surefiz.networkutils;


import com.surefiz.screens.accountability.models.CircleUserResponse;
import com.surefiz.screens.acountabiltySearch.models.AddToCircleResponse;
import com.surefiz.screens.bmidetails.model.BMIResponse;
import com.surefiz.screens.chat.model.ChatListResponse;
import com.surefiz.screens.dashboard.contactmodel.ContactListModel;
import com.surefiz.screens.notifications.models.NotificationsResponse;
import com.surefiz.screens.privacy.model.PrivacyListResponse;
import com.surefiz.screens.reminders.model.ReminderListResponse;
import com.surefiz.screens.users.model.UserListModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

import static com.surefiz.apilist.ApiList.ADDDEVICE;
import static com.surefiz.apilist.ApiList.ADDUSER;
import static com.surefiz.apilist.ApiList.API_ADD_EDIT_REMINDER_LIST;
import static com.surefiz.apilist.ApiList.API_BMI_DATA;
import static com.surefiz.apilist.ApiList.API_CIRCLE_ACCEPT_REJECT_REQUEST;
import static com.surefiz.apilist.ApiList.API_CIRCLE_SEARCH_USER_LIST;
import static com.surefiz.apilist.ApiList.API_CIRCLE_SEND_CANCEL_REQUEST;
import static com.surefiz.apilist.ApiList.API_CIRCLE_USER_LIST;
import static com.surefiz.apilist.ApiList.API_CONVERSATION_LIST;
import static com.surefiz.apilist.ApiList.API_GET_PRIVACY_LIST;
import static com.surefiz.apilist.ApiList.API_GET_REMINDER_LIST;
import static com.surefiz.apilist.ApiList.API_NOTIFICATION_LIST;
import static com.surefiz.apilist.ApiList.BOARDCAST;
import static com.surefiz.apilist.ApiList.CHANGEPASSWORD;
import static com.surefiz.apilist.ApiList.API_SEND_CHAT;
import static com.surefiz.apilist.ApiList.API_UPDATE_PRIVACY_LIST;
import static com.surefiz.apilist.ApiList.CONTACTLIST;
import static com.surefiz.apilist.ApiList.DASHBOARD;
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

    @FormUrlEncoded
    @POST(API_CIRCLE_SEND_CANCEL_REQUEST)
    Call<AddToCircleResponse> call_AddToCircleUserApi(@Header("x-authorization") String token,
                                                      @Field("sender") String sender,
                                                      @Field("receiver") String receiver,
                                                      @Field("type") String type);

    @FormUrlEncoded
    @POST(API_CIRCLE_ACCEPT_REJECT_REQUEST)
    Call<NotificationsResponse> call_AcceptRejectFriendRequestApi(@Header("x-authorization") String token,
                                                      @Field("userId") String userId,
                                                      @Field("requestId") String requestId,
                                                      @Field("type") String type);

    @GET(API_CIRCLE_SEARCH_USER_LIST + "/{keyword}")
    Call<CircleUserResponse> call_SearchCircleUserListApi(@Header("x-authorization") String token,
                                                          @Path("keyword") String keyword);

    @FormUrlEncoded
    @POST(API_CIRCLE_USER_LIST)
    Call<CircleUserResponse> call_CircleUserListApi(@Header("x-authorization") String token,
                                                    @Field("userId") String userId);

    @FormUrlEncoded
    @POST(API_NOTIFICATION_LIST)
    Call<NotificationsResponse> call_NotificationListApi(@Header("x-authorization") String token,
                                                         @Field("userId") String userId);

    @FormUrlEncoded
    @POST(API_CONVERSATION_LIST)
    Call<ChatListResponse> call_ConversationListApi(@Header("x-authorization") String token,
                                                    @Field("senderId") String senderId,
                                                    @Field("receiverId") String receiverId,
                                                    @Field("pagination") String pagination);

    @FormUrlEncoded
    @POST(API_BMI_DATA)
    Call<BMIResponse> call_BMIDetailsApi(@Header("x-authorization") String token,
                                         @Field("serverUserId") String serverUserId,
                                         @Field("scaleUserId") String scaleUserId);

    @FormUrlEncoded
    @POST(API_SEND_CHAT)
    Call<ChatListResponse> call_SendChatApi(@Header("x-authorization") String token,
                                                    @Field("senderId") String senderId,
                                                    @Field("receiverId") String receiverId,
                                                    @Field("chatmessage") String chatmessage);

    @FormUrlEncoded
    @POST(API_GET_PRIVACY_LIST)
    Call<PrivacyListResponse> call_PrivacyListApi(@Header("x-authorization") String token,
                                                  @Field("senderId") String senderId);

    @FormUrlEncoded
    @POST(API_GET_REMINDER_LIST)
    Call<ReminderListResponse> call_ReminderListApi(@Header("x-authorization") String token,
                                                    @Field("userId") String userId);

    @FormUrlEncoded
    @POST(API_ADD_EDIT_REMINDER_LIST)
    Call<ReminderListResponse> call_AddUpdateReminderApi(@Header("x-authorization") String token,
                                                    @Field("userId") String userId,
                                                    @Field("reminderText") String reminderText,
                                                    @Field("dateTime") String dateTime,
                                                    @Field("type") String type,
                                                    @Field("Id") String Id);
    @FormUrlEncoded
    @POST(API_UPDATE_PRIVACY_LIST)
    Call<PrivacyListResponse> call_UpdatePrivacyList(@Header("x-authorization") String token,
                                                  @Field("senderId") String senderId,
                                                  @Field("selectedIds") String selectedIds,
                                                  @Field("removedId") String removedId);

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

    @FormUrlEncoded
    @POST(ADDDEVICE)
    Call<ResponseBody> call_addDeviceApi(@Header("x-authorization") String token,
                                         @Field("senderId") String senderId,
                                         @Field("deviceId") String deviceId);

    @FormUrlEncoded
    @POST(CONTACTLIST)
    Call<ContactListModel> call_contactListApi(@Header("x-authorization") String token,
                                               @Field("userId") String userId);

    @FormUrlEncoded
    @POST(BOARDCAST)
    Call<ResponseBody> call_boardcastApi(@Header("x-authorization") String token,
                                         @Field("senderId") String userId,
                                         @Field("message") String message);

    @FormUrlEncoded
    @POST(CHANGEPASSWORD)
    Call<ResponseBody> call_changePasswordApi(@Header("x-authorization") String token,
                                              @Field("userId") String userId,
                                              @Field("oldPassword") String oldPassword,
                                              @Field("newPassword") String newPassword);
}
