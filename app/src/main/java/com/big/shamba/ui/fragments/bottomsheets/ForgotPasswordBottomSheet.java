package com.big.shamba.ui.fragments.bottomsheets;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.big.shamba.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "ForgotPasswordBottomShe";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Context context;

    public ForgotPasswordBottomSheet(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(requireContext());

        MaterialButton button = view.findViewById(R.id.forgotPasswordBottomSheetButton);
        TextInputLayout emailAddressBottomSheet = view.findViewById(R.id.emailAddressBottomSheet);

        button.setOnClickListener(v -> {
            // Get the entered email address
            String email = emailAddressBottomSheet.getEditText().getText().toString().trim();

            // Perform input validation
            if (TextUtils.isEmpty(email)) {
                emailAddressBottomSheet.setError("Please enter your email address");
                return;
            } else if (!isValidEmail(email)) {
                emailAddressBottomSheet.setError("Please enter a valid email address");
                return;
            } else {
                emailAddressBottomSheet.setError(null);
            }

            // Send password reset email
            sendPasswordResetEmail(email);

            // Dismiss the bottom sheet after sending the request
            dismiss();
        });
    }

    // Helper method to check if the entered email is valid
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void sendPasswordResetEmail(String email) {
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Show success message
                        new MaterialAlertDialogBuilder(context)
                                .setTitle("Password Recovery")
                                .setMessage("An email has been sent to " + email + " with instructions on how to recover your password.")
                                .setIcon(R.drawable.ic_email)
                                .setPositiveButton("Open Email", (dialogInterface, i) -> {
                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    try {
                                        context.startActivity(intent);
                                    } catch (android.content.ActivityNotFoundException ex) {
                                        Toast.makeText(context, "No email app installed.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .create()
                                .show()
                        ;
                        Log.d(TAG, "sendPasswordResetEmail: Success");
                    } else {
                        // Show error message
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email.";
                        Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }

                });
    }

}
