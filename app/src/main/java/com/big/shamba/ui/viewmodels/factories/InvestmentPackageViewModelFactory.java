package com.big.shamba.ui.viewmodels.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.data.firebase.InvestmentPackageService;
import com.big.shamba.ui.viewmodels.InvestmentPackageViewModel;

public class InvestmentPackageViewModelFactory implements ViewModelProvider.Factory {
    private final InvestmentPackageService investmentPackageService;

    public InvestmentPackageViewModelFactory(InvestmentPackageService investmentPackageService) {
        this.investmentPackageService = investmentPackageService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(InvestmentPackageViewModel.class)) {
            return (T) new InvestmentPackageViewModel(investmentPackageService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

