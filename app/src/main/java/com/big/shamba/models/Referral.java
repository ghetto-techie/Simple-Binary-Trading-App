package com.big.shamba.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Referral implements Serializable {
    private String documentId; //Not included in document

    private String userId; //Owner Id
    private String referredUserId; //Referred User
    private String name; //Referred User's name
    @ServerTimestamp
    private Date dateJoined;

    public Referral() {
    }

    public Referral(String userId, String referredUserId, String name, Date dateJoined) {
        this.userId = userId;
        this.referredUserId = referredUserId;
        this.name = name;
        this.dateJoined = dateJoined;
    }

    @Override
    public String toString() {
        return "Referral{" +
                ", userId='" + userId + '\'' +
                ", referredUserId='" + referredUserId + '\'' +
                ", name='" + name + '\'' +
                ", dateJoined=" + dateJoined +
                '}';
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReferredUserId() {
        return referredUserId;
    }

    public void setReferredUserId(String referredUserId) {
        this.referredUserId = referredUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }
}
