package com.big.shamba.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.R;
import com.big.shamba.data.firebase.AuthService;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.factories.AuthViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterFragment extends Fragment {
    private AuthViewModel authViewModel;
    private TextInputLayout emailTIL, passwordTIL, confirmPasswordTIL;
    private MaterialButton registerButton, loginButton;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize UI components
        emailTIL = view.findViewById(R.id.email);
        passwordTIL = view.findViewById(R.id.password);
        confirmPasswordTIL = view.findViewById(R.id.confirmPassword);
        registerButton = view.findViewById(R.id.registerButton);
        loginButton = view.findViewById(R.id.loginTV);
        progressBar = view.findViewById(R.id.progressBar);

        // Initialize ViewModel
        AuthService authService = new AuthService();
        AuthViewModelFactory factory = new AuthViewModelFactory(authService);
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);

        registerButton.setOnClickListener(v -> {
            String email = emailTIL.getEditText().getText().toString().trim();
            String password = passwordTIL.getEditText().getText().toString().trim();
            String confirmPassword = confirmPasswordTIL.getEditText().getText().toString().trim();

            if (validateInputs(email, password, confirmPassword)) {
                progressBar.setVisibility(View.VISIBLE);
                authViewModel.signUp(email, password);
            }
        });

        authViewModel.getAuthStatus().observe(getViewLifecycleOwner(), status -> {
            progressBar.setVisibility(View.GONE);
            if (status.isSuccess()) {
                navigateToLogin();
            } else {
                Snackbar.make(view, status.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        loginButton.setOnClickListener(v -> navigateToLogin());

        return view;
    }

    private boolean validateInputs(String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTIL.setError("Invalid email address");
            return false;
        }
        emailTIL.setError(null);

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordTIL.setError("Password must be at least 6 characters");
            return false;
        }
        passwordTIL.setError(null);

        if (!password.equals(confirmPassword)) {
            confirmPasswordTIL.setError("Passwords do not match");
            return false;
        }
        confirmPasswordTIL.setError(null);

        return true;
    }

    private void navigateToLogin() {
        getParentFragmentManager().popBackStack();
    }
}
