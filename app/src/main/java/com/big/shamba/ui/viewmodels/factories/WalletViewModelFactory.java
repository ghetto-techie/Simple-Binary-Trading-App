package com.big.shamba.ui.viewmodels.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.data.firebase.WalletService;
import com.big.shamba.ui.viewmodels.WalletViewModel;

public class WalletViewModelFactory implements ViewModelProvider.Factory {
    private final WalletService walletService;

    public WalletViewModelFactory(WalletService walletService) {
        this.walletService = walletService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WalletViewModel.class)) {
            return (T) new WalletViewModel(walletService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
