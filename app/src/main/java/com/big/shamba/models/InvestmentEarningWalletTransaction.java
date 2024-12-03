package com.big.shamba.models;

import com.big.shamba.utility.TransactionType;

public class InvestmentEarningWalletTransaction extends WalletTransaction {
    private String investmentId;

    public InvestmentEarningWalletTransaction(String userId, long amount, TransactionType type, String investmentId) {
        super(userId, amount, type);
        this.investmentId = investmentId;
    }


    @Override
    public String toString() {
        return "InvestmentEarningTransaction{" +
                "investmentId='" + investmentId + '\'' +
                '}';
    }

    public String getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(String investmentId) {
        this.investmentId = investmentId;
    }
}
