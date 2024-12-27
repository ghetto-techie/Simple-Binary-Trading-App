package com.big.shamba.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.data.firebase.AuthService;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {
    private final AuthService authService;
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private final MutableLiveData<AuthStatus> authStatus = new MutableLiveData<>();

    public AuthViewModel(AuthService authService) {
        this.authService = authService;
        loadCurrentUser();
    }

    private void loadCurrentUser() {
        currentUser.setValue(authService.getCurrentUser());
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public LiveData<AuthStatus> getAuthStatus() {
        return authStatus;
    }

    public void signIn(String email, String password) {
        authService.logInWithEmailAndPassoword(email, password)
                .addOnSuccessListener(task -> authStatus.setValue(new AuthStatus(true, null)))
                .addOnFailureListener(e -> authStatus.setValue(new AuthStatus(false, e.getMessage())));
    }

    public void signUp(String email, String password) {
        authService.signUpWithEmailAndPassword(email, password)
                .addOnSuccessListener(task -> authStatus.setValue(new AuthStatus(true, null)))
                .addOnFailureListener(e -> authStatus.setValue(new AuthStatus(false, e.getMessage())));
    }

    public void resetPassword(String email) {
        authService.resetPassword(email)
                .addOnSuccessListener(task -> authStatus.setValue(new AuthStatus(true, "Reset email sent")))
                .addOnFailureListener(e -> authStatus.setValue(new AuthStatus(false, e.getMessage())));
    }

    public void signOut() {
        authService.signOut();
        currentUser.setValue(null);
    }
}

