package com.big.shamba.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.data.firebase.WalletService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.ListenerRegistration;

public class WalletViewModel extends ViewModel {
    private static final String TAG = "WalletViewModel";
    private final WalletService walletService;
    private final MutableLiveData<Long> walletBalance = new MutableLiveData<>();
    private final MutableLiveData<String> addEarningsResult = new MutableLiveData<>();
    private ListenerRegistration balanceListenerRegistration;

    public WalletViewModel(WalletService walletService) {
        Log.d(TAG, "WalletViewModel: WalletViewModel started: ");
        this.walletService = walletService;
    }

    public LiveData<Long> getWalletBalance() {
        Log.d(TAG, "getWalletBalance: Wallet Balance -> " + walletBalance.getValue());
        return walletBalance;
    }

    public LiveData<String> getAddEarningsResult() {
        return addEarningsResult;
    }

    public void fetchWalletBalance(String userId) {
        Log.d(TAG, "fetchWalletBalance: Fetching wallet balance for user: " + userId);
        walletService.getWalletBalance(userId).addOnSuccessListener(balance -> {
            walletBalance.setValue(balance);
            Log.d(TAG, "fetchWalletBalance: Wallet Balance -> " + walletBalance.getValue());
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            Log.d(TAG, "fetchWalletBalance: Failed to fetch wallet balance");
            walletBalance.setValue(0L);
        });
    }

    public void startListeningForWalletBalanceChanges(String userId) {
        Log.d(TAG, "startListeningForWalletBalanceChanges: Starting to listen for wallet balance changes");
        if (balanceListenerRegistration != null) {
            balanceListenerRegistration.remove();
            Log.d(TAG, "startListeningForWalletBalanceChanges: Stopped listening for wallet balance changes");
        }
        balanceListenerRegistration = walletService.listenForWalletBalanceChanges(userId, new WalletService.WalletBalanceUpdateListener() {
            @Override
            public void onBalanceChanged(long balance) {
                Log.d(TAG, "onBalanceChanged: Wallet balance changed to: " + balance);
                walletBalance.setValue(balance);
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "onError: Failed to listen for wallet balance changes");
                e.printStackTrace();
            }
        });
    }

    public Task<Void> addInvestmentEarningsToWallet(String userId, String investmentId) {
        return walletService.addInvestmentEarningsToWallet(userId, investmentId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: ViewModel cleared");
        if (balanceListenerRegistration != null) {
            Log.d(TAG, "onCleared: Stopping to listen for wallet balance changes");
            balanceListenerRegistration.remove();
        }
    }
}
