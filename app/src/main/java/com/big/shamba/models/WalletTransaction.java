package com.big.shamba.models;

import com.big.shamba.utility.TransactionType;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public abstract class WalletTransaction {
    private String userId;
    private long amount;
    @ServerTimestamp
    private Date timeStamp;
    private TransactionType type;

    @Override
    public String toString() {
        return "Transaction{" +
                ", amount=" + amount +
                ", timeStamp=" + timeStamp +
                ", type=" + type +
                '}';
    }

    public WalletTransaction(String userId, long amount, TransactionType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
    }

    public WalletTransaction(String userId, long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public WalletTransaction() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}