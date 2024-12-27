package com.big.shamba.data.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthService {
    private final FirebaseAuth firebaseAuth;

    public AuthService() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public Task<Void> logInWithEmailAndPassoword(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return null;
                });
    }

    public Task<Void> signUpWithEmailAndPassword(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return null;
                });
    }

    public Task<Void> resetPassword(String email) {
        return firebaseAuth.sendPasswordResetEmail(email)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return null;
                });
    }

    public Task<Void> updatePassword(@NonNull FirebaseUser user, String newPassword) {
        return user.updatePassword(newPassword)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return null;
                });
    }

    public Task<AuthResult> reauthenticate(
            @NonNull FirebaseUser user,
            String email,
            String password
    ) {
        return user.reauthenticateAndRetrieveData(
                        EmailAuthProvider.getCredential(email, password))
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return task.getResult();
                });
    }

    public void signOut() {
        firebaseAuth.signOut();
    }
}
