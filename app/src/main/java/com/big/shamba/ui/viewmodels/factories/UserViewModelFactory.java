package com.big.shamba.ui.viewmodels.factories;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.data.firebase.UserService;
import com.big.shamba.ui.viewmodels.UserViewModel;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final UserService userService;

    public UserViewModelFactory(UserService userService) {
        this.userService = userService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(userService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
