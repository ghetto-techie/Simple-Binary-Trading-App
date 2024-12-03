package com.big.shamba.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.models.Investment;
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.data.firebase.InvestmentService;
import com.big.shamba.utility.FirestoreCollections;
import com.big.shamba.utility.FirestoreUpdateListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class InvestmentViewModel extends ViewModel {
    private static final String TAG = "InvestmentViewModel";
    private final InvestmentService investmentService;
    private final MutableLiveData<Double> totalEarnings = new MutableLiveData<>();
    private final MutableLiveData<Boolean> canInvest = new MutableLiveData<>();
    private final MutableLiveData<List<Investment>> maturedInvestmentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Investment>> allUserInvestmentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<InvestmentPackage> investmentPackage = new MutableLiveData<>();
    private ListenerRegistration totalEarningsListenerRegistration;

    public InvestmentViewModel(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    public Task<Void> invest(String userId, String packageId, long amount) {
        return investmentService.invest(userId, packageId, amount);
    }

    public Task<Boolean> canInvest(String userId, String packageId) {
        return investmentService.canInvest(userId, packageId)
                .addOnSuccessListener(aBoolean -> canInvest.setValue(aBoolean))
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    public void fetchTotalEarnings(String userId) {
        Log.d(TAG, "fetchTotalEarnings: ");
        investmentService
                .fetchTotalEarnings(userId)
                .addOnSuccessListener(aDouble -> {
                    totalEarnings.setValue(aDouble);
                    Log.d(TAG, "fetchTotalEarnings: Total Earnings -> " + aDouble);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    totalEarnings.setValue(0.0);
                    e.printStackTrace();
                    Log.d(TAG, "fetchTotalEarnings: Failed to get total earnings", e);
                });
    }

    public void fetchMaturedInvestments(String userId) {
        Log.d(TAG, "fetchMaturedInvestments: ");
        investmentService
                .fetchMaturedInvestments(userId)
                .addOnSuccessListener(investmentsList -> {
                    Log.d(TAG, "fetchMaturedInvestments: Investments > " + investmentsList);
                    maturedInvestmentsLiveData.setValue(investmentsList);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.d(TAG, "fetchMaturedInvestments: Failed to get Matured investments > " + e.getMessage());
                    e.printStackTrace();
                })
        ;
    }

    public void fetchMaturedInvestmentsRealtime(String userId) {
        Log.d(TAG, "fetchMaturedInvestmentsRealtime: ");
        Query query = investmentService
                .getFirestoreService()
                .getFirestore()
                .collection(FirestoreCollections.INVESTMENTS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("matured", true)
                .orderBy("endDate");
        investmentService
                .getFirestoreService()
                .listenForCollectionUpdates(
                        query,
                        new FirestoreUpdateListener() {
                            @Override
                            public void onDataChanged(List<DocumentSnapshot> documentSnapshots) {
                                List<Investment> investmentList = new ArrayList<>();
                                if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot investmentSnapshot : documentSnapshots) {
                                        Investment investment = investmentSnapshot.toObject(Investment.class);
                                        investment.setInvestmentId(investmentSnapshot.getId());
                                        investmentList.add(investment);
                                        Log.d(TAG, "onDataChanged: Investment added > " + investment.toString());
                                    }

//                                    allInvestmentsLiveData.setValue(investmentList);
                                    Log.d(TAG, "onDataChanged: Investment List Livedata updated");
                                }
                                maturedInvestmentsLiveData.setValue(investmentList);
                            }

                            @Override
                            public void onDataChangedStatus(boolean isUpdateAvailable) {
                                //Checks if there needs to be an update
                            }

                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "onError: Error while fetching all investments > " + e.getMessage());
                            }
                        }
                )
        ;
    }

    public void fetchAllUserInvestmentsRealtime(String userId) {
        Log.d(TAG, "fetchAllInvestmentsRealtime: ");
        Query allInvestmentsQuery = investmentService
                .getFirestoreService()
                .getFirestore()
                .collection(FirestoreCollections.INVESTMENTS)
                .whereEqualTo("userId", userId)
                .orderBy("endDate");

        investmentService
                .getFirestoreService()
                .listenForCollectionUpdates(
                        allInvestmentsQuery,
                        new FirestoreUpdateListener() {
                            @Override
                            public void onDataChanged(List<DocumentSnapshot> documentSnapshots) {
                                List<Investment> investmentList = new ArrayList<>();
                                if (documentSnapshots != null && !documentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot allInvestmentSnapshots : documentSnapshots) {
                                        Investment investment = allInvestmentSnapshots.toObject(Investment.class);
                                        investmentList.add(investment);
                                        Log.d(TAG, "onDataChanged: Investment added > " + investment.toString());
                                    }
                                    Log.d(TAG, "onDataChanged: Investment List Livedata updated");
                                    allUserInvestmentsLiveData.setValue(investmentList);
                                }
                            }

                            @Override
                            public void onDataChangedStatus(boolean isUpdateAvailable) {
                                // Checks if there needs to be an update
                            }

                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "onError: Error while fetching all investments > " + e.getMessage());
                            }
                        }
                )
        ;
    }

    public void fetchTotalEarningsCountRealTime(String userId) {
        if (totalEarningsListenerRegistration != null) {
            totalEarningsListenerRegistration.remove();
        }

        totalEarningsListenerRegistration = investmentService
                .fetchTotalEarningsRealTime(userId, new InvestmentService.TotalEarningsListener() {
                    @Override
                    public void onTotalEarningsChanged(double totalEarnings) {
                        InvestmentViewModel.this.totalEarnings.setValue(totalEarnings);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error fetching real-time total earnings", e);
                    }
                })
        ;
    }

    public LiveData<List<Investment>> getMaturedInvestments() {
        Log.d(TAG, "getMaturedInvestments: Matured Investments > " + maturedInvestmentsLiveData.getValue());
        return maturedInvestmentsLiveData;

    }

    public LiveData<List<Investment>> getAllUserInvestments() {
        Log.d(TAG, "getAllUserInvestments: All Investments > " + allUserInvestmentsLiveData.getValue());
        return allUserInvestmentsLiveData;
    }

    public LiveData<Double> getTotalEarningsCount() {
        return totalEarnings;
    }
}
