package com.big.shamba.utility;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface FirestoreUpdateListener {
    void onDataChanged(List<DocumentSnapshot> documentSnapshots);

    // True if there is an update
    void onDataChangedStatus(boolean isUpdateAvailable);

    void onError(Exception e);
}
