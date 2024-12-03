package com.big.shamba.data.firebase;

import android.util.Log;


import com.big.shamba.models.Investment;
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.models.Referral;
import com.big.shamba.models.ReferralCommissionWalletTransaction;
import com.big.shamba.models.User;
import com.big.shamba.utility.FirestoreCollections;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class InvestmentService {
    private static final String TAG = "InvestmentService";

    private final FirebaseFirestore firestore;

    private final FirestoreService firestoreService;

    private final WalletService walletService;

    private final InvestmentPackageService investmentPackageService;

    public InvestmentService(
            FirestoreService firestoreService,
            WalletService walletService,
            InvestmentPackageService investmentPackageService
    ) {
        this.firestoreService = firestoreService;
        this.walletService = walletService;
        this.investmentPackageService = investmentPackageService;

        this.firestore = firestoreService.getFirestore();
        Log.d(TAG, "InvestmentService: ");
    }

    public Task<Void> invest(
            String userId,
            String packageId,
            long amount
    ) {
        Log.d(TAG, "invest: ");
        return walletService.getWalletBalance(userId).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }

            double walletBalance = task.getResult();
            if (walletBalance < amount) {
                throw new IllegalArgumentException("Insufficient wallet balance");
            }

            // Check for the first investment outside the transaction
            CollectionReference investmentsCollection = firestore.collection(FirestoreCollections.INVESTMENTS);
            Query firstInvestmentQuery = investmentsCollection.whereEqualTo("userId", userId);

            CollectionReference referralsCollection = firestore.collection(FirestoreCollections.REFERRALS);
            Query referralQuery = referralsCollection.whereEqualTo("referredUserId", userId);

            Task<QuerySnapshot> firstInvestmentTask = firstInvestmentQuery.get();
            Task<QuerySnapshot> referralTask = referralQuery.get();

            return Tasks.whenAllSuccess(firstInvestmentTask, referralTask).continueWithTask(allTasks -> {
                QuerySnapshot firstInvestmentSnapshot = firstInvestmentTask.getResult();
                boolean isFirstInvestment = firstInvestmentSnapshot.isEmpty();

                QuerySnapshot referralSnapshot = referralTask.getResult();
                Referral referral = null;
                if (!referralSnapshot.isEmpty()) {
                    DocumentSnapshot referralDoc = referralSnapshot.getDocuments().get(0);
                    referral = referralDoc.toObject(Referral.class);
                    String documentId = referralDoc.getId();
                    assert referral != null;
                    referral.setDocumentId(documentId);
                }

                final Referral finalReferral = referral; // Make the referral effectively final

                // Fetch the referrer document outside the transaction if referral exists
                Task<DocumentSnapshot> referrerTask = finalReferral != null
                        ? firestore.collection(FirestoreCollections.USERS).document(finalReferral.getUserId()).get()
                        : Tasks.forResult(null);

                return referrerTask.continueWithTask(referrerDocTask -> {
                    DocumentSnapshot referrerSnapshot = referrerDocTask.getResult();
                    return firestore.runTransaction(transaction -> {
                        // Get the package details
                        DocumentReference packageRef = firestore.collection(FirestoreCollections.PACKAGES).document(packageId);
                        DocumentSnapshot investmentPackageSnapshot = transaction.get(packageRef);

                        if (!investmentPackageSnapshot.exists()) {
                            Log.d(TAG, "invest: Investment package not found");
                            throw new IllegalArgumentException("Investment Package not found");
                        }

                        Map<String, Object> packageData = investmentPackageSnapshot.getData();
                        if (packageData == null) {
                            Log.d(TAG, "invest: Invalid package data");
                            throw new IllegalArgumentException("Invalid package data");
                        }

                        // Calculate end date
                        long startDate = System.currentTimeMillis();
                        Number timePeriodNumber = (Number) packageData.get("timePeriod");
                        long timePeriod = timePeriodNumber.longValue();
                        long endDate = startDate + timePeriod * 24 * 60 * 60 * 1000;

                        // Create investment record
                        DocumentReference investmentDocRef = firestore.collection(FirestoreCollections.INVESTMENTS).document();

                        Investment investment = new Investment();
                        investment.setUserId(userId);
                        investment.setPackageId(packageId);
                        investment.setAmount(amount);
                        investment.setStartDate(new Date(startDate));
                        investment.setEndDate(new Date(endDate));
                        investment.setMatured(false);
                        transaction.set(investmentDocRef, investment);

                        // Update user investments
                        DocumentReference userRef = firestore.collection(FirestoreCollections.USERS).document(userId);

                        // Deduct from wallet balance
                        transaction.update(userRef, "walletBalance", FieldValue.increment(-amount));

                        if (isFirstInvestment && finalReferral != null && referrerSnapshot != null) {
                            // Add referral commission for the first investment
                            User referrer = referrerSnapshot.toObject(User.class);
                            if (referrer != null) {
                                walletService.addReferralCommission(transaction, referrer, amount, investmentDocRef.getId(), finalReferral);
                            }
                        }

                        return null;
                    });
                });
            });
        });
    }

    public Task<List<Investment>> fetchMaturedInvestments(String userId) {
        Log.d(TAG, "fetchMaturedInvestments: ");
        CollectionReference investmentsRef = firestore.collection(FirestoreCollections.INVESTMENTS);
        Query query = investmentsRef.whereEqualTo("userId", userId)
                .whereEqualTo("matured", true);

        return query.get().continueWith(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "fetchMaturedInvestments: Could NOT get investments" + task.getException());
                throw task.getException();
            }

            List<Investment> maturedInvestments = new ArrayList<>();
            QuerySnapshot querySnapshot = task.getResult();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Investment investment = document.toObject(Investment.class);
                Log.d(TAG, "fetchMaturedInvestments: Investment[" + document.getId() + "] -> " + investment);
                maturedInvestments.add(investment);
            }
            Log.d(TAG, "fetchMaturedInvestments: All Mature investments" + maturedInvestments);
            return maturedInvestments;
        });
    }

    public Task<Double> calculateProfit(Investment investment) {
        Log.d(TAG, "calculateProfit: ");
        Log.d(TAG, "calculateProfit: Investment -> " + investment);
        TaskCompletionSource<Double> taskCompletionSource = new TaskCompletionSource<>();
        DocumentReference packageRef = firestoreService
                .getPackagesCollection()
                .document(investment.getPackageId().trim());

        packageRef
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "calculateProfit: Could NOT get package -> " + task.getException());
                        taskCompletionSource.setException(task.getException());
                        return;
                    }

                    DocumentSnapshot packageSnapshot = task.getResult();
                    if (!packageSnapshot.exists()) {
                        Log.d(TAG, "calculateProfit: Package not found -> ");
                        taskCompletionSource.setException(new IllegalArgumentException("Package not found"));
                        return;
                    }

                    InvestmentPackage investmentPackage = packageSnapshot.toObject(InvestmentPackage.class);
                    if (investmentPackage != null) {
                        Log.d(TAG, "calculateProfit: Investment package -> " + investmentPackage);
                        double interestRate = investmentPackage.getInterestRate();
                        double amount = investment.getAmount();
                        double profit = amount * (interestRate / 100);

                        Log.d(TAG, "calculateProfit: Amount -> " + amount);
                        Log.d(TAG, "calculateProfit: Interest rate -> " + interestRate);
                        Log.d(TAG, "calculateProfit: Profit " + amount + " x ( " + interestRate + " / 100 ) -> " + profit);
                        taskCompletionSource.setResult(profit);
                    } else {
                        taskCompletionSource.setException(new IllegalArgumentException("Package conversion failed"));
                    }
                })
        ;

        return taskCompletionSource.getTask();
    }

    public Task<Double> fetchTotalEarnings(String userId) {
        Log.d(TAG, "getTotalEarnings: ");
        TaskCompletionSource<Double> taskCompletionSource = new TaskCompletionSource<>();

        fetchMaturedInvestments(userId)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        taskCompletionSource.setException(task.getException());
                        return;
                    }

                    List<Investment> matureInvestments = task.getResult();
                    Log.d(TAG, "getTotalEarnings: Mature investments -> " + matureInvestments);
                    if (matureInvestments == null || matureInvestments.isEmpty()) {
                        taskCompletionSource.setResult(0.0);
                        return;
                    }

                    List<Task<Double>> profitTasks = new ArrayList<>();

                    for (Investment investment : matureInvestments) {
                        profitTasks.add(calculateProfit(investment));
                    }

                    Tasks
                            .whenAllComplete(profitTasks)
                            .addOnCompleteListener(profitTask -> {
                                if (!profitTask.isSuccessful()) {
                                    taskCompletionSource.setException(profitTask.getException());
                                    return;
                                }

                                double totalEarnings = 0.0;
                                for (Task<Double> t : profitTasks) {
                                    if (t.isSuccessful()) {
                                        totalEarnings += t.getResult();
                                    }
                                }
                                Log.d(TAG, "getTotalEarnings: Total earnings -> " + totalEarnings);
                                taskCompletionSource.setResult(totalEarnings);
                            })
                    ;
                })
        ;

        return taskCompletionSource.getTask();
    }

    public ListenerRegistration fetchTotalEarningsRealTime(
            String userId,
            TotalEarningsListener listener
    ) {
        Query query = firestore.collection(FirestoreCollections.INVESTMENTS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("matured", true);

        return query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                listener.onError(e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                List<Investment> investments = querySnapshot.toObjects(Investment.class);

                List<Task<Double>> profitTasks = new ArrayList<>();
                for (Investment investment : investments) {
                    profitTasks.add(calculateProfit(investment));
                }

                Tasks.whenAllComplete(profitTasks)
                        .addOnCompleteListener(profitTask -> {
                            if (!profitTask.isSuccessful()) {
                                listener.onError(profitTask.getException());
                                return;
                            }

                            double totalEarnings = 0.0;
                            for (Task<Double> t : profitTasks) {
                                if (t.isSuccessful()) {
                                    totalEarnings += t.getResult();
                                }
                            }
                            listener.onTotalEarningsChanged(totalEarnings);
                        });
            } else {
                listener.onTotalEarningsChanged(0.0);
            }
        });
    }

    public Task<Boolean> canInvest(
            String userId,
            String packageId
    ) {
        Log.d(TAG, "canInvest: ");
        CollectionReference investments = firestore.collection(FirestoreCollections.INVESTMENTS);
        Query query = investments.whereEqualTo("userId", userId)
                .whereEqualTo("packageId", packageId)
                .whereEqualTo("matured", false);

        return query.get().continueWith(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "canInvest: Could NOT get investments" + task.getException());
                throw task.getException();
            }

            QuerySnapshot querySnapshot = task.getResult();
            return querySnapshot.isEmpty();
        });
    }

    public FirestoreService getFirestoreService() {
        return firestoreService;
    }

    public interface TotalEarningsListener {
        void onTotalEarningsChanged(double totalEarnings);

        void onError(Exception e);
    }
}

