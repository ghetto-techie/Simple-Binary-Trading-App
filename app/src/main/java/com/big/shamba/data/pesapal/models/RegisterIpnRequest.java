package com.big.shamba.data.pesapal.models;

import com.google.gson.annotations.SerializedName;

public class RegisterIpnRequest {
    @SerializedName("url")
    private String url;

    @SerializedName("ipn_notification_type")
    private String ipnNotificationType;

    public RegisterIpnRequest(String url, String ipnNotificationType) {
        this.url = url;
        this.ipnNotificationType = ipnNotificationType;
    }

    public String getUrl() {
        return url;
    }

    public String getIpnNotificationType() {
        return ipnNotificationType;
    }
}
