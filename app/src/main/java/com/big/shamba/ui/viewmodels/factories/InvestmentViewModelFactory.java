package com.big.shamba.ui.viewmodels.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.data.firebase.InvestmentService;
import com.big.shamba.ui.viewmodels.InvestmentViewModel;

public class InvestmentViewModelFactory implements ViewModelProvider.Factory {
    private final InvestmentService investmentService;

    public InvestmentViewModelFactory(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(InvestmentViewModel.class)) {
            return (T) new InvestmentViewModel(investmentService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
