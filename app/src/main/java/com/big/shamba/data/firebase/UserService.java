package com.big.shamba.data.firebase;

import android.util.Log;

import com.big.shamba.models.User;
import com.big.shamba.utility.FirestoreCollections;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class UserService {
    private static final String TAG = "UserService";
    private final FirebaseFirestore firestore;

    public UserService(FirestoreService firestoreService) {
        Log.d(TAG, "UserService: ");
        this.firestore = firestoreService.getFirestore();
    }

    public Task<Void> createUser(String userId, User user) {
        DocumentReference userDocRef = firestore.collection(FirestoreCollections.USERS).document(userId);
        Log.d(TAG, "createUser: ");
        return userDocRef.set(user);
    }

    public Task<User> getUserDetails(String userId) {
        Log.d(TAG, "getUserDetails: ");
        DocumentReference userDocument = firestore.collection(FirestoreCollections.USERS).document(userId);
        return userDocument.get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                return task.getResult().toObject(User.class);
            } else {
                throw new Exception("User not found or task failed");
            }
        });
    }

    public Task<Void> updateUserDetails(String userId, User user) {
        DocumentReference userDocRef = firestore.collection(FirestoreCollections.USERS).document(userId);
        return userDocRef.set(user);
    }

    public Task<Void> updateUserDetails(String userId, Map<String, Object> updates) {
        DocumentReference userDocRef = firestore.collection(FirestoreCollections.USERS).document(userId);
        return userDocRef.update(updates); // Only the fields in 'updates' are modified
    }

}
