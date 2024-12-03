package com.big.shamba.models;

import com.big.shamba.utility.TransactionType;

public class ReferralCommissionWalletTransaction extends WalletTransaction {
    private String referralId;
    private String investmentId;

    // No-argument constructor required by Firestore
    public ReferralCommissionWalletTransaction() {
        super();
    }

    public ReferralCommissionWalletTransaction(
            String userId,
            long amount,
            String referralId,
            String investmentId
    ) {
        super(userId, amount);
        setType(TransactionType.REFERRAL_COMMISSION);
        this.referralId = referralId;
        this.investmentId = investmentId;
    }

    @Override
    public String toString() {
        return "ReferralCommissionWalletTransaction{" +
                "referralId='" + referralId + '\'' +
                ", investmentId='" + investmentId + '\'' +
                '}';
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public String getInvestmentId() {
        return investmentId;
    }

    public void setInvestmentId(String investmentId) {
        this.investmentId = investmentId;
    }
}
