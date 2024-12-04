package com.big.shamba.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.data.firebase.InvestmentPackageService;
import com.big.shamba.models.Investment;
import com.big.shamba.models.InvestmentPackage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvestmentPackageViewModel extends ViewModel {
    private static final String TAG = "InvestmentPackageVM";
    private final InvestmentPackageService investmentPackageService;
    private final MutableLiveData<List<InvestmentPackage>> investmentPackagesLiveData = new MutableLiveData<>();

    // Cache of packages keyed by packageId
    private final MutableLiveData<Map<String, InvestmentPackage>> packageMapLiveData = new MutableLiveData<>(new HashMap<>());


    public InvestmentPackageViewModel(InvestmentPackageService investmentPackageService) {
        this.investmentPackageService = investmentPackageService;
    }

    // Fetch visible packages
    public void fetchVisibleInvestmentPackages() {
        investmentPackageService.fetchVisibleInvestmentPackages()
                .addOnSuccessListener(packages -> {
                    investmentPackagesLiveData.setValue(packages);
                    Log.d(TAG, "fetchVisiblePackages: Success");
                })
                .addOnFailureListener(e -> Log.e(TAG, "fetchVisiblePackages: Failed", e));
    }

    // Fetch packages for a list of investments
    public void fetchPackagesForInvestments(List<Investment> investments) {
        Log.d(TAG, "fetchPackagesForInvestments: _");
        Map<String, InvestmentPackage> currentCache = packageMapLiveData.getValue();
        if (currentCache == null) currentCache = new HashMap<>();

        for (Investment investment : investments) {
            Log.d(TAG, "fetchPackagesForInvestments: investment -> " + investment.toString());
            String packageId = investment.getPackageId();
            if (!currentCache.containsKey(packageId)) { // Fetch only if not already cached
                Log.d(TAG, "fetchPackagesForInvestments: not cached");
                Map<String, InvestmentPackage> finalCurrentCache = currentCache;
                investmentPackageService.fetchPackageById(packageId)
                        .addOnSuccessListener(packageData -> {
                            finalCurrentCache.put(packageId, packageData);
                            packageMapLiveData.setValue(new HashMap<>(finalCurrentCache)); // Trigger LiveData update
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "fetchPackagesForInvestments: Failed for packageId=" + packageId, e))
                ;
            }
        }
    }

    // Expose the cached package map
    public LiveData<Map<String, InvestmentPackage>> getPackageMapLiveData() {
        Log.d(TAG, "getPackageMapLiveData: ");
        return packageMapLiveData;
    }

    // Fetch all packages
    public void fetchAllPackages() {
        investmentPackageService.fetchAllInvestmentPackages()
                .addOnSuccessListener(packages -> {
                    investmentPackagesLiveData.setValue(packages);
                    Log.d(TAG, "fetchAllPackages: Success");
                })
                .addOnFailureListener(e -> Log.e(TAG, "fetchAllPackages: Failed", e));
    }

    // Get packages as LiveData
    public LiveData<List<InvestmentPackage>> getInvestmentPackages() {
        return investmentPackagesLiveData;
    }

    // Update a package
    public void updatePackage(String packageId, Map<String, Object> updates) {
        investmentPackageService.updatePackage(packageId, updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "updatePackage: Success"))
                .addOnFailureListener(e -> Log.e(TAG, "updatePackage: Failed", e));
    }

    // Delete a package
    public void deletePackage(String packageId) {
        investmentPackageService.deletePackage(packageId)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "deletePackage: Success"))
                .addOnFailureListener(e -> Log.e(TAG, "deletePackage: Failed", e));
    }
}

