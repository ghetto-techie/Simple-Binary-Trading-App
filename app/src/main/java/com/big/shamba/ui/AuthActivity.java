package com.big.shamba.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.big.shamba.R;
import com.big.shamba.ui.fragments.LoginFragment;

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        Log.d(TAG, "onCreate: ");
        // Initialize the NavHostFragment for navigation
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.auth_fragment_container, new LoginFragment())
                    .commit();
            Log.d(TAG, "onCreate: Login");
        }
    }
}