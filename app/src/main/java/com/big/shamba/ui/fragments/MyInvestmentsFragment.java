package com.big.shamba.ui.fragments;

import android.os.Bundle;
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
import com.big.shamba.ui.adapter.recyclerview.MyInvestmentsRecyclerViewAdapter;
import com.big.shamba.ui.viewmodels.InvestmentViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MyInvestmentsFragment extends Fragment {
    private static final String TAG = "MyInvestmentsFragment";
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private MyInvestmentsRecyclerViewAdapter myInvestmentsRecyclerViewAdapter;

    private InvestmentViewModel investmentViewModel;

    private List<Investment> userInvestments = new ArrayList<>();
    private List<Investment> allInvestments = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myinvestment, container, false);

        tabLayout = view.findViewById(R.id.myInvestmentsTabLayout);
        recyclerView = view.findViewById(R.id.myInvestmentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // All
                        filterInvestments("all");
                        break;
                    case 1: // Pending
                        filterInvestments("pending");
                        break;
                    case 2: // Matured
                        filterInvestments("matured");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize view models
        investmentViewModel = new ViewModelProvider(requireActivity()).get(InvestmentViewModel.class);

        myInvestmentsRecyclerViewAdapter = new MyInvestmentsRecyclerViewAdapter(requireActivity(), userInvestments);
        recyclerView.setAdapter(myInvestmentsRecyclerViewAdapter);

        investmentViewModel
                .getAllUserInvestments()
                .observe(getViewLifecycleOwner(), allInvestments -> {
                    if (allInvestments != null) {
                        this.allInvestments.clear();
                        this.allInvestments.addAll(allInvestments);
                        filterInvestments("all");
                    }
                });
    }

    private void filterInvestments(String filter) {
        List<Investment> filteredList = new ArrayList<>();
        switch (filter) {
            case "all":
                filteredList = new ArrayList<>(allInvestments);
                break;
            case "pending":
                for (Investment investment : allInvestments) {
                    if (!investment.isMatured()) {
                        filteredList.add(investment);
                    }
                }
                break;
            case "matured":
                for (Investment investment : allInvestments) {
                    if (investment.isMatured()) {
                        filteredList.add(investment);
                    }
                }
                break;
        }
        userInvestments.clear();
        userInvestments.addAll(filteredList);
        myInvestmentsRecyclerViewAdapter.notifyDataSetChanged();
    }
}
