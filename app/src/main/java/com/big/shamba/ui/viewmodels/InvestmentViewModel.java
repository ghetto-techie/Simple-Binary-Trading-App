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

    private final MutableLiveData<List<Investment>> allInvestmentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Investment>> filteredInvestmentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> totalEarningsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> newMaturedInvestmentCount = new MutableLiveData<>(0);
    private final List<String> newlyMaturedIds = new ArrayList<>(); // Track new matured investments


    private String activeFilter = "all"; // Track the current filter

    private ListenerRegistration totalEarningsListenerRegistration;
    private ListenerRegistration investmentsListenerRegistration;

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
                })
        ;
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

    private final MutableLiveData<Integer> newPendingInvestmentCount = new MutableLiveData<>(0);
    private final List<String> newlyPendingIds = new ArrayList<>();

    // Getters for new pending investments
    public LiveData<Integer> getNewPendingInvestmentCount() {
        return newPendingInvestmentCount;
    }

    public List<String> getNewlyPendingIds() {
        return newlyPendingIds;
    }

    // Mark pending investments as viewed
    public void markPendingInvestmentsAsViewed() {
        newlyPendingIds.clear();
        newPendingInvestmentCount.setValue(0); // Reset badge count
    }


    public void clearNewMaturedInvestmentCount() {
        newMaturedInvestmentCount.setValue(0);
    }

    public void startListeningForInvestments(String userId) {
        investmentsListenerRegistration = investmentService
                .getFirestoreService()
                .getFirestore()
                .collection(FirestoreCollections.INVESTMENTS)
                .whereEqualTo("userId", userId)
                .orderBy("endDate")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "startListeningForInvestments: Failed to listen", e);
                        return;
                    }

                    if (querySnapshot != null) {
                        List<Investment> investments = new ArrayList<>();
                        List<String> newPending = new ArrayList<>();
                        List<String> newMatured = new ArrayList<>();

                        for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                            Investment investment = snapshot.toObject(Investment.class);
                            if (investment != null) {
                                investment.setInvestmentId(snapshot.getId());
                                investments.add(investment);

                                // Identify new pending investments
                                if (!investment.isMatured() && !newlyPendingIds.contains(investment.getInvestmentId())) {
                                    newPending.add(investment.getInvestmentId());
                                }

                                // Identify new matured investments
                                if (investment.isMatured() && !newlyMaturedIds.contains(investment.getInvestmentId())) {
                                    newMatured.add(investment.getInvestmentId());
                                }
                            }
                        }

                        allInvestmentsLiveData.setValue(investments);
                        applyCurrentFilter();

                        // Update badge counts and new IDs
                        newlyPendingIds.addAll(newPending);
                        newPendingInvestmentCount.setValue(newPending.size());

                        newlyMaturedIds.addAll(newMatured);
                        newMaturedInvestmentCount.setValue(newMatured.size());
                    }
                })
        ;
    }

    // Apply the current filter to the investment list
    private void applyCurrentFilter() {
        Log.d(TAG, "applyCurrentFilter: Applying filter -> " + activeFilter);
        filterInvestments(activeFilter);
    }


    // Stop listening for updates
    public void stopListeningForInvestments() {
        if (investmentsListenerRegistration != null) {
            investmentsListenerRegistration.remove();
            investmentsListenerRegistration = null;
        }
    }

    // Load all investments
    public void loadInvestments(String userId) {
        Log.d(TAG, "loadInvestments: _");
        investmentService.fetchAllInvestments(userId)
                .addOnSuccessListener(investments -> {
                    Log.d(TAG, "loadInvestments: investments -> " + investments);
                    allInvestmentsLiveData.setValue(investments);
                    filterInvestments("all"); // Default filter
                })
                .addOnFailureListener(e -> Log.e(TAG, "loadInvestments: Failed", e))
        ;
    }

    // Mark matured investments as viewed
    public void markMaturedInvestmentsAsViewed() {
        newlyMaturedIds.clear();
        newMaturedInvestmentCount.setValue(0); // Reset badge count
    }

    // Filter investments by status
    public void filterInvestments(String filter) {
        activeFilter = filter; // Update the active filter
        List<Investment> allInvestments = allInvestmentsLiveData.getValue();
        if (allInvestments == null) return;

        List<Investment> filteredList = new ArrayList<>();
        switch (filter) {
            case "pending":
                for (Investment investment : allInvestments) {
                    if (!investment.isMatured()) filteredList.add(investment);
                }
                break;
            case "matured":
                for (Investment investment : allInvestments) {
                    if (investment.isMatured()) filteredList.add(investment);
                }
                break;
            case "all":
            default:
                filteredList = new ArrayList<>(allInvestments);
                break;
        }

        Log.d(TAG, "filterInvestments: Filtered investments -> " + filteredList);
        filteredInvestmentsLiveData.setValue(filteredList);
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

    // Get LiveData for filtered investments
    public LiveData<List<Investment>> getFilteredInvestments() {
        return filteredInvestmentsLiveData;
    }

    public LiveData<Integer> getNewMaturedInvestmentCount() {
        return newMaturedInvestmentCount;
    }

    public LiveData<List<Investment>> getMaturedInvestments() {
        Log.d(TAG, "getMaturedInvestments: Matured Investments > " + maturedInvestmentsLiveData.getValue());
        return maturedInvestmentsLiveData;

    }

    // Get LiveData for total earnings
    public LiveData<Double> getTotalEarningsLiveData() {
        return totalEarningsLiveData;
    }

    public List<String> getNewlyMaturedIds() {
        return newlyMaturedIds;
    }


    public LiveData<List<Investment>> getAllUserInvestments() {
        Log.d(TAG, "getAllUserInvestments: All Investments > " + allUserInvestmentsLiveData.getValue());
        return allUserInvestmentsLiveData;
    }

    public LiveData<Double> getTotalEarningsCount() {
        return totalEarnings;
    }
}
