package com.big.shamba.data.firebase;

import android.util.Log;

import com.big.shamba.models.Investment;
import com.big.shamba.models.InvestmentEarningWalletTransaction;
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.models.Referral;
import com.big.shamba.models.ReferralCommissionWalletTransaction;
import com.big.shamba.models.User;
import com.big.shamba.utility.FirestoreCollections;
import com.big.shamba.utility.TransactionType;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

public class WalletService {
    private static final String TAG = "WalletService";
    private final FirebaseFirestore firestore;
    private final FirestoreService firestoreService;

    public WalletService(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
        this.firestore = firestoreService.getFirestore();
        Log.d(TAG, "WalletService created");
    }

    public Task<Long> getWalletBalance(String userId) {
        Log.d(TAG, "getWalletBalance: Getting wallet balance for user: " + userId);
        DocumentReference userDocRef = firestoreService.getUsersCollection().document(userId);
        return userDocRef.get().continueWith(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "getWalletBalance: Error getting wallet balance -> " + task.getException());
                throw task.getException();
            }

            DocumentSnapshot result = task.getResult(); // Get the document snapshot
            if (result.exists()) {
                User user = result.toObject(User.class);
                long balance = user != null ? user.getWalletBalance() : 0;
                Log.d(TAG, "getWalletBalance: Wallet balance for user: " + userId + " is " + balance);
                return balance;
            } else {
                Log.d(TAG, "getWalletBalance: User document does not exist.");
                throw new Exception("User document does not exist.");
            }

        });
    }

    public Task<Void> addInvestmentEarningsToWallet(
            String userId,
            String investmentId
    ) {
        Log.d(TAG, "addInvestmentEarningsToWallet: userId=" + userId + ", investmentId=" + investmentId);
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        DocumentReference investmentDocRef = firestoreService.getInvestmentsCollection().document(investmentId);
        DocumentReference userDocRef = firestoreService.getUsersCollection().document(userId);
        CollectionReference transactionsCollection = firestoreService.getWalletTransactions();

        // Check if a transaction for this investment already exists
        transactionsCollection
                .whereEqualTo("investmentId", investmentId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        taskCompletionSource.setException(
                                new FirebaseFirestoreException(
                                        "Funds have already been claimed. Check your earnings history as reference. If this is a mistake, please contact us",
                                        FirebaseFirestoreException.Code.ABORTED)
                        );
                    } else {
                        // Proceed with the transaction if no existing transaction is found
                        firestore.runTransaction((Transaction.Function<Void>) transaction -> {
                            Log.d(TAG, "addInvestmentEarningsToWallet: Running transaction...");

                            // Fetch investment and check if it exists and is matured
                            DocumentSnapshot investmentSnapshot = transaction.get(investmentDocRef);
                            if (!investmentSnapshot.exists()) {
                                throw new FirebaseFirestoreException("Investment not found", FirebaseFirestoreException.Code.NOT_FOUND);
                            }
                            Investment investment = investmentSnapshot.toObject(Investment.class);
                            if (investment == null || !investment.isMatured() || investment.isEarningsAdded()) {
                                throw new FirebaseFirestoreException(
                                        "Earnings have already been added or investment has not yet matured.",
                                        FirebaseFirestoreException.Code.ABORTED
                                );
                            }

                            // Fetch the investment package
                            DocumentReference packageRef = firestoreService
                                    .getPackagesCollection()
                                    .document(investment.getPackageId().trim());
                            DocumentSnapshot investmentPackageSnapshot = transaction.get(packageRef);
                            if (!investmentPackageSnapshot.exists()) {
                                throw new FirebaseFirestoreException(
                                        "Investment package not found",
                                        FirebaseFirestoreException.Code.NOT_FOUND
                                );
                            }
                            InvestmentPackage investmentPackage = investmentPackageSnapshot.toObject(InvestmentPackage.class);
                            if (investmentPackage == null) {
                                throw new FirebaseFirestoreException(
                                        "Investment package conversion failed",
                                        FirebaseFirestoreException.Code.NOT_FOUND
                                );
                            }

                            // Calculate profit
                            long profit = (long) getProfitOfIndividualInvestment(investment, investmentPackage);
                            Log.d(TAG, "addInvestmentEarningsToWallet: Profit > " + profit);

                            // Fetch the user
                            DocumentSnapshot userSnapshot = transaction.get(userDocRef);
                            if (!userSnapshot.exists()) {
                                throw new FirebaseFirestoreException("User not found", FirebaseFirestoreException.Code.NOT_FOUND);
                            }
                            User user = userSnapshot.toObject(User.class);
                            if (user == null) {
                                throw new FirebaseFirestoreException("User conversion failed", FirebaseFirestoreException.Code.NOT_FOUND);
                            }

                            // Perform writes
                            // Update the investment to mark earnings as added
                            transaction.update(investmentDocRef, "earningsAdded", true);
                            Log.d(TAG, "addInvestmentEarningsToWallet: earningsAdded set to true");

                            // Update the user's wallet balance
                            long newBalance = user.getWalletBalance() + profit;
                            Log.d(TAG, "addInvestmentEarningsToWallet: New Balance > " + newBalance);
                            transaction.update(userDocRef, "walletBalance", newBalance);

                            // Add a transaction record
                            InvestmentEarningWalletTransaction transactionRecord =
                                    new InvestmentEarningWalletTransaction(
                                            userId,
                                            (long) profit,
                                            TransactionType.INVESTMENT_EARNING,
                                            investmentId
                                    );
                            DocumentReference transactionDocRef = firestoreService.getWalletTransactions().document();
                            transaction.set(transactionDocRef, transactionRecord);

                            return null;
                        }).addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Investment earnings added to wallet successfully.");
                            taskCompletionSource.setResult(null);
                        }).addOnFailureListener(e -> {
                            Log.d(TAG, "Error adding investment earnings to wallet: ", e);
                            Log.d(TAG, "Error adding investment earnings to wallet: Error > " + e.getMessage());
                            e.printStackTrace();
                            taskCompletionSource.setException(e);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error checking for existing transaction: ", e);
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }

    public void addReferralCommission(
            Transaction transaction,
            User referrer,
            long investmentAmount,
            String investmentId,
            Referral referral
    ) {
        CollectionReference transactionsCollection = firestore.collection(FirestoreCollections.WALLET_TRANSACTIONS);

        long commission = investmentAmount * 10 / 100; // 10% commission
        long newBalance = referrer.getWalletBalance() + commission;
        transaction.update(firestore.collection(FirestoreCollections.USERS).document(referral.getUserId()), "walletBalance", newBalance);

        // Add referral commission transaction record
        ReferralCommissionWalletTransaction transactionRecord =
                new ReferralCommissionWalletTransaction(
                        referral.getUserId(),
                        commission,
                        referral.getDocumentId() != null ? referral.getDocumentId() : "",
                        investmentId
                );

        DocumentReference transactionDocRef = transactionsCollection.document();
        transaction.set(transactionDocRef, transactionRecord);
    }

    private double getProfitOfIndividualInvestment(
            Investment investment,
            InvestmentPackage investmentPackage
    ) {
        Log.d(TAG, "getProfitOfIndividualInvestment: investmentPackage -> " + investmentPackage);
        if (investmentPackage != null) {
            Log.d(TAG, "calculateProfit: Investment package -> " + investmentPackage);
            double interestRate = investmentPackage.getInterestRate();
            double amount = investment.getAmount();
            double profit = amount * (interestRate / 100);

            Log.d(TAG, "getProfitOfIndividualInvestment: Amount -> " + amount);
            Log.d(TAG, "getProfitOfIndividualInvestment: Interest rate -> " + interestRate);
            Log.d(TAG, "getProfitOfIndividualInvestment: Profit " + amount + " x ( " + interestRate + " / 100 ) -> " + profit);
            return profit;
        } else return 0;
    }


    public ListenerRegistration listenForWalletBalanceChanges(
            String userId,
            WalletBalanceUpdateListener walletBalanceUpdateListener
    ) {
        Log.d(TAG, "listenForWalletBalanceChanges: Listening for wallet balance changes for user: " + userId);
        DocumentReference userDocRef = firestoreService.getUsersCollection().document(userId);
        return userDocRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.d(TAG, "listenForWalletBalanceChanges: Error listening for wallet balance changes -> " + e.getMessage());
                walletBalanceUpdateListener.onError(e);
            }
            if (snapshot != null && snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                if (user != null) {
                    Log.d(TAG, "listenForWalletBalanceChanges: Wallet balance updated for user: " + userId + " to " + user.getWalletBalance());
                    walletBalanceUpdateListener.onBalanceChanged(user.getWalletBalance());
                }
            }
        });
    }

    public interface WalletBalanceUpdateListener {
        void onBalanceChanged(long balance);

        void onError(Exception e);
    }
}
