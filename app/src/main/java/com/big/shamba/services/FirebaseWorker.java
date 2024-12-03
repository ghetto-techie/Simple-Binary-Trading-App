package com.big.shamba.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.big.shamba.data.firebase.AuthService;
import com.big.shamba.data.firebase.FirestoreService;
import com.big.shamba.data.firebase.InvestmentService;
import com.big.shamba.data.firebase.InvestmentPackageService;
import com.big.shamba.data.firebase.WalletService;
import com.big.shamba.models.Investment;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.InvestmentViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class FirebaseWorker extends Worker {
    private static final String TAG = "FirebaseWorker";
    private final AuthViewModel authViewModel;
    private final InvestmentViewModel investmentViewModel;
    private final FirestoreService firestoreService;

    public FirebaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        // Initialize ViewModels or any other necessary components here
        authViewModel = new AuthViewModel(new AuthService());
        firestoreService = new FirestoreService();
        investmentViewModel = new InvestmentViewModel(new InvestmentService(
                firestoreService,
                new WalletService(firestoreService),
                new InvestmentPackageService(firestoreService)
        ));
        Log.d(TAG, "FirebaseWorker: ");
    }

    @NonNull
    @Override
    public Result doWork() {
        fetchCurrentUser();
        Log.d(TAG, "doWork: FirebaseWorker");
        return Result.success();
    }

    private void fetchCurrentUser() {
        // Fetch data from Firebase asynchronously
        LiveData<FirebaseUser> currentUser = authViewModel.getCurrentUser();
        currentUser.observeForever(firebaseUser -> {
            if (firebaseUser != null) {
                String userId = firebaseUser.getUid();
                Log.d(TAG, "fetchCurrentUser: firebaseUser -> " + firebaseUser);
//                loadMaturedInvestments(userId);
            }
        });
    }
}
