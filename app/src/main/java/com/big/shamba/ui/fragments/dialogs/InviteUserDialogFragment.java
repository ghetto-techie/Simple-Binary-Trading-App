package com.big.shamba.ui.fragments.dialogs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.big.shamba.R;
import com.big.shamba.models.User;
import com.big.shamba.ui.viewmodels.AuthViewModel;
import com.big.shamba.ui.viewmodels.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class InviteUserDialogFragment extends DialogFragment {
    private static final String TAG = "InviteUserDialogFragmen";
    private TextView referralCodeTextView;
    private ImageButton copyCodeButton;
    private MaterialButton copyLinkButton;
    private MaterialButton shareLinkButton;

    private AuthViewModel authViewModel;
    private UserViewModel userViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_invite_new_user, null);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        referralCodeTextView = view.findViewById(R.id.referralCodeTV);
        copyCodeButton = view.findViewById(R.id.copyReferralCodeBtn);
        copyLinkButton = view.findViewById(R.id.copyReferralLinkBtn);
        shareLinkButton = view.findViewById(R.id.shareReferralLinkBtn);

        authViewModel.getCurrentUser().observe(this, currentUser -> {
            if (currentUser != null) {
                userViewModel.getUserDetails(currentUser.getUid()).observe(this, userFirestoreDoc -> {
                    if (userFirestoreDoc != null) {
                        loadUserData(userFirestoreDoc);
                    } else {
                        Log.d(TAG, "onChanged: User NOT found in firestore");
                        Toast.makeText(requireActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.d(TAG, "onChanged: User is not logged in");
                Toast.makeText(requireActivity(), "Please log in", Toast.LENGTH_SHORT).show();
            }
        });

        copyLinkButton.setOnClickListener(v -> {
            authViewModel.getCurrentUser().observe(this, currentUser -> {
                if (currentUser != null) {
                    userViewModel.getUserDetails(currentUser.getUid()).observe(this, user -> {
                        if (user != null) {
                            String dynamicLink = createDynamicLink(user.getCode());
                            copyToClipboard("Referral Link", dynamicLink);
                        }
                    });
                }
            });
        });

        shareLinkButton.setOnClickListener(v -> {
            FirebaseUser firebaseUser = authViewModel.getCurrentUser().getValue();
            if (firebaseUser != null) {
                User user = userViewModel.getUserDetails(firebaseUser.getUid())
                        .getValue();
                if (user != null) {
                    String dynamicLink = createDynamicLink(user.getCode());
                    String appName = getResources().getString(R.string.app_name);
                    String inviteMessage = "🌿 **Join " + getString(R.string.app_name) + "! 🌿**\n\n"
                            + dynamicLink + "\n\n"
                            + "Are you looking to invest in a thriving agricultural business? Look no further!\n\n"
                            + "At " + appName + ", we're revolutionizing farming and offering you the chance to grow with us. "
                            + "Whether it’s poultry, cattle, or swine, your investment helps us expand and in return, you earn high returns in a short period!\n\n"
                            + "🔹 **Why Join Us?**\n"
                            + "- **High Returns:** Enjoy attractive interest rates on your investments.\n"
                            + "- **Flexible Packages:** Choose from various investment options tailored to your needs.\n"
                            + "- **Transparency:** Regular updates and full visibility into your investments.\n"
                            + "- **Referral Program:** Earn rewards by inviting your friends to join.\n\n"
                            + "🔹 **Get Started Today!**\n"
                            + "Invest as little as Ksh. 500 and see your money grow. Click the link below to join our Telegram channel and start your journey with " + appName + ":" +
                            "\n\n "
                            + "Together, let's cultivate success!";
                    shareText("Share Referral Link", inviteMessage);
                } else {
                    Log.d(TAG, "onCreateDialog: User is NULL");
                }
            }
        });

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();
    }

    private void loadUserData(User user) {
        Log.d(TAG, "loadUserData: ");
        String referralCode = user.getCode();
        Log.d(TAG, "loadUserData: Referral Code > " + referralCode);
        if (referralCode != null) {
            copyCodeButton.setOnClickListener(v -> copyToClipboard("Referral Code", referralCode));
            referralCodeTextView.setText(referralCode);
        }
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), label + " copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void shareText(String title, String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, title));
    }

    private String createDynamicLink(String referralCode) {
        Uri dynamicLinkUri = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://heavenzgate.page.link/referrals?referralCode=" + referralCode))
                .setDomainUriPrefix("https://heavenzgate.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink()
                .getUri();

        Log.d(TAG, "createDynamicLink: Generated Dynamic Link: " + dynamicLinkUri.toString());
        return dynamicLinkUri.toString();
    }
}
