package com.big.shamba.data.firebase;

import android.util.Log;

import com.big.shamba.models.InvestmentPackage;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvestmentPackageService {
    private static final String TAG = "InvestmentPackageService";
    private final FirestoreService firestore;

    public InvestmentPackageService(FirestoreService firestore) {
        this.firestore = firestore;
    }

    // --- CREATE ---
    public Task<DocumentReference> addInvestmentPackage(InvestmentPackage investmentPackage) {
        Log.d(TAG, "addInvestmentPackage: Adding new package -> " + investmentPackage);
        return firestore
                .getPackagesCollection()
                .add(investmentPackage)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "addInvestmentPackage: Package added with ID -> " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.e(TAG, "addInvestmentPackage: Failed to add package", e));
    }

    // --- READ ---
    public Task<List<InvestmentPackage>> fetchVisibleInvestmentPackages() {
        Log.d(TAG, "fetchVisibleInvestmentPackages: Fetching visible packages...");
        return firestore
                .getPackagesCollection()
                .whereEqualTo("isVisible", true)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<InvestmentPackage> packages = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            InvestmentPackage investmentPackage = doc.toObject(InvestmentPackage.class);
                            if (investmentPackage != null) {
                                investmentPackage.setPackageId(doc.getId());
                                packages.add(investmentPackage);
                                Log.d(TAG, "fetchVisibleInvestmentPackages: Fetched package -> " + investmentPackage);
                            }
                        }
                        return packages;
                    } else {
                        Log.e(TAG, "fetchVisibleInvestmentPackages: Failed", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<List<InvestmentPackage>> fetchAllInvestmentPackages() {
        Log.d(TAG, "fetchAllInvestmentPackages: Fetching all packages...");
        return firestore
                .getPackagesCollection()
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<InvestmentPackage> packages = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            InvestmentPackage investmentPackage = doc.toObject(InvestmentPackage.class);
                            if (investmentPackage != null) {
                                investmentPackage.setPackageId(doc.getId());
                                packages.add(investmentPackage);
                                Log.d(TAG, "fetchAllInvestmentPackages: Fetched package -> " + investmentPackage);
                            }
                        }
                        return packages;
                    } else {
                        Log.e(TAG, "fetchAllInvestmentPackages: Failed", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<InvestmentPackage> fetchPackageById(String packageId) {
        Log.d(TAG, "fetchPackageById: Fetching package with ID -> " + packageId);
        return firestore
                .getPackagesCollection()
                .document(packageId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        InvestmentPackage investmentPackage = task.getResult().toObject(InvestmentPackage.class);
                        if (investmentPackage != null) {
                            investmentPackage.setPackageId(packageId);
                            Log.d(TAG, "fetchPackageById: Package fetched -> " + investmentPackage);
                            return investmentPackage;
                        } else {
                            throw new Exception("Package not found");
                        }
                    } else {
                        Log.e(TAG, "fetchPackageById: Failed", task.getException());
                        throw task.getException();
                    }
                });
    }

    // --- UPDATE ---
    public Task<Void> updatePackage(String packageId, Map<String, Object> updates) {
        Log.d(TAG, "updatePackage: Updating package with ID -> " + packageId);
        return firestore
                .getPackagesCollection()
                .document(packageId)
                .update(updates)
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "updatePackage: Package updated successfully"))
                .addOnFailureListener(e ->
                        Log.e(TAG, "updatePackage: Failed to update package", e));
    }

    // --- DELETE ---
    public Task<Void> deletePackage(String packageId) {
        Log.d(TAG, "deletePackage: Deleting package with ID -> " + packageId);
        return firestore
                .getPackagesCollection()
                .document(packageId)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "deletePackage: Package deleted successfully"))
                .addOnFailureListener(e ->
                        Log.e(TAG, "deletePackage: Failed to delete package", e));
    }
}
