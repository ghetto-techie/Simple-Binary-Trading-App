package com.big.shamba.ui.fragments.bottomsheets;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.R;
import com.big.shamba.models.Investment;
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.WalletViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EarningsInvestmentDetailsBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "EarningsInvestmentDetai";
    private static final String ARG_PACKAGE_ID = "packageID";
    private static final String ARG_SHOW_BUTTON = "show";
    private static final String ARG_IMG_URL = "imgUrl";
    private static final String ARG_PACKAGE_NAME = "packageName";
    private static final String ARG_PACKAGE_TYPE = "packageType";
    private static final String ARG_TIME_PERIOD = "timePeriod";
    private static final String ARG_INTEREST_RATE = "interestRate";
    private static final String ARG_AMOUNT_INVESTED = "amountInvested";
    private static final String ARG_START_DATE = "startDate";
    private static final String ARG_END_DATE = "endDate";
    private static final String ARG_INTEREST_EARNED = "interestProfit";
    public static final String ARG_EARNINGS_ADDED = "earningsAdded";
    public static final String ARG_INVESTMENT_ID = "investmentId";

    private ImageView packageImage;
    private TextView packageNameTV;
    private TextView packageTypeTV;
    private TextView packageInterestRateTV;
    private TextView packageTimePeriodTV;
    private TextView investmentAmountTV;
    private TextView investmentInterestEarnedTV;
    private TextView investmentStartDate;
    private TextView investmentEndDateTV;
    private MaterialButton addProfitToWalletBtn;

    private WalletViewModel walletViewModel;
    private AuthViewModel authViewModel;
    private ProgressDialog progressDialog;

    public static EarningsInvestmentDetailsBottomSheet newInstance(
            InvestmentPackage investmentPackage,
            Investment investment,
            boolean showButton
    ) {
        Log.d(TAG, "newInstance: ");
        EarningsInvestmentDetailsBottomSheet fragment = new EarningsInvestmentDetailsBottomSheet();
        Bundle args = new Bundle();
        if (investmentPackage != null) {
            Log.d(TAG, "newInstance: Investment Package -> " + investmentPackage);
            args.putString(ARG_INVESTMENT_ID, (investment.getInvestmentId() == null) ? "" : investment.getInvestmentId());
            args.putBoolean(ARG_SHOW_BUTTON, showButton);
            args.putString(ARG_PACKAGE_ID, investmentPackage.getPackageId());
            args.putString(ARG_IMG_URL, investmentPackage.getImgUrl());
            args.putString(ARG_PACKAGE_NAME, investmentPackage.getName());
            args.putString(ARG_PACKAGE_TYPE, investmentPackage.getType());
            args.putString(ARG_TIME_PERIOD, investmentPackage.getTimePeriod() + " days");
            args.putString(ARG_INTEREST_RATE, investmentPackage.getInterestRate() + "%");
            if (investment != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, d MMM yyyy", Locale.getDefault());
                args.putString(ARG_AMOUNT_INVESTED, String.valueOf(investment.getAmount()));
                args.putString(ARG_START_DATE, dateFormat.format(investment.getStartDate()));
                args.putString(ARG_END_DATE, dateFormat.format(investment.getEndDate()));
                args.putBoolean(ARG_EARNINGS_ADDED, investment.isEarningsAdded());

                double profitOfIndividualInvestment = getProfitOfIndividualInvestment(investment, investmentPackage);
                args.putString(ARG_INTEREST_EARNED, String.valueOf(profitOfIndividualInvestment));
                fragment.setArguments(args);
            }
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_earnings_investment_details, container, false);

        walletViewModel = new ViewModelProvider(requireActivity()).get(WalletViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        packageNameTV = view.findViewById(R.id.packageNameTV);
        packageImage = view.findViewById(R.id.packageImage);
        packageTypeTV = view.findViewById(R.id.packageTypeTV);
        packageInterestRateTV = view.findViewById(R.id.packageInterestRateTV);
        packageTimePeriodTV = view.findViewById(R.id.packageTimePeriodTV);
        investmentAmountTV = view.findViewById(R.id.investmentAmountTV);
        investmentInterestEarnedTV = view.findViewById(R.id.investmentInterestEarnedTV);
        investmentStartDate = view.findViewById(R.id.investmentStartDate);
        investmentEndDateTV = view.findViewById(R.id.investmentEndDateTV);
        addProfitToWalletBtn = view.findViewById(R.id.addProfitToWallet);

        progressDialog = new ProgressDialog(requireContext());

        if (getArguments() != null) {
            Log.d(TAG, "onCreateView: Load Investment details");

            walletViewModel
                    .getAddEarningsResult()
                    .observe(getViewLifecycleOwner(), result -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireActivity(), result, Toast.LENGTH_LONG).show();
                    })
            ;

            String imgUrl = getArguments().getString(ARG_IMG_URL);
            if (imgUrl != null && !imgUrl.isEmpty()) {
                Glide
                        .with(requireContext())
                        .load(imgUrl)
                        .fitCenter()
                        .placeholder(R.color.color_primary_light)
                        .into(packageImage)
                ;
            }

            packageNameTV.setText(getArguments().getString(ARG_PACKAGE_NAME));
            packageTypeTV.setText(getArguments().getString(ARG_PACKAGE_TYPE));
            packageInterestRateTV.setText(getArguments().getString(ARG_INTEREST_RATE));
            packageTimePeriodTV.setText(getArguments().getString(ARG_TIME_PERIOD));
            investmentAmountTV.setText(getArguments().getString(ARG_AMOUNT_INVESTED));
            investmentInterestEarnedTV.setText(getArguments().getString(ARG_INTEREST_EARNED));
            investmentStartDate.setText(getArguments().getString(ARG_START_DATE));
            investmentEndDateTV.setText(getArguments().getString(ARG_END_DATE));

            String investmentId = getArguments().getString(ARG_INVESTMENT_ID);
            boolean isEarningsAdded = getArguments().getBoolean(ARG_EARNINGS_ADDED);

            boolean show = getArguments().getBoolean(ARG_SHOW_BUTTON);
            if (show) {
                addProfitToWalletBtn.setVisibility(View.VISIBLE);
                if (isEarningsAdded) {
                    int backgroundColor = ContextCompat.getColor(requireContext(), R.color.color_error_red);
                    ColorStateList colorStateList = ColorStateList.valueOf(backgroundColor);
                    addProfitToWalletBtn.setBackgroundTintList(colorStateList);
                    addProfitToWalletBtn.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_cancel));
                    addProfitToWalletBtn.setText("Funds already claimed!");
                    addProfitToWalletBtn.setOnClickListener(v -> {
                        Toast.makeText(requireContext(), "Your profits have already been added to your wallet", Toast.LENGTH_LONG).show();
                    });
                } else {
                    addProfitToWalletBtn.setEnabled(true);
                    addProfitToWalletBtn.setText("Save to wallet");
                    addProfitToWalletBtn.setOnClickListener(v -> {
                        String userId = authViewModel.getCurrentUser().getValue().getUid();
                        Log.d(TAG, "addProfitToWalletBtn clicked: userId -> " + userId + ", investmentId -> " + investmentId);
                        progressDialog.setMessage("Adding earnings to wallet...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        walletViewModel.addInvestmentEarningsToWallet(userId, investmentId)
                                .addOnCompleteListener(task -> {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Funds added to wallet successfully", Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    } else {
                                        String errorMessage = task.getException().getMessage();
                                        new MaterialAlertDialogBuilder(requireContext())
                                                .setIcon(R.drawable.ic_cancel)
                                                .setTitle("Funds NOT added")
                                                .setPositiveButton("Ok", ((dialogInterface, i) -> {
                                                    dialogInterface.dismiss();
                                                }))
                                                .setMessage(errorMessage)
                                                .create()
                                                .show()
                                        ;
                                    }
                                })
                        ;
                    });
                }
            } else {
                addProfitToWalletBtn.setVisibility(View.GONE);
            }
        }

        return view;
    }

    private static double getProfitOfIndividualInvestment(Investment investment, InvestmentPackage investmentPackage) {
        Log.d(TAG, "getProfitOfIndividualInvestment: investmentPackage -> " + investmentPackage);
        if (investmentPackage != null) {
            double interestRate = investmentPackage.getInterestRate();
            double amount = investment.getAmount();
            double profit = amount * (interestRate / 100);

            Log.d(TAG, "getProfitOfIndividualInvestment: Amount -> " + amount);
            Log.d(TAG, "getProfitOfIndividualInvestment: Interest rate -> " + interestRate);
            Log.d(TAG, "getProfitOfIndividualInvestment: Profit " + amount + " x ( " + interestRate + " / 100 ) -> " + profit);
            return profit;
        } else return 0;
    }
}
