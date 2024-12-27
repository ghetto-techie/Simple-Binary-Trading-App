package com.big.shamba.data.firebase;


import android.util.Log;

import androidx.core.util.Pair;

import com.big.shamba.models.Referral;
import com.big.shamba.models.ReferralCommissionWalletTransaction;
import com.big.shamba.utility.TransactionType;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReferralService {
    private static final String TAG = "ReferralService";
    private final FirestoreService firestoreService;

    public ReferralService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    public FirestoreService getFirestoreService() {
        return firestoreService;
    }

    public Task<List<Referral>> fetchAllReferrals(String userId) {
        return firestoreService
                .getReferralsCollection()
                .whereEqualTo("userId", userId)
                .orderBy("dateJoined")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<Referral> referrals = new ArrayList<>();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                Referral referral = documentSnapshot.toObject(Referral.class);
                                referrals.add(referral);
                            }
                        }
                        return referrals;
                    } else {
                        throw task.getException();
                    }
                });
    }

    public Task<List<ReferralCommissionWalletTransaction>> getReferralCommissions(String userId) {
        Log.d(TAG, "getReferralCommissions: ");
        return firestoreService
                .getWalletTransactions()
                .whereEqualTo("type", TransactionType.REFERRAL_COMMISSION)
                .whereEqualTo("userId", userId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getReferralCommissions: Success");
                        QuerySnapshot querySnapshot = task.getResult();
                        List<ReferralCommissionWalletTransaction> transactions = new ArrayList<>();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                ReferralCommissionWalletTransaction transaction = documentSnapshot.toObject(ReferralCommissionWalletTransaction.class);
                                transactions.add(transaction);
                                Log.d(TAG, "getReferralCommissions: Transaction > " + transaction);
                            }
                        }
                        return transactions;
                    } else {
                        Log.d(TAG, "getReferralCommissions: Failed");
                        throw task.getException();
                    }
                });
    }

    // Fetches the total cumulative sum of all commissions
    public Task<Long> getTotalSumOfReferralCommissions(String userId) {
        Log.d(TAG, "fetchTotalReferralCommissions: ");
        return firestoreService
                .getWalletTransactions()
                .whereEqualTo("type", TransactionType.REFERRAL_COMMISSION)
                .whereEqualTo("userId", userId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "fetchTotalReferralCommissions: Success");
                        QuerySnapshot querySnapshot = task.getResult();
                        long totalCommissions = 0;
                        if (querySnapshot != null) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                ReferralCommissionWalletTransaction transaction = documentSnapshot.toObject(ReferralCommissionWalletTransaction.class);
                                Log.d(TAG, "getTotalSumOfReferralCommissions: transaction -> " + transaction.toString());
                                totalCommissions += transaction.getAmount();
                            }
                        }
                        Log.d(TAG, "fetchTotalReferralCommissions: Total Commissions -> " + totalCommissions);
                        return totalCommissions;
                    } else {
                        Log.d(TAG, "fetchTotalReferralCommissions: Failed");
                        throw task.getException();
                    }
                });
    }

    // Fetch the total number of referrals i.e How many referrals exist for user with the Id given
    public Task<Integer> fetchTotalReferralCount(String userId) {
        return firestoreService
                .getReferralsCollection()
                .whereEqualTo("userId", userId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        return querySnapshot.size();
                    } else {
                        throw task.getException();
                    }
                });
    }

    // Fetch all referrals as well as the referral count
    public Task<Pair<List<Referral>, Integer>> fetchReferralsAndCount(String userId) {
        return firestoreService
                .getReferralsCollection()
                .whereEqualTo("userId", userId)
                .orderBy("dateJoined")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<Referral> referrals = new ArrayList<>();
                        int count = 0;
                        if (querySnapshot != null) {
                            count = querySnapshot.size();
                            for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                Referral referral = documentSnapshot.toObject(Referral.class);
                                referrals.add(referral);
                            }
                        }
                        return new Pair<>(referrals, count);
                    } else {
                        throw task.getException();
                    }
                });
    }

    // Get Referral with the Id provided
    public Task<DocumentSnapshot> getReferralById(String referralId) {
        Log.d(TAG, "getReferralById: ");
        Task<DocumentSnapshot> documentSnapshotTask = firestoreService
                .getReferralsCollection()
                .document(referralId)
                .get();
        Log.d(TAG, "getReferralById: Referral -> " + documentSnapshotTask);
        return documentSnapshotTask;
    }

    public Task<Void> addReferral(Referral referral) {
        return firestoreService
                .getReferralsCollection()
                .add(referral)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return null;
                    } else {
                        throw task.getException();
                    }
                });
    }

    public Task<Void> updateReferral(String referralId, Map<String, Object> updates) {
        return firestoreService
                .getReferralsCollection()
                .document(referralId)
                .update(updates)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return null;
                    } else {
                        throw task.getException();
                    }
                });
    }

    public Task<Void> deleteReferral(String referralId) {
        return firestoreService
                .getReferralsCollection()
                .document(referralId)
                .delete()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return null;
                    } else {
                        throw task.getException();
                    }
                });
    }
}
