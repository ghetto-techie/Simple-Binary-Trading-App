package com.big.shamba.ui.viewmodels;

import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.data.firebase.ReferralService;
import com.big.shamba.models.Referral;
import com.big.shamba.models.ReferralCommissionWalletTransaction;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;

public class ReferralViewModel extends ViewModel {
    private static final String TAG = "ReferralViewModel";
    private final ReferralService referralService;
    private final MutableLiveData<List<Referral>> referralsListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalReferralCountLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Pair<ReferralCommissionWalletTransaction, Referral>>> referralCommissionLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> totalSumOfReferralCommissions = new MutableLiveData<>();

    public ReferralViewModel(ReferralService referralService) {
        Log.d(TAG, "ReferralViewModel: ");
        this.referralService = referralService;
    }

    public void fetchTotalReferrals(String userId) {
        Log.d(TAG, "fetchTotalReferrals: ");
        referralService.fetchReferralsAndCount(userId)
                .addOnSuccessListener(result -> {
                    List<Referral> referrals = result.first;
                    int count = result.second;
                    referralsListLiveData.setValue(referrals);
//                    totalReferralCountLiveData.setValue(count);
                    Log.d(TAG, "fetchTotalReferrals: Fetched " + count + " referrals for user " + userId);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "fetchTotalReferrals: Failed to fetch referrals and count -> " + e.getMessage());
                    e.printStackTrace();
                });
    }

    public void fetchTotalReferralCount(String userId) {
        referralService.fetchTotalReferralCount(userId)
                .addOnSuccessListener(totalReferrals -> {
                    Log.d(TAG, "fetchTotalReferrals: Total referrals for user " + userId + " -> " + totalReferrals);
                    totalReferralCountLiveData.setValue(totalReferrals);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "fetchTotalReferrals: Failed to fetch total referrals -> " + e.getMessage());
                    e.printStackTrace();
                });
    }


    public LiveData<List<Pair<ReferralCommissionWalletTransaction, Referral>>>
    getReferralCommissionsLiveData() {
        Log.d(TAG, "getReferralCommissionsLiveData: " + referralCommissionLiveData);
        return referralCommissionLiveData;
    }

    public void fetchReferralCommissions(String userId) {
        Log.d(TAG, "fetchReferralCommissions: ");
        referralService.getReferralCommissions(userId)
                .addOnSuccessListener(referralCommissionTransactions -> {
                    Log.d(TAG, "fetchReferralCommissions: Success");
                    List<Task<Pair<ReferralCommissionWalletTransaction, Referral>>> tasks = new ArrayList<>();
                    for (ReferralCommissionWalletTransaction transaction : referralCommissionTransactions) {
                        tasks.add(fetchReferralWithTransaction(transaction));
                    }
                    Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
                        List<Pair<ReferralCommissionWalletTransaction, Referral>> result = new ArrayList<>();
                        for (Task<Pair<ReferralCommissionWalletTransaction, Referral>> t : tasks) {
                            if (t.isSuccessful()) {
                                result.add(t.getResult());
                                Log.d(TAG, "fetchReferralCommissions:" + t.getResult() + "\n");
                            }
                        }
                        referralCommissionLiveData.setValue(result);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch referral commissions: ", e);
                });
    }

    private Task<Pair<ReferralCommissionWalletTransaction, Referral>>
    fetchReferralWithTransaction(ReferralCommissionWalletTransaction transaction) {
        Log.d(TAG, "fetchReferralWithTransaction: ");
        Log.d(TAG, "fetchReferralWithTransaction: Transaction > " + transaction.getReferralId());
        return referralService.getReferralById(transaction.getReferralId())
                .continueWith(task -> {
                    Referral referral = task.isSuccessful() ? task.getResult().toObject(Referral.class) : null;
                    Pair<ReferralCommissionWalletTransaction, Referral> referralCommissionWalletTransactionReferralPair = new Pair<>(transaction, referral);
                    Log.d(TAG, "fetchReferralWithTransaction: ReferralTransactionPair -> " + referralCommissionWalletTransactionReferralPair);
                    return referralCommissionWalletTransactionReferralPair;
                });
    }

    public void fetchTotalSumOfReferralCommissions(String userId) {
        referralService.getTotalSumOfReferralCommissions(userId)
                .addOnSuccessListener(totalCommissions -> {
                    Log.d(TAG, "fetchTotalSumOfReferralCommissions: Total commissions for user " + userId + " -> " + totalCommissions);
                    totalSumOfReferralCommissions.setValue(totalCommissions);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "fetchTotalSumOfReferralCommissions: Failed to fetch total commissions -> " + e.getMessage());
                    e.printStackTrace();
                })
        ;
    }

    public LiveData<List<Referral>> getReferralsLiveData() {
        Log.d(TAG, "getReferralsLiveData: Referrals > " + referralsListLiveData);
        return referralsListLiveData;
    }

    public LiveData<Long> getTotalSumOfReferralCommissions() {
        return totalSumOfReferralCommissions;
    }

    public LiveData<Integer> getTotalReferralCount() {
        return totalReferralCountLiveData;
    }

    public Task<DocumentSnapshot> getReferralById(String referralId) {
        return referralService
                .getReferralById(referralId)
                .addOnSuccessListener(documentSnapshot -> documentSnapshot.toObject(Referral.class));
    }

    public Task<Void> addReferral(Referral referral) {
        return referralService.addReferral(referral);
    }

    public Task<Void> updateReferral(String referralId, Map<String, Object> updates) {
        return referralService.updateReferral(referralId, updates);
    }

    public Task<Void> deleteReferral(String referralId) {
        return referralService.deleteReferral(referralId);
    }
}
