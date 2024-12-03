package com.big.shamba.ui.fragments.bottomsheets;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.R;
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.ui.viewmodels.InvestmentViewModel;
import com.big.shamba.ui.viewmodels.WalletViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InvestBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "InvestBottomSheet";

    private static final String ARG_PACKAGE_ID = "packageID";
    private static final String ARG_PACKAGE_IMAGE = "packageImage";
    private static final String ARG_PACKAGE_NAME = "packageName";
    private static final String ARG_TIME_PERIOD = "timePeriod";
    private static final String ARG_INTEREST_RATE = "interestRate";
    private static final String ARG_MIN_VALUE = "minValue";
    private static final String ARG_MAX_VALUE = "maxValue";

    private String packageID;
    private String packageName;
    private String packageImage;
    private String timePeriod;
    private float interestRate;
    private float minValue;
    private float maxValue;
    private long walletBalance;

    private ImageView packageImageView;
    private TextView packageNameTV;
    private TextView packageTimePeriodTV;
    private TextView packageInterestTV;
    private TextView minAmountTV;
    private TextView maxAmountTV;
    private TextView estimatedInterestTV;
    private Slider amountSlider;
    private TextInputLayout investmentAmountTIL;
    private MaterialButton investInPackageBottomSheetButton;
    private InvestmentViewModel investmentViewModel;
    private WalletViewModel walletViewModel;

    public static InvestBottomSheet newInstance(InvestmentPackage investmentPackage) {
        Log.d(TAG, "newInstance: ");
        InvestBottomSheet fragment = new InvestBottomSheet();
        Bundle args = new Bundle();
        if (investmentPackage != null) {
            Log.d(TAG, "newInstance: Investment Package -> " + investmentPackage.toString());
            args.putString(ARG_PACKAGE_ID, investmentPackage.getPackageId());
            args.putString(ARG_PACKAGE_IMAGE, investmentPackage.getImgUrl());
            args.putString(ARG_PACKAGE_NAME, investmentPackage.getName());
            args.putString(ARG_TIME_PERIOD, investmentPackage.getTimePeriod() + " business days");
            args.putFloat(ARG_INTEREST_RATE, investmentPackage.getInterestRate());
            args.putFloat(ARG_MIN_VALUE, (float) investmentPackage.getRange().getMin());
            args.putFloat(ARG_MAX_VALUE, (float) investmentPackage.getRange().getMax());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        if (getArguments() != null) {
            packageID = getArguments().getString(ARG_PACKAGE_ID);
            packageImage = getArguments().getString(ARG_PACKAGE_IMAGE);
            packageName = getArguments().getString(ARG_PACKAGE_NAME);
            timePeriod = getArguments().getString(ARG_TIME_PERIOD);
            interestRate = getArguments().getFloat(ARG_INTEREST_RATE);
            minValue = getArguments().getFloat(ARG_MIN_VALUE);
            maxValue = getArguments().getFloat(ARG_MAX_VALUE);
        }

        // Initialize the ViewModels
        investmentViewModel = new ViewModelProvider(requireActivity()).get(InvestmentViewModel.class);
        walletViewModel = new ViewModelProvider(requireActivity()).get(WalletViewModel.class);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            walletViewModel.fetchWalletBalance(userId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_invest_in_package, container, false);

        packageImageView = view.findViewById(R.id.investInPackageImage);
        packageNameTV = view.findViewById(R.id.packageNameTV);
        packageTimePeriodTV = view.findViewById(R.id.packageTimePeriodTV);
        packageInterestTV = view.findViewById(R.id.packageInterestTV);
        maxAmountTV = view.findViewById(R.id.maxAmountTV);
        minAmountTV = view.findViewById(R.id.minAmountTV);
        estimatedInterestTV = view.findViewById(R.id.estimatedInterestTV);
        amountSlider = view.findViewById(R.id.amountSlider);
        investmentAmountTIL = view.findViewById(R.id.investmentAmountTIL);
        investInPackageBottomSheetButton = view.findViewById(R.id.investInPackageBottomSheetButton);

        Glide
                .with(requireContext())
                .load(packageImage)
                .placeholder(R.color.color_primary_light)
                .fitCenter()
                .into(packageImageView)
        ;
        packageNameTV.setText(packageName);
        packageTimePeriodTV.setText(timePeriod);
        packageInterestTV.setText(interestRate + "% interest rate");

        // Set slider range from the passed arguments
        amountSlider.setValueFrom(minValue);
        amountSlider.setValueTo(maxValue);
        amountSlider.setValue(maxValue / 2);

        maxAmountTV.setText(String.valueOf(maxValue));
        minAmountTV.setText(String.valueOf(minValue));

        EditText investmentAmountTILEditText = investmentAmountTIL.getEditText();
        if (investmentAmountTILEditText != null) {
            investmentAmountTILEditText.setText(String.format("%.2f", amountSlider.getValue()));
        }

        // Calculate and update the estimated interest
        updateEstimatedInterest(amountSlider.getValue());

        // Add an OnChangeListener to update the investmentAmountTIL and estimatedInterestTV when the slider value changes
        amountSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (investmentAmountTILEditText != null) {
                investmentAmountTILEditText.setText(String.format("%.2f", value));
            }
            updateEstimatedInterest(value);
        });

        // Add a TextWatcher to the EditText to update the estimated interest when the value changes
        if (investmentAmountTILEditText != null) {
            investmentAmountTILEditText.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        float value = Float.parseFloat(s.toString());
                        updateEstimatedInterest(value);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid number format", e);
                    }
                }
            });
        }

        // Observe the wallet balance and update the local variable
        walletViewModel
                .getWalletBalance()
                .observe(getViewLifecycleOwner(), balance -> {
                    walletBalance = balance;
                })
        ;

        // Handle the investment button click
        investInPackageBottomSheetButton
                .setOnClickListener(v -> {
                    if (investmentAmountTILEditText != null) {
                        String inputText = investmentAmountTILEditText.getText().toString();
                        if (validateAmount(inputText)) {
                            float amount = Float.parseFloat(inputText);
                            if (amount > walletBalance) {
                                investmentAmountTIL.setError("Insufficient funds. Your current balance is " + walletBalance);
                                Log.d(TAG, "onCreateView: Insufficient funds");
                            } else {
                                investmentAmountTIL.setError(null); // Clear the error

                                // Show a progress dialog
                                AlertDialog progressDialog = new MaterialAlertDialogBuilder(requireActivity())
                                        .setView(R.layout.layout_loading)
                                        .setCancelable(false)
                                        .create();
                                progressDialog.show();

                                // Disable the button to prevent multiple clicks
                                investInPackageBottomSheetButton.setEnabled(false);

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser != null) {
                                    String userId = currentUser.getUid();
                                    investmentViewModel
                                            .invest(userId, packageID, (long) amount)
                                            .addOnCompleteListener(task -> {
                                                progressDialog.dismiss(); // Dismiss the progress dialog

                                                if (task.isSuccessful()) {
                                                    // Handle success
                                                    Log.d(TAG, "Investment successful");
                                                    dismiss(); // Close the BottomSheet
                                                    Toast.makeText(requireContext(), "Investment Successful.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Handle failure
                                                    Log.e(TAG, "Investment failed", task.getException());
                                                    investInPackageBottomSheetButton.setEnabled(true); // Re-enable the button
                                                    Toast.makeText(getContext(), "Investment failed. Please try again.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Log.d(TAG, "onCreateView: CurrentUser is NULL");
                                }
                            }
                        }
                    }
                })
        ;

        return view;
    }

    private void updateEstimatedInterest(float amount) {
        float estimatedInterest = amount * (interestRate / 100);
        estimatedInterestTV.setText(String.format("Est. Interest: %.2f", estimatedInterest));
    }

    private boolean validateAmount(String amountStr) {
        try {
            float amount = Float.parseFloat(amountStr);
            if (amount < minValue || amount > maxValue) {
                investmentAmountTIL.setError("Amount must be between " + minValue + " and " + maxValue);
                return false;
            } else {
                investmentAmountTIL.setError(null); // Clear the error
                return true;
            }
        } catch (NumberFormatException e) {
            investmentAmountTIL.setError("Invalid amount");
            return false;
        }
    }

    // SimpleTextWatcher class to avoid implementing all methods of TextWatcher
    private abstract class SimpleTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(android.text.Editable s) {
        }
    }
}
