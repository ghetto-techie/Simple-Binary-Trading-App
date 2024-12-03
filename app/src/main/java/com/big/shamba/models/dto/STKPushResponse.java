package com.big.shamba.models.dto;

public class STKPushResponse {
    private String MerchantRequestID;
    private String CheckoutRequestID;
    private String ResponseCode;
    private String ResponseDescription;
    private String CustomerMessage;

    public STKPushResponse(String merchantRequestID, String checkoutRequestID, String responseCode, String responseDescription, String customerMessage) {
        MerchantRequestID = merchantRequestID;
        CheckoutRequestID = checkoutRequestID;
        ResponseCode = responseCode;
        ResponseDescription = responseDescription;
        CustomerMessage = customerMessage;
    }

    public STKPushResponse() {
    }

    public String getMerchantRequestID() {
        return MerchantRequestID;
    }

    public void setMerchantRequestID(String merchantRequestID) {
        MerchantRequestID = merchantRequestID;
    }

    public String getCheckoutRequestID() {
        return CheckoutRequestID;
    }

    public void setCheckoutRequestID(String checkoutRequestID) {
        CheckoutRequestID = checkoutRequestID;
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public String getResponseDescription() {
        return ResponseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        ResponseDescription = responseDescription;
    }

    public String getCustomerMessage() {
        return CustomerMessage;
    }

    public void setCustomerMessage(String customerMessage) {
        CustomerMessage = customerMessage;
    }

    @Override
    public String toString() {
        return "STKPushResponse{" +
                "MerchantRequestID='" + MerchantRequestID + '\'' +
                ", CheckoutRequestID='" + CheckoutRequestID + '\'' +
                ", ResponseCode='" + ResponseCode + '\'' +
                ", ResponseDescription='" + ResponseDescription + '\'' +
                ", CustomerMessage='" + CustomerMessage + '\'' +
                '}';
    }
}
