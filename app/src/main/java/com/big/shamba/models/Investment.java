package com.big.shamba.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Investment implements Serializable {
    private String investmentId = null; // Is not saved in the document

    private String userId;
    private String packageId;
    private long amount;
    @ServerTimestamp
    private Date startDate;
    private Date endDate;
    private boolean matured;
    private boolean earningsAdded;

    public Investment() {
    }

    @Override
    public String toString() {
        return "Investment{" +
                "userId='" + userId + '\'' +
                ", packageId='" + packageId + '\'' +
                ", amount=" + amount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", matured=" + matured +
                '}';
    }

    public Investment(String userId, String packageId, long amount, Date endDate) {
        this.userId = userId;
        this.packageId = packageId;
        this.amount = amount;
        this.endDate = endDate;
        this.matured = false;
        this.earningsAdded = false;
    }

    public String getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(String investmentId) {
        this.investmentId = investmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isMatured() {
        return matured;
    }

    public void setMatured(boolean matured) {
        this.matured = matured;
    }

    public boolean isEarningsAdded() {
        return earningsAdded;
    }

    public void setEarningsAdded(boolean earningsAdded) {
        this.earningsAdded = earningsAdded;
    }
}
