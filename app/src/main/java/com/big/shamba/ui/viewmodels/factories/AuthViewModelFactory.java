package com.big.shamba.ui.viewmodels.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.data.firebase.AuthService;
import com.big.shamba.ui.viewmodels.AuthViewModel;

public class AuthViewModelFactory implements ViewModelProvider.Factory {
    private final AuthService authService;

    public AuthViewModelFactory(AuthService authService) {
        this.authService = authService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            return (T) new AuthViewModel(authService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
