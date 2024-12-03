package com.big.shamba.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.R;
import com.big.shamba.data.firebase.AuthService;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.factories.AuthViewModelFactory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";
    public static final String REFERRAL_CODE_INTENT = "REFERRAL_CODE";
    private AuthViewModel authViewModel;
    private AuthService authService;
    private AuthViewModelFactory authViewModelFactory;

    private String referralCode = null;
    private static final int SPLASH_TIMEOUT = 3500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(TAG, "onCreate: ");

        authService = new AuthService();
        authViewModelFactory = new AuthViewModelFactory(authService);
        authViewModel = new ViewModelProvider(this, authViewModelFactory).get(AuthViewModel.class);

        // Check if the app was launched through a dynamic link
        if (getIntent() != null && getIntent().getData() != null) {
            Log.d(TAG, "onCreate: Intent > " + getIntent().getData().toString());
            handleDynamicLink();
        } else {
            proceedNormally();
        }
    }

    private void handleDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        Log.d(TAG, "Deep link URL: " + (deepLink != null ? deepLink.toString() : "null"));
                        if (deepLink != null) {
                            referralCode = deepLink.getQueryParameter("referralCode");
                            Log.d(TAG, "Referral code from dynamic link: " + referralCode);
                        } else {
                            Log.d(TAG, "No deep link found");
                        }
                    } else {
                        Log.d(TAG, "No pending dynamic link data");
                    }
                    proceedBasedOnAuthStatus();
                })
                .addOnFailureListener(this, e -> {
                    Log.w(TAG, "getDynamicLink:onFailure", e);
                    proceedNormally();
                });
    }

    private void proceedBasedOnAuthStatus() {
        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // User is signed in, prompt to sign out
                promptSignOut();
            } else {
                // User is signed out, launch CreateAccountActivity with referral code
                launchCreateAccountActivity();
            }
        });
    }

    private void promptSignOut() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Sign Out Required")
                .setMessage("You need to sign out to use the referral code and create a new account. Do you want to sign out?")
                .setPositiveButton("Sign Out", (dialog, which) -> {
                    authViewModel.signOut();
                    launchCreateAccountActivity();
                })
                .setNegativeButton("Cancel", (dialog, which) -> launchMainActivity())
                .setOnDismissListener(dialog -> finish()) // Ensure the activity finishes after dialog is dismissed
                .show();
    }

    private void launchCreateAccountActivity() {
        Log.d(TAG, "launchCreateAccountActivity: Referral Code > " + referralCode);
        Intent intent = new Intent(this, CreateAccountActivity.class);
        if (referralCode != null) {
            intent.putExtra(REFERRAL_CODE_INTENT, referralCode);
        }
        startActivity(intent);
        finish();
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void proceedNormally() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIMEOUT);
    }
}
