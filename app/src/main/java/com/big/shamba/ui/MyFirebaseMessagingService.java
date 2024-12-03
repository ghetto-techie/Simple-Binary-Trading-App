package com.big.shamba.ui;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle the received message
    }

    @Override
    public void onNewToken(String token) {
        // Handle token refresh
    }
}

