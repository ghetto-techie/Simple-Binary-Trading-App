package com.big.shamba.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.ui.adapter.recyclerview.UserInvestmentsRecyclerViewAdapter;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.InvestmentPackageViewModel;
import com.big.shamba.ui.viewmodels.InvestmentViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class UserInvestmentsFragment extends Fragment {
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private UserInvestmentsRecyclerViewAdapter adapter;

    private AuthViewModel authViewModel;
    private InvestmentViewModel investmentViewModel;
    private InvestmentPackageViewModel packageViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_investments, container, false);

        // Initialize RecyclerView and Adapter
        tabLayout = view.findViewById(R.id.userInvestmentsTabLayout);
        recyclerView = view.findViewById(R.id.userInvestmentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserInvestmentsRecyclerViewAdapter(requireActivity(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        investmentViewModel = new ViewModelProvider(requireActivity()).get(InvestmentViewModel.class);
        packageViewModel = new ViewModelProvider(requireActivity()).get(InvestmentPackageViewModel.class);

        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                String userId = firebaseUser.getUid();// Load initial investments
                investmentViewModel.loadInvestments(userId); // Replace with actual user ID
            }
        });

        // Observe filtered investments and update adapter
        investmentViewModel.getFilteredInvestments().observe(getViewLifecycleOwner(), investments -> {
            adapter.updateInvestments(investments);
            packageViewModel.fetchPackagesForInvestments(investments); // Fetch associated package details
        });

        // Observe package map and update adapter
        packageViewModel.getPackageMapLiveData().observe(getViewLifecycleOwner(), packageMap -> {
            adapter.updatePackages(packageMap); // Updates cached packages in adapter
        });

        // Tab selection listener for filtering
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        investmentViewModel.filterInvestments("all");
                        break;
                    case 1:
                        investmentViewModel.filterInvestments("pending");
                        break;
                    case 2:
                        investmentViewModel.filterInvestments("matured");
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
    }
}
