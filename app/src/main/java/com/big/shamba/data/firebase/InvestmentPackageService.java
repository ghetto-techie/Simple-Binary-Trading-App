package com.big.shamba.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.utility.FirestoreCollections;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvestmentPackageService {
    private static final String TAG = "InvestmentPackageService";
    private final FirestoreService firestoreService;

    public InvestmentPackageService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
        Log.d(TAG, "InvestmentPackageService: ");
    }

    public Task<List<InvestmentPackage>> fetchVisibleInvestmentPackages() {
        Log.d(TAG, "fetchInvestmentPackages: ");
        return firestoreService
                .getFirestore()
                .collection(FirestoreCollections.PACKAGES)
                .whereEqualTo("isVisible", true)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "fetchInvestmentPackages: Failed to get Investment Packages > " + task.getException());
                        throw task.getException();
                    }
                    List<InvestmentPackage> investmentPackagesList = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        InvestmentPackage investmentPackage = documentSnapshot.toObject(InvestmentPackage.class);
                        String packageId = documentSnapshot.getId();
                        assert investmentPackage != null;
                        investmentPackage.setPackageId(packageId);

                        investmentPackagesList.add(investmentPackage);
                        Log.d(TAG, "fetchInvestmentPackages: Investment Package added > " + investmentPackage);
                    }
                    return investmentPackagesList;
                });
    }
}
