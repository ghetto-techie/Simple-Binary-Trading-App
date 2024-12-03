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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.Referral;
import com.big.shamba.ui.adapter.recyclerview.ReferralsRecyclerViewAdapter;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.ReferralViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReferralFragment extends Fragment {
    private static final String TAG = "ReferralFragment";
    private AuthViewModel authViewModel;
    private ReferralViewModel referralViewModel;

    private RecyclerView recyclerView;
    private View emptyListNotice;
    private ReferralsRecyclerViewAdapter referralsRecyclerViewAdapter;
    private List<Referral> members;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        members = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_referrals, container, false);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        referralViewModel = new ViewModelProvider(requireActivity()).get(ReferralViewModel.class);

        recyclerView = view.findViewById(R.id.teamL1RecyclerView);
        emptyListNotice = view.findViewById(R.id.emptyListNotice);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));

        referralsRecyclerViewAdapter = new ReferralsRecyclerViewAdapter(requireContext(), members);
        recyclerView.setAdapter(referralsRecyclerViewAdapter);

        authViewModel
                .getCurrentUser()
                .observe(getViewLifecycleOwner(), currentUser -> {
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        referralViewModel.fetchTotalReferrals(userId);
                    }
                })
        ;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        referralsRecyclerViewAdapter.setShowShimmer(true);
        displayNotice(false);
        referralViewModel
                .getReferralsLiveData()
                .observe(getViewLifecycleOwner(), referrals -> {
                    referralsRecyclerViewAdapter.setShowShimmer(false);
                    if (referrals != null && !referrals.isEmpty()) {
                        Log.d(TAG, "onChanged: referrals > " + referrals);
                        displayNotice(false);
                        members.clear();
                        for (Referral referral : referrals) {
                            members.add(referral);
                            referralsRecyclerViewAdapter.notifyItemInserted(members.size() + 1);
                            Log.d(TAG, "onChanged: Referral > " + referral);
                        }
                    } else {
                        Log.d(TAG, "onViewCreated: 0 referrals");
                        displayNotice(true);
                    }
                })
        ;
    }

    private void displayNotice(boolean display) {
        if (display) {
            emptyListNotice.setVisibility(View.VISIBLE);
            Log.d(TAG, "displayNotice: Notice Displayed");
        } else {
            emptyListNotice.setVisibility(View.GONE);
            Log.d(TAG, "displayNotice: Notice Hidden");
        }
    }
}
