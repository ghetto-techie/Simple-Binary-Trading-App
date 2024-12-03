package com.big.shamba.data.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationService {
    private final FirebaseFirestore firestore;

    public NotificationService(FirestoreService firestoreService) {
        this.firestore = firestoreService.getFirestore();
    }

    public void sendNotification(String userId, String message) {
        //TODO: Implement Logic to sendNotification
    }

    public void getUserNotifications(String userId) {
        //TODO: Implement fetch notifications
    }
}
