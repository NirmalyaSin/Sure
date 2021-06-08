package com.surefiz.screens.acountabiltySearch;

public interface RequestState {
    //Result States
    String STATUS_IDLE = "Idle State";
    String STATUS_REQUEST_SENT = "Request Sent";
    String STATUS_REQUEST_ACCEPTED = "Request Accepted";

    //Send request states
    String REQUEST_STATUS_SEND = "Send Request";
    String REQUEST_STATUS_CANCEL = "Cancel Request";
    String RESPONSE_TYPE_REQUESTED = "Requested";
    String RESPONSE_TYPE_CANCELED = "Canceled";
    String RESPONSE_TYPE_NONE = "";


    //Accept or Reject friend Request
    String SEND_ACCEPT_FRIEND_REQUEST = "Accept Request";
    String SEND_REJECT_FRIEND_REQUEST = "Reject Request";
}
