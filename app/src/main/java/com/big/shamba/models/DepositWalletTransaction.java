package com.big.shamba.models;

import com.big.shamba.utility.TransactionPlatform;
import com.big.shamba.utility.TransactionType;

public class DepositWalletTransaction extends WalletTransaction {
    private TransactionPlatform platform;
    private String description;

    public DepositWalletTransaction(String userId, long amount, TransactionPlatform platform, String description) {
        super(userId, amount, TransactionType.DEPOSIT);
        this.platform = platform;
        this.description = description;
    }

    public DepositWalletTransaction() {
        super();
        setType(TransactionType.DEPOSIT);
    }

    public TransactionPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(TransactionPlatform platform) {
        this.platform = platform;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
