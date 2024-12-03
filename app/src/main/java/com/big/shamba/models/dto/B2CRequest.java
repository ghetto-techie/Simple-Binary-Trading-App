package com.big.shamba.models.dto;

public class B2CRequest {
    private String InitiatorName;
    private String SecurityCredential;
    private String CommandID;
    private String Amount;
    private String PartyA;
    private String PartyB;
    private String Remarks;
    private String QueueTimeOutURL;
    private String ResultURL;
    private String Occasion;

    public B2CRequest(String initiatorName, String securityCredential, String commandID, String amount, String partyB, String remarks, String occasion) {
        this.InitiatorName = initiatorName;
        this.SecurityCredential = securityCredential;
        this.CommandID = commandID;
        this.Amount = amount;
        this.PartyA = initiatorName;
        this.PartyB = partyB;
        this.Remarks = remarks;
        this.QueueTimeOutURL = "https://example.com/timeout";
        this.ResultURL = "https://example.com/result";
        this.Occasion = occasion;
    }

    public String getInitiatorName() {
        return InitiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        InitiatorName = initiatorName;
    }

    public String getSecurityCredential() {
        return SecurityCredential;
    }

    public void setSecurityCredential(String securityCredential) {
        SecurityCredential = securityCredential;
    }

    public String getCommandID() {
        return CommandID;
    }

    public void setCommandID(String commandID) {
        CommandID = commandID;
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

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getQueueTimeOutURL() {
        return QueueTimeOutURL;
    }

    public void setQueueTimeOutURL(String queueTimeOutURL) {
        QueueTimeOutURL = queueTimeOutURL;
    }

    public String getResultURL() {
        return ResultURL;
    }

    public void setResultURL(String resultURL) {
        ResultURL = resultURL;
    }

    public String getOccasion() {
        return Occasion;
    }

    public void setOccasion(String occasion) {
        Occasion = occasion;
    }
}
