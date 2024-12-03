package com.big.shamba.ui.fragments.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.R;
import com.big.shamba.data.firebase.FirestoreService;
import com.big.shamba.data.firebase.UserService;
import com.big.shamba.models.User;
import com.big.shamba.ui.viewmodels.UserViewModel;
import com.big.shamba.ui.viewmodels.factories.UserViewModelFactory;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PersonalInformationFragment extends Fragment {
    private static final String TAG = "PersonalInformationFrag";
    private static final int UPLOAD_DOCUMENT_REQUEST_CODE = 100;

    private TextInputEditText firstNameInput, middleNameInput, lastNameInput, emailInput, phoneNumberInput, idNumberInput;
    private TextInputLayout firstNameLayout, lastNameLayout, emailLayout, phoneNumberLayout, idNumberLayout;
    private TextView dobTV;
    private FirebaseAuth auth;
    private UserViewModel userViewModel;
    private Uri uploadedDocumentUri = null; // To store uploaded document Uri
    private User currentUser = null;
    private ProgressDialog progressDialog;

    private Date selectedDob = null; // Variable to store the selected date of birth

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_information, container, false);
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this,
                new UserViewModelFactory(
                        new UserService(new FirestoreService())
                )).get(UserViewModel.class)
        ;

        // Initialize input fields
        firstNameInput = view.findViewById(R.id.firstNameInput);
        middleNameInput = view.findViewById(R.id.middleNameInput);
        lastNameInput = view.findViewById(R.id.lastNameInput);
        emailInput = view.findViewById(R.id.emailInput);
        phoneNumberInput = view.findViewById(R.id.phoneNumberInput);
        idNumberInput = view.findViewById(R.id.idNumberInput);
        dobTV = view.findViewById(R.id.dobTV);

        // Initialize Layouts for error handling
        firstNameLayout = view.findViewById(R.id.firstName);
        lastNameLayout = view.findViewById(R.id.lastName);
        emailLayout = view.findViewById(R.id.email);
        phoneNumberLayout = view.findViewById(R.id.phoneNumber);
        idNumberLayout = view.findViewById(R.id.idNumber);

        // Load existing user data
        loadUserData();

        // Setup Date of Birth picker
        view.findViewById(R.id.pickDOBButton).setOnClickListener(v -> showDatePicker());

        // Setup Document ID upload
//        view.findViewById(R.id.uploadDocumentButton).setOnClickListener(v -> uploadDocument());

        // Save button listener
        view.findViewById(R.id.savePersonalInformationBtn).setOnClickListener(v -> saveUserInformation());

        return view;
    }

    private void loadUserData() {
        progressDialog.show();

        // Get the user's UID from FirebaseAuth
        String userId = auth.getCurrentUser().getUid();

        // Fetch user data from Firestore using ViewModel
        userViewModel
                .getUserDetails(userId)
                .observe(getViewLifecycleOwner(), user -> {
                    progressDialog.dismiss();
                    if (user != null) {
                        currentUser = user;
                        // Populate input fields with user data
                        firstNameInput.setText(user.getFirstName());
                        middleNameInput.setText(user.getMiddleName());
                        lastNameInput.setText(user.getLastName());
                        emailInput.setText(user.getEmail());
                        phoneNumberInput.setText(user.getPhoneNumber());
                        idNumberInput.setText(user.getIdNumber());
                        dobTV.setText((user.getDob() != null) ? user.getDob().toString() : "");
                    } else {
                        showErrorDialog();
                    }
                })
        ;
    }

    private void showErrorDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Error")
                .setMessage("Oops! An error has occurred")
                .setPositiveButton("Go back", (dialog, which) -> {
                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .remove(this)
                            .commit();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void saveUserInformation() {
        progressDialog.show();

        // Collect data from input fields
        String firstName = firstNameInput.getText().toString().trim();
        String middleName = middleNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String idNumber = idNumberInput.getText().toString().trim();

        // Ensure date of birth is set if updated
        if (selectedDob == null && currentUser.getDob() != null) {
            selectedDob = currentUser.getDob();
        }

        // Validate input fields
        if (!validateInput(firstName, lastName, currentUser.getEmail(), phoneNumber, idNumber, dobTV.getText().toString())) {
            progressDialog.dismiss();
            return; // Exit if validation fails
        }

        // Build a map of only the modified fields
        Map<String, Object> updates = new HashMap<>();
        if (!firstName.equals(currentUser.getFirstName())) updates.put("firstName", firstName);
        if (!middleName.equals(currentUser.getMiddleName())) updates.put("middleName", middleName);
        if (!lastName.equals(currentUser.getLastName())) updates.put("lastName", lastName);
        if (!phoneNumber.equals(currentUser.getPhoneNumber())) updates.put("phoneNumber", phoneNumber);
        if (!idNumber.equals(currentUser.getIdNumber())) updates.put("idNumber", idNumber);
        if (selectedDob != null && !selectedDob.equals(currentUser.getDob())) updates.put("dob", selectedDob);
        if (uploadedDocumentUri != null) updates.put("documentUri", uploadedDocumentUri.toString());

        // If no fields are updated, display a message and exit
        if (updates.isEmpty()) {
            Toast.makeText(getContext(), "No changes detected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        // Get the user's UID from FirebaseAuth
        String userId = auth.getCurrentUser().getUid();

        // Update only the modified fields in Firestore
        userViewModel.updateUserDetails(userId, updates).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Information updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String firstName, String lastName, String email, String phoneNumber, String idNumber, String dob) {
        boolean isValid = true;

        // Validate first name
        if (TextUtils.isEmpty(firstName)) {
            firstNameLayout.setError("First name is required");
            isValid = false;
        } else {
            firstNameLayout.setError(null);
        }

        // Validate last name
        if (TextUtils.isEmpty(lastName)) {
            lastNameLayout.setError("Last name is required");
            isValid = false;
        } else {
            lastNameLayout.setError(null);
        }

        // Validate email format
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        // Validate phone number
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberLayout.setError("Phone number is required");
            isValid = false;
        } else {
            phoneNumberLayout.setError(null);
        }

        // Validate ID number
        if (TextUtils.isEmpty(idNumber)) {
            idNumberLayout.setError("ID number is required");
            isValid = false;
        } else {
            idNumberLayout.setError(null);
        }

        // Validate Date of Birth
        if (dob.equals("--/--/--")) {
            Toast.makeText(getContext(), "Please select a date of birth", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker
                .addOnPositiveButtonClickListener(selection -> {
                    selectedDob = new Date(selection);
                    String selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                    dobTV.setText(selectedDate);
                });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void uploadDocument() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // You can restrict this to specific file types like "application/pdf" or "image/*"
        startActivityForResult(intent, UPLOAD_DOCUMENT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPLOAD_DOCUMENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uploadedDocumentUri = data.getData(); // Store the URI of the uploaded document
                Toast.makeText(getContext(), "Document uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
