package com.big.shamba.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.models.dto.Category;
import com.big.shamba.ui.adapter.recyclerview.HomeVerticalRecyclerViewAdapter;
import com.big.shamba.ui.viewmodels.InvestmentPackageViewModel;
import com.big.shamba.utility.FirestoreCollections;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private RecyclerView verticalRecyclerView;
    private List<Category> categoryList;
    private HomeVerticalRecyclerViewAdapter verticalRecyclerViewAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;

    private InvestmentPackageViewModel investmentPackageViewModel;

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        investmentPackageViewModel = new ViewModelProvider(requireActivity()).get(InvestmentPackageViewModel.class);
        verticalRecyclerView = view.findViewById(R.id.verticalRecyclerView);
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);

        verticalRecyclerViewAdapter = new HomeVerticalRecyclerViewAdapter(requireActivity(), categoryList);
        verticalRecyclerView.setHasFixedSize(true);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        verticalRecyclerView.setAdapter(verticalRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Start the shimmer effect
        shimmerFrameLayout.startShimmer();

        // Fetch investment packages
        fetchInvestmentPackages();
    }

    //TODO: Add Investment package View Model to handle fetchInvestmentPackages
    private void fetchInvestmentPackages() {
        Log.d(TAG, "fetchInvestmentPackages: ");

        investmentPackageViewModel
                .getInvestmentPackages()
                .observe(getViewLifecycleOwner(), investmentPackages -> {
                    if (investmentPackages != null) {
                        Log.d(TAG, "onChanged: Investment Packages > " + investmentPackages);
                        Map<String, List<InvestmentPackage>> categorizedPackages = new HashMap<>();
                        for (InvestmentPackage investmentPackage : investmentPackages) {
                            if (!categorizedPackages.containsKey(investmentPackage.getType())) {
                                categorizedPackages.put(investmentPackage.getType(), new ArrayList<>());
                            }
                            categorizedPackages.get(investmentPackage.getType()).add(investmentPackage);
                        }

                        categoryList.clear();
                        for (Map.Entry<String, List<InvestmentPackage>> entry : categorizedPackages.entrySet()) {
                            Category category = new Category(entry.getKey(), entry.getValue());
                            categoryList.add(category);
                            Log.d(TAG, "onSuccess: Category List Item added -> " + category.toString());
                            verticalRecyclerViewAdapter.updateCategories(categoryList);
                        }
                        // Stop the shimmer effect and show the recycler view
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        verticalRecyclerView.setVisibility(View.VISIBLE);
                    }
                })
        ;
    }

}