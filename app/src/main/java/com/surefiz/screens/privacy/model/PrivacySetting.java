package com.surefiz.screens.privacy.model;

import com.google.gson.annotations.SerializedName;

public class PrivacySetting {
    @SerializedName("privacyId")
    Integer privacyId;
    @SerializedName("privacyText")
    String privacyText;
    @SerializedName("privacyEnabled")
    String privacyEnabled;

    public PrivacySetting() {
    }

    public Integer getPrivacyId() {
        return privacyId;
    }

    public void setPrivacyId(Integer privacyId) {
        this.privacyId = privacyId;
    }

    public String getPrivacyText() {
        return privacyText;
    }

    public void setPrivacyText(String privacyText) {
        this.privacyText = privacyText;
    }

    public String getPrivacyEnabled() {
        return privacyEnabled;
    }

    public void setPrivacyEnabled(String privacyEnabled) {
        this.privacyEnabled = privacyEnabled;
    }

    @Override
    public String toString() {
        return "PrivacySetting{" +
                "privacyId=" + privacyId +
                ", privacyText='" + privacyText + '\'' +
                ", privacyEnabled='" + privacyEnabled + '\'' +
                '}';
    }
}
