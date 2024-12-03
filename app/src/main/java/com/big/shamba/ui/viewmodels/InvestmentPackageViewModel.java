package com.big.shamba.ui.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.data.firebase.InvestmentPackageService;
import com.big.shamba.models.InvestmentPackage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class InvestmentPackageViewModel extends ViewModel {
    private static final String TAG = "InvestmentPackageViewMo";
    private final InvestmentPackageService investmentPackageService;

    private MutableLiveData<List<InvestmentPackage>> investmentPackagesLiveData = new MutableLiveData<>();

    public InvestmentPackageViewModel(InvestmentPackageService investmentPackageService) {
        this.investmentPackageService = investmentPackageService;
    }

    public void fetchInvestmentPackages() {
        investmentPackageService
                .fetchVisibleInvestmentPackages()
                .addOnSuccessListener(investmentPackages -> {
                    if (investmentPackages != null) {
                        Log.d(TAG, "onSuccess: Investment packages > " + investmentPackages);
                        investmentPackagesLiveData.setValue(investmentPackages);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.d(TAG, "onFailure: Failed to get packages > " + e.getMessage());
                })
        ;
    }

    public LiveData<List<InvestmentPackage>> getInvestmentPackages() {
        Log.d(TAG, "getInvestmentPackages: Investment Packages > " + investmentPackagesLiveData);
        return investmentPackagesLiveData;
    }

}
