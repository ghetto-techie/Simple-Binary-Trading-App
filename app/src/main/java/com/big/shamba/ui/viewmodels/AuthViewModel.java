package com.big.shamba.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.data.firebase.AuthService;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {
    private final AuthService authService;
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

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

    public void signOut() {
        authService.signOut();
        currentUser.setValue(null);
    }
}
