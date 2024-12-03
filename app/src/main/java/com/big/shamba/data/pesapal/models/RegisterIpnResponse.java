package com.big.shamba.data.pesapal.models;

import com.google.gson.annotations.SerializedName;

public class RegisterIpnResponse {
    @SerializedName("url")
    private String url;

    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("ipn_id")
    private String ipnId;

    @SerializedName("notification_type")
    private int notificationType;

    @SerializedName("ipn_notification_type_description")
    private String ipnNotificationTypeDescription;

    @SerializedName("ipn_status")
    private int ipnStatus;

    @SerializedName("ipn_status_description")
    private String ipnStatusDescription;

    @SerializedName("error")
    private String error;

    @SerializedName("status")
    private String status;

    public String getUrl() {
        return url;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getIpnId() {
        return ipnId;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public String getIpnNotificationTypeDescription() {
        return ipnNotificationTypeDescription;
    }

    public int getIpnStatus() {
        return ipnStatus;
    }

    public String getIpnStatusDescription() {
        return ipnStatusDescription;
    }

    public String getError() {
        return error;
    }

    public String getStatus() {
        return status;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setIpnId(String ipnId) {
        this.ipnId = ipnId;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public void setIpnNotificationTypeDescription(String ipnNotificationTypeDescription) {
        this.ipnNotificationTypeDescription = ipnNotificationTypeDescription;
    }

    public void setIpnStatus(int ipnStatus) {
        this.ipnStatus = ipnStatus;
    }

    public void setIpnStatusDescription(String ipnStatusDescription) {
        this.ipnStatusDescription = ipnStatusDescription;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
