package com.big.shamba.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.R;
import com.big.shamba.data.firebase.AuthService;
import com.big.shamba.ui.MainActivity;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.factories.AuthViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private AuthViewModel authViewModel;
    private TextInputLayout emailTIL, passwordTIL;
    private TextView forgotPasswordButton;
    private MaterialButton loginButton, createAccountButton;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Log.d(TAG, "onCreateView: Login Fragment");
        // Initialize UI components
        emailTIL = view.findViewById(R.id.emailAddress);
        passwordTIL = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.loginButton);
        createAccountButton = view.findViewById(R.id.createAccountButton);
        forgotPasswordButton = view.findViewById(R.id.forgotPassword);
        progressBar = view.findViewById(R.id.progressBar);

        // Initialize ViewModel
        AuthService authService = new AuthService();
        AuthViewModelFactory factory = new AuthViewModelFactory(authService);
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);

        loginButton.setOnClickListener(v -> {
            String email = emailTIL.getEditText().getText().toString().trim();
            String password = passwordTIL.getEditText().getText().toString().trim();

            if (validateInputs(email, password)) {
                progressBar.setVisibility(View.VISIBLE);
                authViewModel.signIn(email, password);
            }
        });

        // Observe authentication status
        authViewModel.getAuthStatus().observe(getViewLifecycleOwner(), status -> {
            progressBar.setVisibility(View.GONE);
            if (status.isSuccess()) {
                navigateToMain();
            } else {
                Snackbar.make(view, status.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        // Navigation buttons
        createAccountButton.setOnClickListener(v -> navigateToRegister());
        forgotPasswordButton.setOnClickListener(v -> navigateToForgotPassword());

        return view;
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailTIL.setError("Please enter your email address");
            return false;
        }
        emailTIL.setError(null);

        if (TextUtils.isEmpty(password)) {
            passwordTIL.setError("Password is required");
            return false;
        }
        passwordTIL.setError(null);

        return true;
    }

    private void navigateToMain() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void navigateToRegister() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.auth_fragment_container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
        Log.d(TAG, "navigateToRegister: Create Account");
    }

    private void navigateToForgotPassword() {
        // Implement ForgotPasswordFragment navigation
    }
}
