package com.big.shamba.ui.fragments.bottomsheets;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.big.shamba.R;
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.ui.viewmodels.InvestmentViewModel;
import com.big.shamba.ui.viewmodels.WalletViewModel;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private TextView packageNameTV;
    private TextView packageTimePeriodTV;
    private TextView packageInterestTV;
    private TextView minAmountTV;
    private TextView maxAmountTV;
    private CandleStickChart candlestickChart;
    private TextView estimatedInterestTV;
    private Slider amountSlider;
    private TextInputLayout investmentAmountTIL;
    private MaterialButton investInPackageBottomSheetButton;
    private InvestmentViewModel investmentViewModel;
    private WalletViewModel walletViewModel;
    private LottieAnimationView animationView;

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

    private List<CandleEntry> generateDummyData(int count) {
        List<CandleEntry> entries = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            float high = random.nextFloat() * 100 + 100; // Random high between 100 and 200
            float low = high - random.nextFloat() * 50; // Random low up to 50 below high
            float open = low + random.nextFloat() * (high - low); // Open between low and high
            float close = low + random.nextFloat() * (high - low); // Close between low and high

            entries.add(new CandleEntry(i, high, low, open, close));
        }

        return entries;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_invest_in_package, container, false);

        packageNameTV = view.findViewById(R.id.packageNameTV);
        packageTimePeriodTV = view.findViewById(R.id.packageTimePeriodTV);
        packageInterestTV = view.findViewById(R.id.packageInterestTV);
        maxAmountTV = view.findViewById(R.id.maxAmountTV);
        minAmountTV = view.findViewById(R.id.minAmountTV);
        estimatedInterestTV = view.findViewById(R.id.estimatedInterestTV);
        candlestickChart = view.findViewById(R.id.candlestickChart);
        amountSlider = view.findViewById(R.id.amountSlider);
        investmentAmountTIL = view.findViewById(R.id.investmentAmountTIL);
        investInPackageBottomSheetButton = view.findViewById(R.id.investInPackageBottomSheetButton);

        setupCandlestickChart(candlestickChart);
        simulateRealTimeUpdates(candlestickChart, 100);

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

                                View dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.layout_loading, null);

                                // Create the dialog
                                AlertDialog progressDialog = new MaterialAlertDialogBuilder(requireActivity())
                                        .setView(dialogView)
                                        .setCancelable(false)
                                        .create();

                                // Access LottieAnimationView and ensure the animation is set correctly
                                animationView = dialogView.findViewById(R.id.loadingAnimationView);
                                animationView.setAnimation(R.raw.loading); // Load animation dynamically
                                animationView.setRepeatCount(LottieDrawable.INFINITE); // Loop the animation
                                animationView.playAnimation();

                                // Show the dialog
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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (handler != null && updateChartTask != null) {
            handler.removeCallbacks(updateChartTask);
        }
    }


    private final Handler handler = new Handler();
    private Runnable updateChartTask;

    private void simulateRealTimeUpdates(CandleStickChart chart, float initialPrice) {
        List<CandleEntry> entries = new ArrayList<>();
        Random random = new Random();
        float[] currentPrice = {initialPrice}; // Use an array to keep it mutable

        updateChartTask = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                // Generate realistic data for the next candlestick
                float open = currentPrice[0];
                float close = open + (random.nextFloat() - 0.5f) * 10;
                float high = Math.max(open, close) + random.nextFloat() * 5;
                float low = Math.min(open, close) - random.nextFloat() * 5;

                entries.add(new CandleEntry(index++, high, low, open, close));
                currentPrice[0] = close;

                // Update chart on UI thread
                updateChart(chart, entries);

                // Schedule the next update
                handler.postDelayed(this, 1000); // 1-second interval
            }
        };

        handler.post(updateChartTask); // Start the periodic task
    }

    private void updateChart(CandleStickChart chart, List<CandleEntry> entries) {
        CandleDataSet dataSet;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            dataSet = (CandleDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.invalidate();
        } else {
            dataSet = new CandleDataSet(entries, "Real-Time Trading Data");
            dataSet.setShadowColor(Color.GRAY);
            dataSet.setDecreasingColor(Color.RED);
            dataSet.setDecreasingPaintStyle(Paint.Style.FILL);
            dataSet.setIncreasingColor(Color.GREEN);
            dataSet.setIncreasingPaintStyle(Paint.Style.FILL);
            dataSet.setNeutralColor(Color.BLUE);
            dataSet.setDrawValues(false);

            CandleData candleData = new CandleData(dataSet);
            chart.setData(candleData);
            chart.invalidate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop the updates to prevent memory leaks
        if (handler != null && updateChartTask != null) {
            handler.removeCallbacks(updateChartTask);
        }
    }

    private void setupCandlestickChart(CandleStickChart chart) {
        List<CandleEntry> dummyData = generateDummyData(50);

        CandleDataSet dataSet = new CandleDataSet(dummyData, "Trading Data");
        dataSet.setShadowColor(requireContext().getColor(R.color.chart_shadow));
        dataSet.setDecreasingColor(requireContext().getColor(R.color.chart_decreasing));
        dataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        dataSet.setIncreasingColor(requireContext().getColor(R.color.chart_increasing));
        dataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        dataSet.setNeutralColor(requireContext().getColor(R.color.chart_text));
        dataSet.setDrawValues(false); // Disable value text on the chart

        CandleData candleData = new CandleData(dataSet);
        chart.setData(candleData);

        // Chart appearance
        chart.setBackgroundColor(requireContext().getColor(R.color.chart_background));
        chart.getDescription().setEnabled(false); // Remove chart description
        chart.setDrawGridBackground(false); // Remove grid background
        chart.setPinchZoom(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // Gestures
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);
        chart.setHighlightPerDragEnabled(true);

        // X-Axis configuration
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(requireContext().getColor(R.color.chart_grid));
        xAxis.setTextColor(requireContext().getColor(R.color.chart_text));
        xAxis.setGranularity(1f);

        // Y-Axis configuration
        YAxis leftAxis = chart.getAxisLeft();
        LimitLine limitLine = new LimitLine(150, "Threshold");
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(requireContext().getColor(R.color.chart_grid));
        leftAxis.setTextColor(requireContext().getColor(R.color.chart_text));
        leftAxis.setGranularityEnabled(true);
        limitLine.setLineColor(Color.RED);
        limitLine.setLineWidth(2f);
        limitLine.setTextColor(Color.RED);
        limitLine.setTextSize(12f);
        leftAxis.addLimitLine(limitLine);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false); // Disable the right Y-axis

        chart.invalidate(); // Refresh the chart
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
