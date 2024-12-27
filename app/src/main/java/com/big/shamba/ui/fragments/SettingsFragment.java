package com.big.shamba.ui.fragments;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.big.shamba.R;
import com.big.shamba.ui.LoginActivity;
import com.big.shamba.ui.fragments.settings.FAQFragment;
import com.big.shamba.ui.fragments.settings.PersonalInformationFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "SettingsFragment";
    public static final String PREF_NOTIFICATION = "notification_preference";
    public static final String PREF_KEY_LOGOUT = "pref_key_logout";
    public static final String PREF_KEY_APP_VERSION = "pref_key_version";
    public static final String PREF_KEY_PERSONAL_INFORMATION = "pref_key_personal_information";
    public static final String PREF_KEY_CONTACT = "pref_key_contact";
    public static final String PREF_KEY_DISPLAY_MODE = "pref_key_display_mode";
    public static final String PREF_KEY_FAQ = "pref_key_faq";
    public static final String PREF_KEY_HIDE_BALANCES = "pref_key_hide_balances";
    public static final String PREF_KEY_ABOUT = "pref_key_about";
    private Preference logoutPreference;
    private SwitchPreferenceCompat notificationPreference;
    private Preference versionPreference;
    private Preference personalInformationPreference;
    private Preference contactUsPreference;
    private Preference aboutPreference;
    private ListPreference displayModePreference;
    private SwitchPreferenceCompat hideBalancesPreference;
    private SharedPreferences sharedPreferences;
    private Preference faqPreference;
    private DatabaseReference aboutDatabaseReference;
    private DatabaseReference contactDatabaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        firebaseDatabase = FirebaseDatabase.getInstance();
        aboutDatabaseReference = firebaseDatabase.getReference("about").child("text");
        contactDatabaseReference = firebaseDatabase.getReference("contact");
        // Initialize preferences
        notificationPreference = findPreference(PREF_NOTIFICATION);
        logoutPreference = findPreference(PREF_KEY_LOGOUT);
        versionPreference = findPreference(PREF_KEY_APP_VERSION);
        personalInformationPreference = findPreference(PREF_KEY_PERSONAL_INFORMATION);
        contactUsPreference = findPreference(PREF_KEY_CONTACT);
        displayModePreference = findPreference(PREF_KEY_DISPLAY_MODE);
        aboutPreference = findPreference(PREF_KEY_ABOUT);
        faqPreference = findPreference(PREF_KEY_FAQ);
        hideBalancesPreference = findPreference(PREF_KEY_HIDE_BALANCES);

        if (personalInformationPreference != null) {
            personalInformationPreference
                    .setOnPreferenceClickListener(preference -> {
                        loadFragment(new PersonalInformationFragment());
                        return true;
                    })
            ;
        }

        if (faqPreference != null) {
            faqPreference.setOnPreferenceClickListener(preference -> {
                loadFragment(new FAQFragment());
                return true;
            });
        }

        // Register preference change listener
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Set up notification preference change listener
        if (notificationPreference != null) {
            notificationPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean enabled = (boolean) newValue;
                if (enabled) {
                    enableNotifications();
                } else {
                    disableNotifications();
                }
                return true;
            });
        }

        // Set up contact us preference click listener
        if (contactUsPreference != null) {
            contactUsPreference.setOnPreferenceClickListener(preference -> {
//                FarmCrestVentures app = (FarmCrestVentures) requireContext().getApplicationContext();
//                app.getNetworkLiveData().observe(getViewLifecycleOwner(), isConnected -> {
//                    if (Boolean.TRUE.equals(isConnected)) {
//
//                    }
//                });
                ProgressDialog progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                contactDatabaseReference
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                progressDialog.dismiss();
                                if (snapshot.exists()) {
                                    String phone = snapshot.child("call").getValue(String.class);
                                    String facebook = snapshot.child("facebook").getValue(String.class);
                                    String instagram = snapshot.child("instagram").getValue(String.class);
                                    String telegram = snapshot.child("telegram").getValue(String.class);
                                    String[] contacts = {"Call", "Telegram", "Facebook", "Instagram"};

                                    new MaterialAlertDialogBuilder(requireContext())
                                            .setTitle("Contact Us")
                                            .setItems(contacts, (dialogInterface, i) -> {
                                                String selected = contacts[i];
                                                switch (selected) {
                                                    case "Telegram":
                                                        openLink(telegram, "Visit our Telegram Channel");
                                                        break;
                                                    case "Facebook":
                                                        openLink(facebook, "Visit our Facebook page");
                                                        break;
                                                    case "Instagram":
                                                        openLink(instagram, "Visit our Instagram page");
                                                        break;
                                                    case "Call":
                                                        // Create an intent to dial the phone number
                                                        if (phone != null) {
                                                            copyToClipboard("Phone number", phone);
                                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                                            intent.setData(Uri.parse("tel:" + phone));
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show();
                                                        }

                                                        break;
                                                }
                                            })
                                            .create()
                                            .show();
                                } else
                                    Toast.makeText(requireContext(), "Error! Please try again later", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Log.d(TAG, "onCancelled: Error fetching contact info > " + error);
                                Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show();
                            }
                        })
                ;
                return true;
            });
        }

        if (aboutPreference != null) {
            aboutPreference.setOnPreferenceClickListener(preference -> {
                aboutDatabaseReference
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String about = snapshot.getValue(String.class);
                                    if (about != null) {
                                        new MaterialAlertDialogBuilder(requireContext())
                                                .setTitle("ABOUT US")
                                                .setMessage(about)
                                                .create()
                                                .show()
                                        ;
                                    }
                                    Log.d(TAG, "onDataChange: " + about);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(TAG, "onCancelled: ");
                                Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show();
                            }
                        })
                ;
                return false;
            });
        }

        // Set up logout preference click listener
        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(preference -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Sign Out")
                        .setIcon(R.drawable.ic_logout)
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Sign Out", (dialog, which) -> logoutUser())
                        .setNegativeButton("Cancel", null)
                        .show()
                ;
                return true;
            });
        }

        // Set up version preference summary with app version
        if (versionPreference != null) {
            String version = getAppVersion();
            versionPreference.setSummary("Version " + version);
        }

        // Initialize display mode summary
        if (displayModePreference != null) {
            displayModePreference.setSummary(displayModePreference.getEntry());
        }

        // Apply saved display mode on startup
        String savedDisplayMode = sharedPreferences.getString(PREF_KEY_DISPLAY_MODE, "system");
        updateDisplayMode(savedDisplayMode);
    }

    private void openLink(String link, String message) {
        if (link != null && URLUtil.isValidUrl(link)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), label + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public void loadFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrame, fragment)
                .addToBackStack(null)
                .commit()
        ;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_KEY_DISPLAY_MODE)) {
            if (displayModePreference != null) {
                displayModePreference.setSummary(displayModePreference.getEntry());
                updateDisplayMode(displayModePreference.getValue());
            }
        } else if (key.equals(PREF_KEY_HIDE_BALANCES)) {
            boolean hideBalances = sharedPreferences.getBoolean(key, false);
            handleHideBalancesChange(hideBalances);
        }
    }

    private void updateDisplayMode(String mode) {
        switch (mode) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

        // Save the display mode preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_DISPLAY_MODE, mode);
        editor.apply();
    }

    private void disableNotifications() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(requireActivity());
        notificationManagerCompat.cancelAll();
    }

    private void enableNotifications() {
        // Implement logic to enable notifications if needed
    }

    private void logoutUser() {
        // Clear user session or perform any necessary logout actions
        FirebaseAuth.getInstance().signOut();
        // Create an Intent to start the LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);

        // Set flags to clear the activity stack and start a new task
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Start the LoginActivity
        startActivity(intent);

        // Finish the current activity (SettingsActivity or any other)
        getActivity().finish();
    }

    private String getAppVersion() {
        try {
            PackageManager pm = requireActivity().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(requireActivity().getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "---";
        }
    }

    private void handleHideBalancesChange(boolean hideBalances) {
        // TODO: Implement your logic to hide or show balances
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister preference change listener
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
