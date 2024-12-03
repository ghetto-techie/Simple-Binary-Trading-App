package com.big.shamba.data.firebase;

import android.util.Log;

import com.big.shamba.utility.FirestoreCollections;
import com.big.shamba.utility.FirestoreUpdateListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

public class FirestoreService {
    private static final String TAG = "FirestoreService";
    private final FirebaseFirestore firestore;

    public FirestoreService() {
        firestore = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    public ListenerRegistration listenForCollectionUpdates(
            Query query,
            FirestoreUpdateListener firestoreUpdateListener
    ) {
        return query
                .addSnapshotListener((documentSnapshots, error) -> {
                    if (error != null) {
                        Log.d(TAG, "listenForCollectionUpdates: Error listening for updates in > ");
                        error.printStackTrace();
                        firestoreUpdateListener.onError(error);
                    }
                    if (documentSnapshots != null) {
                        firestoreUpdateListener.onDataChanged(documentSnapshots.getDocuments());
                        firestoreUpdateListener.onDataChangedStatus(true);
                        Log.d(TAG, "listenForCollectionUpdates: Updates available > " + documentSnapshots.getDocuments());
                    }
                });
    }

    public ListenerRegistration listenForCollectionUpdates(
            String collectionPath,
            FirestoreUpdateListener firestoreUpdateListener
    ) {
        CollectionReference collection = firestore
                .collection(collectionPath);
        return collection
                .addSnapshotListener((documentSnapshots, error) -> {
                    if (error != null) {
                        Log.d(TAG, "listenForCollectionUpdates: Error listening for updates in > " + collectionPath);
                        error.printStackTrace();
                        firestoreUpdateListener.onError(error);
                    }
                    if (documentSnapshots != null) {
                        firestoreUpdateListener.onDataChanged(documentSnapshots.getDocuments());
                        firestoreUpdateListener.onDataChangedStatus(true);
                        Log.d(TAG, "listenForCollectionUpdates: Updates available > " + documentSnapshots.getDocuments());
                    }
                });
    }


    public CollectionReference getUsersCollection() {
        return firestore.collection(FirestoreCollections.USERS);
    }

    public CollectionReference getInvestmentsCollection() {
        return firestore.collection((FirestoreCollections.INVESTMENTS));
    }

    public CollectionReference getWalletTransactions() {
        return firestore.collection(FirestoreCollections.WALLET_TRANSACTIONS);
    }

    public CollectionReference getPackagesCollection() {
        return firestore.collection(FirestoreCollections.PACKAGES);
    }

    public CollectionReference getReferralsCollection() {
        return firestore.collection(FirestoreCollections.REFERRALS);
    }
}
