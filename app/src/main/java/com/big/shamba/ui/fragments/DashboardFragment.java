package com.big.shamba.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.R;
import com.big.shamba.models.Investment;
import com.big.shamba.ui.fragments.dashboard.CommissionFragment;
import com.big.shamba.ui.fragments.dashboard.DepositsFragment;
import com.big.shamba.ui.fragments.dashboard.EarningsFragment;
import com.big.shamba.ui.fragments.dashboard.WithdrawalFragment;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.InvestmentViewModel;
import com.big.shamba.ui.viewmodels.ReferralViewModel;
import com.big.shamba.ui.viewmodels.UserViewModel;
import com.big.shamba.ui.viewmodels.WalletViewModel;
import com.big.shamba.ui.viewmodels.PesapalViewModel;
import com.big.shamba.utility.TokenStorage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private WalletViewModel walletViewModel;
    private AuthViewModel authViewModel;
    private UserViewModel userViewModel;
    private PesapalViewModel pesapalViewModel;
    private InvestmentViewModel investmentViewModel;
    private ReferralViewModel referralViewModel;

    private MaterialButton depositButton;
    private MaterialButton withdrawalButton;

    private TextView totalAccountBalanceTV;
    private TextView userNameTV;

    private TextView totalEarningsTV;
    private TextView totalReferralsTV;
    private TextView totalCommissionsTV;

    private List<Investment> maturedInvestments = new ArrayList<>();
    private EarningsFragment earningsFragment;
    private CommissionFragment commissionFragment;
    private ProgressDialog progressDialog;

    public DashboardFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        pesapalViewModel = new ViewModelProvider(requireActivity()).get(PesapalViewModel.class);
        walletViewModel = new ViewModelProvider(requireActivity()).get(WalletViewModel.class);
        investmentViewModel = new ViewModelProvider(requireActivity()).get(InvestmentViewModel.class);
        referralViewModel = new ViewModelProvider(requireActivity()).get(ReferralViewModel.class);

        Log.d(TAG, "onCreate:");
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        progressDialog = new ProgressDialog(requireContext());

        progressBar = view.findViewById(R.id.progressBar);
        tabLayout = view.findViewById(R.id.tabLayout);
        totalAccountBalanceTV = view.findViewById(R.id.totalAccountBalanceTV);
        userNameTV = view.findViewById(R.id.userNameNav);
        depositButton = view.findViewById(R.id.depositBtn);
        withdrawalButton = view.findViewById(R.id.withdrawBtn);
        totalEarningsTV = view.findViewById(R.id.totalEarningsTV);
        totalReferralsTV = view.findViewById(R.id.referralsTV);
        totalCommissionsTV = view.findViewById(R.id.totalCommissionsTV);

        depositButton.setOnClickListener(v -> {
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.create();
            progressDialog.show();

            //TODO: Store consumer key and secret on external database
            pesapalViewModel
                    .authenticate(
                            "qkio1BGGYAXTu2JOfm7XSXNruoZsrqEW",
                            "osGQ364R49cXKeOYSpaOnT++rHs="
                    )
                    .observe(getViewLifecycleOwner(), authResponsePesapal -> {
                        progressDialog.dismiss();
                        if (authResponsePesapal != null && authResponsePesapal.getStatus().equals("200")) {
                            String token = authResponsePesapal.getToken();
                            try {
                                TokenStorage.saveToken(requireContext(), token);
                                //Proceed to register IPN number and trigger pesapal payment
                                registerIPN();
                            } catch (Exception e) {
                                Log.d(TAG, "onCreateView: Error calling API -> " + e.getMessage());
                                throw new RuntimeException(e);
                            }
                            Log.d(TAG, "onCreateView: Token fetched successfully");
                        } else {
                            Toast.makeText(requireContext(), "Error: " + authResponsePesapal.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onCreateView: Failed to fetch token -> " + authResponsePesapal.getMessage());
                        }
                    })
            ;
        });

        authViewModel
                .getCurrentUser()
                .observe(getViewLifecycleOwner(), currentUser -> {
                    if (currentUser != null) {
                        String userId = currentUser.getUid();

                        // Show progress bar while fetching data
                        progressBar.setVisibility(View.VISIBLE);

                        userViewModel
                                .getUserDetails(userId)
                                .observe(getViewLifecycleOwner(), user -> {
                                    if (user != null) {
                                        userNameTV.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
                                        String imageUrl = user.getImageUrl();
                                        if (imageUrl != null && !imageUrl.isEmpty()) {
//                                            Glide
//                                                    .with(this)
//                                                    .load(imageUrl)
//                                                    .centerCrop()
//                                                    .placeholder(R.drawable.icon_light)
//                                                    .into(userDisplayImage)
//                                            ;
                                        }
                                    }
                                })
                        ;

                        walletViewModel.fetchWalletBalance(userId);
                        walletViewModel
                                .getWalletBalance()
                                .observe(getViewLifecycleOwner(), balance -> {
                                    // Update UI with wallet balance
                                    totalAccountBalanceTV.setText("Ksh. " + balance);
                                })
                        ;
                        // Start listening for balance changes
                        walletViewModel.startListeningForWalletBalanceChanges(userId);
                        //Fetch total earnings
                        investmentViewModel.fetchTotalEarningsCountRealTime(userId);
                        //Update Total Earnings
                        investmentViewModel
                                .getTotalEarningsCount()
                                .observe(getViewLifecycleOwner(), totalEarnings -> {
                                    if (totalEarnings != null) {
                                        totalEarningsTV.setText("" + totalEarnings);
                                    } else {
                                        totalEarningsTV.setText("0");
                                    }
                                    progressBar.setVisibility(View.GONE);  // Hide progress bar when data is updated
                                })
                        ;
                        // Fetch total referral count
                        referralViewModel.fetchTotalReferralCount(userId);
                        //Update Referral count views
                        referralViewModel
                                .getTotalReferralCount()
                                .observe(getViewLifecycleOwner(), totalReferralCount -> {
                                    if (totalReferralCount != null) {
                                        totalReferralsTV.setText(String.valueOf(totalReferralCount));
                                    }
                                })
                        ;
                        referralViewModel.fetchTotalSumOfReferralCommissions(userId);
                        referralViewModel
                                .getTotalSumOfReferralCommissions()
                                .observe(getViewLifecycleOwner(), totalSumOfReferralCommissions -> {
                                    if (totalSumOfReferralCommissions != null) {
                                        totalCommissionsTV.setText(String.valueOf(totalSumOfReferralCommissions));
                                    } else {
                                        Log.d(TAG, "onCreateView: totalSumOfReferralCommissions is NULL");
                                    }
                                })
                        ;
                    } else {
                        Log.d(TAG, "onCreateView: User not logged in");
                    }
                })
        ;

        earningsFragment = new EarningsFragment();
        commissionFragment = new CommissionFragment();
        return view;
    }

    private void registerIPN() {
        try {
            String token = TokenStorage.getToken(requireContext());
            pesapalViewModel
                    .registerIPN(token, "https://www.myapplication.com/ipn", "GET")
                    .observe(getViewLifecycleOwner(), response -> {
                        progressDialog.dismiss();
                        if (response != null && "200".equals(response.getStatus())) {
                            Toast.makeText(requireContext(), "IPN Registered Successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "IPN Registered: " + response.getIpnId());
                        } else {
                            Toast.makeText(requireContext(), "Error: " + response.getError(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "IPN Registration Error: " + response.getError());
                        }
                    });
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(requireContext(), "Error retrieving token", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error retrieving token", e);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        // Setup Tab Layout
        tabLayout.addTab(tabLayout.newTab().setText("Earnings"));
        tabLayout.addTab(tabLayout.newTab().setText("Deposits"));
        tabLayout.addTab(tabLayout.newTab().setText("Withdrawals"));
        tabLayout.addTab(tabLayout.newTab().setText("Commission"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        loadFragment(earningsFragment);
                        break;
                    case 1:
                        loadFragment(new DepositsFragment());
                        break;
                    case 2:
                        loadFragment(new WithdrawalFragment());
                        break;
                    case 3:
                        loadFragment(commissionFragment);
                        break;
                    default:
                        loadFragment(earningsFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Load the first tab initially
        if (tabLayout.getSelectedTabPosition() == 0) {
            loadFragment(earningsFragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        Log.d(TAG, "loadFragment: ");
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.transactionHistoryFrameLayout, fragment);
        fragmentTransaction.commit();
    }
}
