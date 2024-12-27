package com.big.shamba.ui.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.badge.BadgeDrawable;

import java.util.ArrayList;

public class UserInvestmentsFragment extends Fragment {
    private static final String TAG = "UserInvestmentsFragment";
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private View emptyListNotice;
    private UserInvestmentsRecyclerViewAdapter adapter;

    private AuthViewModel authViewModel;
    private InvestmentViewModel investmentViewModel;
    private InvestmentPackageViewModel packageViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_investments, container, false);

        // Initialize views
        tabLayout = view.findViewById(R.id.userInvestmentsTabLayout);
        recyclerView = view.findViewById(R.id.userInvestmentsRecyclerView);
        emptyListNotice = view.findViewById(R.id.emptyListNotice); // Empty list view
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize adapter
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

        // Observe current user
        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                String userId = firebaseUser.getUid();
                adapter.setShowShimmer(true);

                // Start listening for real-time updates
                investmentViewModel.startListeningForInvestments(userId);
            } else {
                investmentViewModel.stopListeningForInvestments();
                adapter.setShowShimmer(false);
                displayNotice(true);
            }
        });

        // Observe filtered investments and update adapter
        investmentViewModel.getFilteredInvestments().observe(getViewLifecycleOwner(), investments -> {
            adapter.setShowShimmer(false);
            if (investments != null) {
                adapter.updateInvestments(investments, investmentViewModel.getNewlyPendingIds(), investmentViewModel.getNewlyMaturedIds());
                displayNotice(investments.isEmpty());
                packageViewModel.fetchPackagesForInvestments(investments);
            } else {
                displayNotice(true);
            }
        });

        // Observe new pending investments and update badge
        investmentViewModel.getNewPendingInvestmentCount().observe(getViewLifecycleOwner(), pendingCount -> {
            TabLayout.Tab pendingTab = tabLayout.getTabAt(1); // Assuming the Pending tab is at index 1
            if (pendingTab != null) {
                if (pendingCount > 0) {
                    BadgeDrawable badge = pendingTab.getOrCreateBadge();
                    badge.setNumber(pendingCount);
                    badge.setVisible(true);
                } else {
                    pendingTab.removeBadge();
                }
            }
        });

        // Observe new matured investments and update badge
        investmentViewModel.getNewMaturedInvestmentCount().observe(getViewLifecycleOwner(), maturedCount -> {
            TabLayout.Tab maturedTab = tabLayout.getTabAt(2); // Assuming the Matured tab is at index 2
            if (maturedTab != null) {
                if (maturedCount > 0) {
                    BadgeDrawable badge = maturedTab.getOrCreateBadge();
                    badge.setNumber(maturedCount);
                    badge.setVisible(true);
                } else {
                    maturedTab.removeBadge();
                }
            }
        });

        // Observe package map and update adapter
        packageViewModel.getPackageMapLiveData().observe(getViewLifecycleOwner(), packageMap -> {
            adapter.updatePackages(packageMap);
        });

        // Tab selection listener for filtering and badge clearing
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        investmentViewModel.filterInvestments("all");
                        break;
                    case 1:
                        investmentViewModel.filterInvestments("pending");
                        investmentViewModel.markPendingInvestmentsAsViewed(); // Clear pending badge
                        break;
                    case 2:
                        investmentViewModel.filterInvestments("matured");
                        investmentViewModel.markMaturedInvestmentsAsViewed(); // Clear matured badge
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop listening for updates when the fragment is destroyed
        investmentViewModel.stopListeningForInvestments();
    }

    private void displayNotice(boolean display) {
        if (display) {
            emptyListNotice.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            Log.d(TAG, "displayNotice: Notice Displayed");
        } else {
            emptyListNotice.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "displayNotice: Notice Hidden");
        }
    }
}
