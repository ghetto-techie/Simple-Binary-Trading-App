package com.big.shamba.models.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class STKPushRequest {
    private String BusinessShortCode;
    private String Password;
    private String Timestamp;
    private String TransactionType;
    private String Amount;
    private String PartyA;
    private String PartyB;
    private String PhoneNumber;
    private String CallBackURL;
    private String AccountReference;
    private String TransactionDesc;

    public STKPushRequest(String businessShortCode, String password, String amount, String phoneNumber, String partyA, String partyB, String callBackURL, String accountReference, String transactionDesc) {
        this.BusinessShortCode = businessShortCode;
        this.Password = password;
        this.Timestamp = generateTimestamp();
        this.TransactionType = "CustomerPayBillOnline";
        this.Amount = amount;
        this.PartyA = partyA;
        this.PartyB = partyB;
        this.PhoneNumber = phoneNumber;
        this.CallBackURL = callBackURL;
        this.AccountReference = accountReference;
        this.TransactionDesc = transactionDesc;
    }

    private String generateTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getBusinessShortCode() {
        return BusinessShortCode;
    }

    public void setBusinessShortCode(String businessShortCode) {
        BusinessShortCode = businessShortCode;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getPartyA() {
        return PartyA;
    }

    public void setPartyA(String partyA) {
        PartyA = partyA;
    }

    public String getPartyB() {
        return PartyB;
    }

    public void setPartyB(String partyB) {
        PartyB = partyB;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getCallBackURL() {
        return CallBackURL;
    }

    public void setCallBackURL(String callBackURL) {
        CallBackURL = callBackURL;
    }

    public String getAccountReference() {
        return AccountReference;
    }

    public void setAccountReference(String accountReference) {
        AccountReference = accountReference;
    }

    public String getTransactionDesc() {
        return TransactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        TransactionDesc = transactionDesc;
    }
}
