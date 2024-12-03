package com.big.shamba.ui.fragments.dashboard.charts;


import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.R;
import com.big.shamba.models.Investment;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.InvestmentViewModel;
import com.big.shamba.ui.viewmodels.ReferralViewModel;
import com.github.mikephil.charting.charts.CombinedChart;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.graphics.Color;
import android.widget.Toast;

import com.big.shamba.models.Referral;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


public class ChartsBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String TAG = "ChartsFragment";
    private CombinedChart combinedChart;
    private InvestmentViewModel investmentViewModel;
    private ReferralViewModel referralViewModel;

    public static ChartsBottomSheetFragment newInstance(String chart) {
        ChartsBottomSheetFragment chartsBottomSheetFragment = new ChartsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable("", chart);
        return chartsBottomSheetFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_charts, container, false);
        combinedChart = view.findViewById(R.id.combinedChart);
        // Initialize view models
        AuthViewModel authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        investmentViewModel = new ViewModelProvider(requireActivity()).get(InvestmentViewModel.class);
        referralViewModel = new ViewModelProvider(requireActivity()).get(ReferralViewModel.class);

        authViewModel
                .getCurrentUser()
                .observe(getViewLifecycleOwner(), currentUser -> {
                    if (currentUser != null) {
                        Log.d(TAG, "onCreateView: User is logged in");
                        String userId = currentUser.getUid();
                        referralViewModel.fetchTotalReferrals(userId);
                        referralViewModel.fetchReferralCommissions(userId);
                    }
                })
        ;

        setupCombinedChart();
        fetchDataAndPopulateChart();
        return view;
    }

    private void setupCombinedChart() {
        combinedChart.getDescription().setEnabled(false);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        boolean isDarkTheme = (requireContext().getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        int textColor = ContextCompat.getColor(requireContext(), isDarkTheme ? R.color.white : R.color.black);
        int gridColor = ContextCompat.getColor(requireContext(), R.color.chart_grid_color);

        // Set axis styling based on theme
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setTextColor(textColor);
        xAxis.setGridColor(gridColor);
        xAxis.setValueFormatter(new DateAxisFormatter());

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setTextColor(textColor);
        leftAxis.setGridColor(gridColor);
        leftAxis.setAxisLineColor(textColor);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Customize Legend
        Legend legend = combinedChart.getLegend();
        legend.setTextColor(textColor);
        legend.setTextSize(12f);

        // Enable interaction features
        combinedChart.setDragEnabled(true);
        combinedChart.setScaleEnabled(true);
        combinedChart.setPinchZoom(true);
    }


    private void fetchDataAndPopulateChart() {
        List<Investment> investments = fetchInvestments();   // Replace with actual fetching
        fetchReferralsAndPopulateChart();
        // Replace with actual fetching
//        List<ReferralCommissionWalletTransaction> commissions = fetchCommissions(); // Replace with actual fetching
//        populateCombinedChart(investments, referrals);
    }


//    private List<ReferralCommissionWalletTransaction> fetchCommissions() {
//        return referralViewModel
//                .getReferralCommissionsLiveData()
//                .getValue();
//    }

    private void fetchReferralsAndPopulateChart() {
        referralViewModel
                .getReferralsLiveData()
                .observe(getViewLifecycleOwner(), referrals -> {
                    if (referrals != null && !referrals.isEmpty()) {
                        Log.d(TAG, "onChanged: Referrals -> " + referrals);
                        populateCombinedChart(fetchInvestments(), referrals);  // Only call once data is ready
                    } else {
                        Log.d(TAG, "onChanged: No referrals data available");
                        Toast.makeText(requireContext(), "No referrals data available", Toast.LENGTH_SHORT).show();
                    }
                })
        ;
    }


    private List<Investment> fetchInvestments() {
        return investmentViewModel
                .getMaturedInvestments()
                .getValue();
    }

    private void populateCombinedChart(
            List<Investment> investments,
            List<Referral> referrals
//            ,List<ReferralCommissionWalletTransaction> commissions
    ) {
        CombinedData combinedData = new CombinedData();

//        combinedData.setData(generateAggregatedReferralLineData(referrals)); // Referrals with daily counts
        combinedData.setData(generateReferralLineData(referrals)); //Referrals
//        combinedData.setData(generateLineData(investments));
//        combinedData.setData(generateBarData(commissions));
//        combinedData.setData(generateBarData(referrals));

        combinedChart.setData(combinedData);
        combinedChart.invalidate(); // Refresh chart
    }

    private LineData generateLineData(List<Investment> investments) {
        List<Entry> lineEntries = new ArrayList<>();
        for (Investment investment : investments) {
            float xValue = investment.getEndDate().getTime();
            float yValue = investment.getAmount();
            lineEntries.add(new Entry(xValue, yValue));
        }
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Investment Earnings");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setLineWidth(2f);
        return new LineData(lineDataSet);
    }

    private BarData generateBarData(List<Referral> referrals) {
        Log.d(TAG, "generateBarData: ");
        List<BarEntry> barEntries = new ArrayList<>();

        if (referrals != null && !referrals.isEmpty()) {
            Log.d(TAG, "generateBarData: Referrals -> " + referrals);
            for (Referral referral : referrals) {
                float xValue = referral.getDateJoined().getTime();  // Use dateJoined as X-value
                barEntries.add(new BarEntry(xValue, 1));  // Use static Y-value, or replace with other value if needed
            }

            BarDataSet barDataSet = new BarDataSet(barEntries, "Referrals");
            barDataSet.setColor(Color.RED);  // Set color for bars
            barDataSet.setValueTextColor(Color.WHITE);  // Text color for bar values
            barDataSet.setValueTextSize(10f);  // Size for bar values text

            // Return BarData with the configured dataset
            return new BarData(barDataSet);
        } else {
            Log.d(TAG, "generateBarData: No referrals data to display");
            Toast.makeText(requireContext(), "No referrals data available", Toast.LENGTH_SHORT).show();
            return new BarData();  // Return empty BarData if no referrals
        }
    }

    private LineData generateReferralLineData(List<Referral> referrals) {
        List<Entry> referralEntries = new ArrayList<>();
        int cumulativeCount = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            referrals.sort(Comparator.comparing(Referral::getDateJoined));
        } else {
            Collections.sort(referrals, new Comparator<Referral>() {
                @Override
                public int compare(Referral r1, Referral r2) {
                    return r1.getDateJoined().compareTo(r2.getDateJoined());
                }
            });
        }

        for (Referral referral : referrals) {
            cumulativeCount++;
            float xValue = referral.getDateJoined().getTime(); // Convert date to timestamp for x-axis
            referralEntries.add(new Entry(xValue, cumulativeCount));
        }

        LineDataSet lineDataSet = new LineDataSet(referralEntries, "Referral Trend Over Time");
        lineDataSet.setColor(Color.MAGENTA);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleColor(Color.MAGENTA);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawValues(false);  // Hides values above each point

        return new LineData(lineDataSet);
    }

    // Aggregates and plots referral data by date
    private LineData generateAggregatedReferralLineData(List<Referral> referrals) {
        // Use a map to store counts by date (timestamp at start of day)
        Map<Long, Integer> sortedDateToReferralCount = getLongIntegerMap(referrals);
        List<Entry> referralEntries = new ArrayList<>();
        int cumulativeCount = 0;

        for (Map.Entry<Long, Integer> entry : sortedDateToReferralCount.entrySet()) {
            cumulativeCount += entry.getValue(); // Running total for cumulative trend
            referralEntries.add(new Entry(entry.getKey(), cumulativeCount));
        }

        LineDataSet lineDataSet = new LineDataSet(referralEntries, "Daily Referral Count");
        lineDataSet.setColor(Color.MAGENTA);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleColor(Color.MAGENTA);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawValues(false);  // Hide values above points to reduce clutter

        return new LineData(lineDataSet);
    }

    private static @NonNull Map<Long, Integer> getLongIntegerMap(List<Referral> referrals) {
        Map<Long, Integer> dateToReferralCount = new HashMap<>();

        // Count referrals for each day
        for (Referral referral : referrals) {
            long dateInMillis = referral.getDateJoined().getTime();
            // Round down to start of day to group all referrals on the same date
            long startOfDay = dateInMillis - (dateInMillis % (24 * 60 * 60 * 1000));

            int count = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                count = dateToReferralCount.getOrDefault(startOfDay, 0);
            }
            dateToReferralCount.put(startOfDay, count + 1);
        }

        // Sort dates for ordered display
        Map<Long, Integer> sortedDateToReferralCount = new TreeMap<>(dateToReferralCount);
        return sortedDateToReferralCount;
    }

    private static class DateAxisFormatter extends ValueFormatter {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());

        @Override
        public String getFormattedValue(float value) {
            return dateFormat.format(new Date((long) value));
        }
    }
}

