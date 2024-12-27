package com.big.shamba;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.big.shamba.ui.viewmodels.livedata.NetworkLiveData;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class ElevateCryptoTrade extends Application {

    private NetworkLiveData networkLiveData; // NetworkLiveData instance

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firestore with offline persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

        // Apply saved display mode (light, dark, or system default)
        String displayMode = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("pref_key_display_mode", "system");
        updateDisplayMode(displayMode);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize NetworkLiveData
        networkLiveData = new NetworkLiveData(this);
    }

    public LiveData<Boolean> getNetworkLiveData() {
        return networkLiveData;
    }

    private void updateDisplayMode(String mode) {
        switch (mode) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
