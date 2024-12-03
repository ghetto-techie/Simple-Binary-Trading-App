package com.big.shamba.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.FarmCrestVentures;
import com.big.shamba.R;
import com.big.shamba.data.firebase.AuthService;
import com.big.shamba.data.firebase.FirestoreService;
import com.big.shamba.data.firebase.InvestmentService;
import com.big.shamba.data.firebase.InvestmentPackageService;
import com.big.shamba.data.firebase.ReferralService;
import com.big.shamba.data.firebase.UserService;
import com.big.shamba.data.firebase.WalletService;
import com.big.shamba.ui.adapter.views.MainBanner;
import com.big.shamba.ui.fragments.DashboardFragment;
import com.big.shamba.ui.fragments.HomeFragment;
import com.big.shamba.ui.fragments.MyInvestmentsFragment;
import com.big.shamba.ui.fragments.ReferralFragment;
import com.big.shamba.ui.fragments.SettingsFragment;
import com.big.shamba.ui.fragments.dialogs.InviteUserDialogFragment;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.InvestmentPackageViewModel;
import com.big.shamba.ui.viewmodels.InvestmentViewModel;
import com.big.shamba.ui.viewmodels.ReferralViewModel;
import com.big.shamba.ui.viewmodels.UserViewModel;
import com.big.shamba.ui.viewmodels.WalletViewModel;
import com.big.shamba.ui.viewmodels.factories.AuthViewModelFactory;
import com.big.shamba.ui.viewmodels.factories.InvestmentPackageViewModelFactory;
import com.big.shamba.ui.viewmodels.factories.InvestmentViewModelFactory;
import com.big.shamba.ui.viewmodels.factories.ReferralViewModelFactory;
import com.big.shamba.ui.viewmodels.factories.UserViewModelFactory;
import com.big.shamba.ui.viewmodels.factories.WalletViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String SELECTED_NAV_ITEM = "selected_nav_item";

    //    private MainBanner mainBanner;
    private HomeFragment homeFragment;
    private MyInvestmentsFragment myInvestmentsFragment;
    private DashboardFragment dashboardFragment;
    private SettingsFragment settingsFragment;
    private ReferralFragment referralFragment;

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    private WalletViewModel walletViewModel;
    private InvestmentViewModel investmentViewModel;
    private InvestmentPackageViewModel investmentPackageViewModel;
    private AuthViewModel authViewModel;
    private UserViewModel userViewModel;
    private ReferralViewModel referralViewModel;

    private FirebaseUser currentUser;
    private int selectedNavItemId = R.id.actionHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewModels();

        toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(null);

        bottomNavigationView = findViewById(R.id.bottomNav);
//        View banner = findViewById(R.id.banner);
//        mainBanner = new MainBanner(banner);

        // Initialize fragments
        homeFragment = new HomeFragment();
        myInvestmentsFragment = new MyInvestmentsFragment();
        dashboardFragment = new DashboardFragment();
        referralFragment = new ReferralFragment();
        settingsFragment = new SettingsFragment();

        // Restore selected navigation item state
        if (savedInstanceState != null) {
            selectedNavItemId = savedInstanceState.getInt(SELECTED_NAV_ITEM, R.id.actionHome);
        }

        // Set up BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            selectedNavItemId = item.getItemId(); // Save selected item

            if (selectedNavItemId == R.id.actionHome) {
                loadFragment(homeFragment);
                return true;
            } else if (selectedNavItemId == R.id.actionProfile) {
                loadFragment(dashboardFragment);
                return true;
            } else if (selectedNavItemId == R.id.actionMyInvestments) {
                loadFragment(myInvestmentsFragment);
                return true;
            } else if (selectedNavItemId == R.id.actionsSettings) {
                loadFragment(settingsFragment);
                return true;
            } else if (selectedNavItemId == R.id.actionReferrals) {
                loadFragment(referralFragment);
                return true;
            } else {
                return false;
            }
        });

        FarmCrestVentures app = (FarmCrestVentures) getApplicationContext();
        app.getNetworkLiveData().observe(this, isConnected -> {
            if (Boolean.FALSE.equals(isConnected)) {
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            } else {
                // Perform actions that require internet connectivity
            }
        });


        // Load the appropriate fragment
        if (savedInstanceState == null) {
            // First-time load
            loadFragment(homeFragment);
        } else {
            // Restore fragment after configuration change
            restoreFragment();
        }

        // Handle dynamic link
        handleDynamicLink();
    }

    private void handleDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String referralCode = deepLink.getQueryParameter("referralCode");
                            if (referralCode != null) {
                                Log.d(TAG, "Referral code from dynamic link: " + referralCode);
                                handleReferralLink(referralCode);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.w(TAG, "getDynamicLink:onFailure", e));
    }

    private void handleReferralLink(String referralCode) {
        if (authViewModel.getCurrentUser().getValue() != null) {
            // User is signed in, prompt to sign out
            promptSignOut(referralCode);
        } else {
            // User is signed out, launch CreateAccountActivity with referral code
            launchCreateAccountActivity(referralCode);
        }
    }

    private void promptSignOut(String referralCode) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Sign Out Required")
                .setMessage("You need to sign out to use the referral code and create a new account. Do you want to sign out?")
                .setPositiveButton("Sign Out", (dialog, which) -> {
                    authViewModel.signOut();
                    launchCreateAccountActivity(referralCode);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void launchCreateAccountActivity(String referralCode) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        intent.putExtra("REFERRAL_CODE", referralCode);
        // Clear the existing task and start a new one
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initViewModels() {
        FirestoreService firestoreService = new FirestoreService();
        WalletService walletService = new WalletService(firestoreService);
        InvestmentPackageService investmentPackageService = new InvestmentPackageService(firestoreService);
        InvestmentService investmentService = new InvestmentService(firestoreService, walletService, investmentPackageService);
        UserService userService = new UserService(firestoreService);
        ReferralService referralService = new ReferralService(firestoreService);
        AuthService authService = new AuthService();

        WalletViewModelFactory walletViewModelFactory = new WalletViewModelFactory(walletService);
        walletViewModel = new ViewModelProvider(this, walletViewModelFactory).get(WalletViewModel.class);
        InvestmentViewModelFactory investmentViewModelFactory = new InvestmentViewModelFactory(investmentService);
        investmentViewModel = new ViewModelProvider(this, investmentViewModelFactory).get(InvestmentViewModel.class);
        InvestmentPackageViewModelFactory investmentPackageViewModelFactory = new InvestmentPackageViewModelFactory(investmentPackageService);
        investmentPackageViewModel = new ViewModelProvider(this, investmentPackageViewModelFactory).get(InvestmentPackageViewModel.class);
        UserViewModelFactory userViewModelFactory = new UserViewModelFactory(userService);
        userViewModel = new ViewModelProvider(this, userViewModelFactory).get(UserViewModel.class);
        AuthViewModelFactory authViewModelFactory = new AuthViewModelFactory(authService);
        authViewModel = new ViewModelProvider(this, authViewModelFactory).get(AuthViewModel.class);
        ReferralViewModelFactory referralViewModelFactory = new ReferralViewModelFactory(referralService);
        referralViewModel = new ViewModelProvider(this, referralViewModelFactory).get(ReferralViewModel.class);

        authViewModel
                .getCurrentUser()
                .observe(this, firebaseUser -> {
                    if (firebaseUser != null) {
                        currentUser = firebaseUser;
                        setupViewModelObservers();
                    }
                });
    }

    private void setupViewModelObservers() {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            investmentPackageViewModel.fetchInvestmentPackages();
            investmentViewModel.fetchMaturedInvestmentsRealtime(userId);
            investmentViewModel.fetchAllUserInvestmentsRealtime(userId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authViewModel.getCurrentUser().getValue() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Please Sign In to continue!", Toast.LENGTH_SHORT).show();
            finishAffinity();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_NAV_ITEM, selectedNavItemId); // Save the selected item
    }

    private void restoreFragment() {
        Fragment fragment = null;

        if (selectedNavItemId == R.id.actionHome) {
            fragment = homeFragment;
        } else if (selectedNavItemId == R.id.actionProfile) {
            fragment = dashboardFragment;
        } else if (selectedNavItemId == R.id.actionMyInvestments) {
            fragment = myInvestmentsFragment;
        } else if (selectedNavItemId == R.id.actionsSettings) {
            fragment = settingsFragment;
        } else if (selectedNavItemId == R.id.actionReferrals) {
            fragment = referralFragment;
        }

        // Check if the fragment is already in FragmentManager
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);
        if (currentFragment != fragment) {
            loadFragment(fragment);
        }

        // Restore selected item in BottomNavigationView
        bottomNavigationView.setSelectedItemId(selectedNavItemId);
    }

    public void loadFragment(Fragment fragment) {
        if (fragment == null) return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionInvite) {
            inviteNewMember();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void inviteNewMember() {
        InviteUserDialogFragment inviteUserDialogFragment = new InviteUserDialogFragment();
        inviteUserDialogFragment.show(getSupportFragmentManager(), "Invite");
    }
}
