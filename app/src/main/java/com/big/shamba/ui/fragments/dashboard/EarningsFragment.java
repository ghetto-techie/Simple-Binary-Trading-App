package com.big.shamba.ui.fragments.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.Investment;
import com.big.shamba.ui.adapter.recyclerview.EarningRecyclerViewAdapter;
import com.big.shamba.ui.viewmodels.InvestmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class EarningsFragment extends Fragment {
    private static final String TAG = "EarningsFragment";
    public static final String ARGS_INVESTMENT_LIST = "investmentList";

    private RecyclerView recyclerView;
    private EarningRecyclerViewAdapter earningRecyclerViewAdapter;
    private InvestmentViewModel investmentViewModel;
    private List<Investment> investments = new ArrayList<>();
    private View emptyListNotice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize view models
        investmentViewModel = new ViewModelProvider(requireActivity()).get(InvestmentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);
        Log.d(TAG, "Layout inflated successfully");

        recyclerView = view.findViewById(R.id.earningsRecyclerView);
        emptyListNotice = view.findViewById(R.id.emptyListNotice);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        earningRecyclerViewAdapter = new EarningRecyclerViewAdapter(requireActivity(), investments);
        recyclerView.setAdapter(earningRecyclerViewAdapter);

        displayNotice(false);
        return view;
    }

    private void displayNotice(boolean display) {
        if (display) emptyListNotice.setVisibility(View.VISIBLE);
        else emptyListNotice.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        earningRecyclerViewAdapter.setShowShimmer(true);

        investmentViewModel
                .getMaturedInvestments()
                .observe(getViewLifecycleOwner(), maturedInvestments -> {
                    if (maturedInvestments != null) {
                        if (!maturedInvestments.isEmpty()) {
                            displayNotice(false);
                            this.investments.clear();
                            for (Investment investment : maturedInvestments) {
                                this.investments.add(investment);
                                earningRecyclerViewAdapter.notifyItemInserted(investments.size() + 1);
                            }
                            earningRecyclerViewAdapter.setShowShimmer(false); // Hide shimmer once data is loaded
                        } else {
                            displayNotice(true);
                            earningRecyclerViewAdapter.setShowShimmer(false);
                        }
                    }
                })
        ;
    }
}
