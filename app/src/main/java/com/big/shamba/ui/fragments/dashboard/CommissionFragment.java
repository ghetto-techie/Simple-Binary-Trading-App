package com.big.shamba.ui.fragments.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.Referral;
import com.big.shamba.models.ReferralCommissionWalletTransaction;
import com.big.shamba.ui.adapter.recyclerview.ReferralCommissionRecyclerViewAdapter;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.ReferralViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class CommissionFragment extends Fragment {
    private static final String TAG = "CommissionFragment";
    private View emptyListNotice;
    private RecyclerView recyclerView;
    private ReferralCommissionRecyclerViewAdapter referralCommissionRecyclerViewAdapter;
    //    private ShimmerFrameLayout shimmerFrameLayout; // Shimmer layout
    private ReferralViewModel referralViewModel;
    private AuthViewModel authViewModel;
    private List<Pair<ReferralCommissionWalletTransaction, Referral>> commissions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        referralViewModel = new ViewModelProvider(requireActivity()).get(ReferralViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commission, container, false);
        commissions = new ArrayList<>();

        // Initialize Shimmer Layout
//        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.commissionRecyclerView);
        emptyListNotice = view.findViewById(R.id.emptyListNotice);
        referralCommissionRecyclerViewAdapter = new ReferralCommissionRecyclerViewAdapter(commissions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(referralCommissionRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                String userId = firebaseUser.getUid();

                // Start showing shimmer
                referralCommissionRecyclerViewAdapter.setShowShimmer(true);
                displayNotice(false);
                referralViewModel.fetchReferralCommissions(userId);

                referralViewModel
                        .getReferralCommissionsLiveData()
                        .observe(getViewLifecycleOwner(), referralCommissions -> {
                            referralCommissionRecyclerViewAdapter.setShowShimmer(false);
                            if (!referralCommissions.isEmpty()) {
                                displayNotice(false);
                                commissions.clear();
                                for (Pair<ReferralCommissionWalletTransaction, Referral> referralCommission : referralCommissions) {
                                    commissions.add(referralCommission);
                                    referralCommissionRecyclerViewAdapter.notifyItemInserted(commissions.size() + 1);
                                }
                            } else {
                                displayNotice(true);
                            }
                        })
                ;
            } else {
                Log.d(TAG, "onViewCreated: User NOT logged in!");
            }
        });
    }

    private void displayNotice(boolean display) {
        if (display) emptyListNotice.setVisibility(View.VISIBLE);
        else emptyListNotice.setVisibility(View.GONE);
    }
}
