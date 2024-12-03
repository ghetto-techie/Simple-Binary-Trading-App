package com.big.shamba.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.big.shamba.models.User;
import com.big.shamba.data.firebase.UserService;
import com.google.android.gms.tasks.Task;

import java.util.Map;

public class UserViewModel extends ViewModel {
    private final UserService userService;
    private final MutableLiveData<User> userDetails;

    public UserViewModel(UserService userService) {
        this.userService = userService;
        this.userDetails = new MutableLiveData<>();
    }

    public LiveData<User> getUserDetails(String userId) {
        userService.getUserDetails(userId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userDetails.setValue(task.getResult());
            }
        });
        return userDetails;
    }

    public Task<Void> updateUserDetails(String userId, User user) {
        return userService.updateUserDetails(userId, user);
    }

    public Task<Void> updateUserDetails(String userId, Map<String, Object> updates) {
        return userService.updateUserDetails(userId, updates);
    }

    // Method to create a new user
    public Task<Void> createUser(String userId, User user) {
        return userService.createUser(userId, user);
    }
}
