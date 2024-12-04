package com.big.shamba.ui.viewmodels;

import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.data.firebase.ReferralService;
import com.big.shamba.models.Referral;
import com.big.shamba.models.ReferralCommissionWalletTransaction;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for managing referral-related operations.
 * Handles data fetching, transformation, and LiveData updates for the UI layer.
 */
public class ReferralViewModel extends ViewModel {
    private static final String TAG = "ReferralViewModel";

    // Dependencies
    private final ReferralService referralService;

    // LiveData for referrals and commission data
    private final MutableLiveData<List<Referral>> referralsListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalReferralCountLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Pair<ReferralCommissionWalletTransaction, Referral>>> referralCommissionLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> totalSumOfReferralCommissions = new MutableLiveData<>();

    // Constructor to initialize dependencies
    public ReferralViewModel(ReferralService referralService) {
        Log.d(TAG, "ReferralViewModel initialized");
        this.referralService = referralService;
    }

    /**
     * Fetches total referrals for the given user and updates LiveData.
     * Avoids redundant calls by checking existing data.
     */
    public void fetchTotalReferrals(String userId) {
        if (referralsListLiveData.getValue() != null) {
            Log.d(TAG, "fetchTotalReferrals: Skipping as data is already available.");
            return; // Prevent redundant API calls
        }

        Log.d(TAG, "fetchTotalReferrals: Starting fetch.");
        referralService.fetchReferralsAndCount(userId)
                .addOnSuccessListener(result -> {
                    List<Referral> referrals = result.first;
                    int count = result.second;

                    // Update LiveData
                    referralsListLiveData.setValue(referrals);
                    Log.d(TAG, "fetchTotalReferrals: Fetched " + count + " referrals for user " + userId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "fetchTotalReferrals: Failed to fetch referrals -> " + e.getMessage());
                });
    }

    /**
     * Fetches the total referral count for a user.
     */
    public void fetchTotalReferralCount(String userId) {
        referralService.fetchTotalReferralCount(userId)
                .addOnSuccessListener(totalReferrals -> {
                    Log.d(TAG, "fetchTotalReferralCount: Fetched " + totalReferrals + " total referrals for user " + userId);
                    totalReferralCountLiveData.setValue(totalReferrals);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "fetchTotalReferralCount: Failed -> " + e.getMessage());
                });
    }

    /**
     * Fetches referral commissions and their corresponding referrals for a user.
     * Handles asynchronous tasks efficiently.
     */
    public void fetchReferralCommissions(String userId) {
        Log.d(TAG, "fetchReferralCommissions: Starting fetch.");
        referralService.getReferralCommissions(userId)
                .addOnSuccessListener(referralCommissionTransactions -> {
                    Log.d(TAG, "fetchReferralCommissions: Successfully fetched transactions.");

                    // Create tasks to fetch referrals for each transaction
                    List<Task<Pair<ReferralCommissionWalletTransaction, Referral>>> tasks = new ArrayList<>();
                    for (ReferralCommissionWalletTransaction transaction : referralCommissionTransactions) {
                        tasks.add(fetchReferralWithTransaction(transaction));
                    }

                    // Handle completion of all tasks
                    Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
                        List<Pair<ReferralCommissionWalletTransaction, Referral>> results = new ArrayList<>();

                        for (Task<Pair<ReferralCommissionWalletTransaction, Referral>> t : tasks) {
                            if (t.isSuccessful()) {
                                results.add(t.getResult());
                                Log.d(TAG, "fetchReferralCommissions: Result -> " + t.getResult());
                            } else {
                                Log.e(TAG, "fetchReferralCommissions: Failed task -> " + t.getException());
                            }
                        }

                        referralCommissionLiveData.setValue(results);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "fetchReferralCommissions: Failed -> " + e.getMessage());
                });
    }

    /**
     * Fetches referral details for a specific transaction.
     */
    private Task<Pair<ReferralCommissionWalletTransaction, Referral>> fetchReferralWithTransaction(ReferralCommissionWalletTransaction transaction) {
        Log.d(TAG, "fetchReferralWithTransaction: Transaction ID -> " + transaction.getReferralId());

        return referralService.getReferralById(transaction.getReferralId())
                .continueWith(task -> {
                    Referral referral = task.isSuccessful() ? task.getResult().toObject(Referral.class) : null;
                    Pair<ReferralCommissionWalletTransaction, Referral> pair = new Pair<>(transaction, referral);

                    Log.d(TAG, "fetchReferralWithTransaction: Pair -> " + pair);
                    return pair;
                });
    }

    /**
     * Fetches the total sum of referral commissions for a user.
     */
    public void fetchTotalSumOfReferralCommissions(String userId) {
        referralService.getTotalSumOfReferralCommissions(userId)
                .addOnSuccessListener(totalCommissions -> {
                    Log.d(TAG, "fetchTotalSumOfReferralCommissions: Fetched total -> " + totalCommissions);
                    totalSumOfReferralCommissions.setValue(totalCommissions);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "fetchTotalSumOfReferralCommissions: Failed -> " + e.getMessage());
                })
        ;
    }

    // --- LiveData Getters ---
    public LiveData<List<Referral>> getReferralsLiveData() {
        Log.d(TAG, "getReferralsLiveData: LiveData -> " + referralsListLiveData);
        return referralsListLiveData;
    }

    public LiveData<Integer> getTotalReferralCount() {
        return totalReferralCountLiveData;
    }

    public LiveData<List<Pair<ReferralCommissionWalletTransaction, Referral>>> getReferralCommissionsLiveData() {
        Log.d(TAG, "getReferralCommissionsLiveData: LiveData -> " + referralCommissionLiveData);
        return referralCommissionLiveData;
    }

    public LiveData<Long> getTotalSumOfReferralCommissions() {
        return totalSumOfReferralCommissions;
    }

    // --- CRUD Operations ---
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
