package com.big.shamba.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.big.shamba.R;
import com.big.shamba.models.Referral;
import com.big.shamba.models.User;
import com.big.shamba.utility.FirestoreCollections;
import com.big.shamba.utility.ReferralCodeGenerator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.util.Patterns;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Random;

public class CreateAccountActivity extends AppCompatActivity {
    private static final String TAG = "CreateAccountActivity";
    public static final String REFERRAL_CODE_INTENT = "REFERRAL_CODE";
    private TextInputLayout firstName;
    private ProgressBar progressBar;
    private Snackbar snackbar;
    private TextInputLayout lastNameTIL;
    private TextInputLayout emailTIL;
    private TextInputLayout passwordTIL;
    private TextInputLayout confirmPasswordTIL;
    private TextInputLayout referralCodeTIL;
    private Button registerButton;
    private MaterialButton loginTV;
    private CollectionReference usersCollectionFirestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    public void setMessage(String msg) {
        Log.d(TAG, "setMessage: ");
        snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();

        usersCollectionFirestore = FirebaseFirestore.getInstance().collection(FirestoreCollections.USERS);

        // Initialize views
        firstName = findViewById(R.id.firstName);
        lastNameTIL = findViewById(R.id.lastName);
        emailTIL = findViewById(R.id.email);
        passwordTIL = findViewById(R.id.password);
        referralCodeTIL = findViewById(R.id.referralCode);
        confirmPasswordTIL = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerButton);
        loginTV = findViewById(R.id.loginTV);

        progressBar = findViewById(R.id.progressBar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Authenticating");
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);

        loginTV
                .setOnClickListener(v -> {
                    Log.d(TAG, "onCreate: Login");
                    startActivity(new Intent(this, LoginActivity.class));
                    finishAffinity();
                })
        ;

        registerButton.setOnClickListener(v -> {
            // Perform field validations
            if (validateFields()) {
                // Fields are valid, proceed with registration
                Log.d(TAG, "onCreate: Fields are valid");
                registerUser();
            }
        });

        String referralCode = getIntent().getStringExtra(REFERRAL_CODE_INTENT);
        if (referralCode != null) {
            Log.d(TAG, "onCreate: Referral Code > " + referralCode);
            referralCodeTIL.getEditText().setText(referralCode);
            Toast.makeText(this, referralCode, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        Log.d(TAG, "validateFields: ");
        String firstNameText = firstName.getEditText().getText().toString().trim();
        String lastNameText = lastNameTIL.getEditText().getText().toString().trim();
        String emailText = emailTIL.getEditText().getText().toString().trim();
        String passwordText = passwordTIL.getEditText().getText().toString().trim();
        String confirmPasswordText = confirmPasswordTIL.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(firstNameText)) {
            firstName.requestFocus();
            firstName.setError("First name is required");
            Log.d(TAG, "validateFields: First name empty");
            return false;
        } else {
            firstName.setError(null);
        }

        if (TextUtils.isEmpty(lastNameText)) {
            lastNameTIL.requestFocus();
            lastNameTIL.setError("Last name is required");
            Log.d(TAG, "validateFields: Last name empty");
            return false;
        } else {
            lastNameTIL.setError(null);
        }

        if (TextUtils.isEmpty(emailText)) {
            emailTIL.requestFocus();
            emailTIL.setError("Email is required");
            Log.d(TAG, "validateFields: Email is empty");
            return false;
        } else {
            emailTIL.setError(null);
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailTIL.requestFocus();
            emailTIL.setError("Invalid email address");
            Log.d(TAG, "validateFields: Email is invalid");
            return false;
        } else {
            emailTIL.setError(null);
        }

        if (TextUtils.isEmpty(passwordText)) {
            passwordTIL.requestFocus();
            passwordTIL.setError("Password is required");
            Log.d(TAG, "validateFields: Password empty");
            return false;
        } else {
            passwordTIL.setError(null);
            if (passwordText.length() < 6) {
                passwordTIL.setError("Password must be at least 6 characters");
                Log.d(TAG, "validateFields: Password too short");
                return false;
            }
        }

        if (!passwordText.equals(confirmPasswordText)) {
            confirmPasswordTIL.requestFocus();
            confirmPasswordTIL.setError("Passwords do not match");
            Log.d(TAG, "validateFields: Password mismatch");
            return false;
        } else {
            confirmPasswordTIL.setError(null);
        }

        // All fields are valid
        return true;
    }

    private void registerUser() {
        Log.d(TAG, "registerUser: ");
        String firstNameText = firstName.getEditText().getText().toString().trim();
        String lastNameText = lastNameTIL.getEditText().getText().toString().trim();
        String emailText = emailTIL.getEditText().getText().toString().trim();
        String passwordText = passwordTIL.getEditText().getText().toString().trim();
        String referralCodeText = referralCodeTIL.getEditText().getText().toString().trim();

        progressDialog.create();
        progressDialog.show();

        registerButton.setEnabled(false);
        loginTV.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // Create user to firebase authentication
        firebaseAuth
                .createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "registerUser: Auth Account created successfully");
                        String userId = task.getResult().getUser().getUid().toString();

                        // Generate referral code
                        String code = ReferralCodeGenerator.generateReferralCode(userId);
                        // Create user object
                        User user = new User(
                                userId,
                                firstNameText,
                                "",
                                lastNameText,
                                emailText,
                                code,
                                "",
                                "",
                                "",
                                0,
                                "",
                                "",
                                null,
                                new Date(System.currentTimeMillis())
                        );

                        // Check if referral code is present on form
                        if (!TextUtils.isEmpty(referralCodeText)) {
                            Log.d(TAG, "registerUser: Referral code > " + referralCodeText);
                            usersCollectionFirestore
                                    .whereEqualTo("code", referralCodeText)
                                    .get()
                                    .addOnCompleteListener(referralTask -> {
                                        if (referralTask.isSuccessful() && !referralTask.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot document : referralTask.getResult()) {
                                                String referrerId = document.getId();
                                                user.setUserReferreeId(referrerId);

                                                // Add referral to Firestore
                                                addReferral(referrerId, userId, firstNameText + " " + lastNameText);
                                            }
                                        }

                                        // Save new user to firestore
                                        saveUserToFirestore(userId, user);
                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        Log.d(TAG, "registerUser: Failed to find referral code > " + e.getMessage());
                                        Toast.makeText(this, "Invalid referral code", Toast.LENGTH_SHORT).show();
                                        saveUserToFirestore(userId, user);
                                    });
                        } else {
                            // Save new user to firestore if no referral code is present
                            saveUserToFirestore(userId, user);
                        }
                    } else {
                        Log.d(TAG, "registerUser: Failed to create Auth Account");
                        String message = task.getException().getMessage();
                        task.getException().printStackTrace();
                        setMessage(message);
                        registerButton.setEnabled(true);
                        loginTV.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                })
        ;
    }

    private void saveUserToFirestore(String userId, User user) {
        Log.d(TAG, "saveUserToFirestore: ");
        usersCollectionFirestore
                .document(userId)
                .set(user)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "registerUser: Successfully Created user in Firestore");
                    Toast.makeText(CreateAccountActivity.this, "Welcome to " + getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "registerUser: Failed to create User in Firestore");
                    e.fillInStackTrace();
                    registerButton.setEnabled(true);
                    loginTV.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                })
        ;
    }

    private void addReferral(String refereeId, String currentUserId, String name) {
        Log.d(TAG, "addReferral: ");
        Referral referral = new Referral(refereeId, currentUserId, name, new Date(System.currentTimeMillis()));
        FirebaseFirestore
                .getInstance()
                .collection(FirestoreCollections.REFERRALS)
                .add(referral)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Referral added successfully to Level 1");
                    } else {
                        Log.d(TAG, "onComplete: Failed to add referral > " + task.getException().getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.d(TAG, "onFailure: Failed to add referral > " + e.getMessage());
                })
        ;
    }

    private String generateReferralCode() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int CODE_LENGTH = 8;
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }
}
