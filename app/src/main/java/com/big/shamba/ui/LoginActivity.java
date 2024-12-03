package com.big.shamba.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.big.shamba.R;
import com.big.shamba.ui.fragments.bottomsheets.ForgotPasswordBottomSheet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private MaterialButton signInBtn;
    private TextView forgotPasswordTV;
    private ForgotPasswordBottomSheet forgotPasswordBottomSheet;
    private MaterialButton createAccountTV;
    private TextInputLayout emailTIL;
    private TextInputLayout passwordTIL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        signInBtn = findViewById(R.id.loginButton);
        emailTIL = findViewById(R.id.emailAddress);
        passwordTIL = findViewById(R.id.password);
        createAccountTV = findViewById(R.id.createAccountButton);
        forgotPasswordTV = findViewById(R.id.forgotPassword);

        forgotPasswordBottomSheet = new ForgotPasswordBottomSheet(this);
        forgotPasswordTV
                .setOnClickListener(v -> {
                    forgotPasswordBottomSheet.show(getSupportFragmentManager(), forgotPasswordBottomSheet.getTag());
                })
        ;

        // Inside the sign-in button click listener
        signInBtn
                .setOnClickListener(v -> {
                    signInBtn.setEnabled(false);
                    // Get the email and password from the input fields
                    String email = emailTIL.getEditText().getText().toString().trim();
                    String password = passwordTIL.getEditText().getText().toString().trim();

                    // Validate the input
                    if (TextUtils.isEmpty(email)) {
                        emailTIL.requestFocus();
                        emailTIL.setError("Please enter your email address");
                        signInBtn.setEnabled(true);
                        return;
                    } else {
                        emailTIL.setError(null);
                    }

                    if (TextUtils.isEmpty(password)) {
                        passwordTIL.requestFocus();
                        passwordTIL.setError("Password is required");
                        signInBtn.setEnabled(true);
                        return;
                    } else {
                        passwordTIL.setError(null);
                    }

                    // Show a progress dialog
                    ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Authenticating", "Signing in...", true);
                    progressDialog.setCancelable(false);
                    // Perform the sign-in using Firebase Auth
                    firebaseAuth
                            .signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authResult -> {
                                Log.d(TAG, "onSuccess: Successful sign in");
                                // Dismiss the progress dialog
                                progressDialog.dismiss();

                                // Start the MainActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                                // Finish the LoginActivity to remove it from the back stack
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                signInBtn.setEnabled(true);
                                // Display a Snackbar with the error message
                                Snackbar.make(v, "Sign-in failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            })
                    ;
                });

        createAccountTV
                .setOnClickListener(v -> {
                    startActivity(new Intent(this, CreateAccountActivity.class));
                    finishAffinity();
                })
        ;
    }
}
