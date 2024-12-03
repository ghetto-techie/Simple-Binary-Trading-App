package com.big.shamba.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.data.firebase.InvestmentPackageService;
import com.big.shamba.models.InvestmentPackage;

import java.util.List;

public class PackageViewModel extends ViewModel {
    private final InvestmentPackageService investmentPackageService;
    private MutableLiveData<InvestmentPackage> investmentPackage = new MutableLiveData<>();
    private MutableLiveData<List<InvestmentPackage>> investmentPackages = new MutableLiveData<>();

    public PackageViewModel(InvestmentPackageService investmentPackageService) {
        this.investmentPackageService = investmentPackageService;
    }
}
