package com.big.shamba.ui.viewmodels.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.data.firebase.ReferralService;
import com.big.shamba.ui.viewmodels.ReferralViewModel;

public class ReferralViewModelFactory implements ViewModelProvider.Factory {
    private final ReferralService referralService;

    public ReferralViewModelFactory(ReferralService referralService) {
        this.referralService = referralService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReferralViewModel.class)) {
            return (T) new ReferralViewModel(referralService);
        }
        throw new IllegalArgumentException("Unknown View Model class");
    }
}
