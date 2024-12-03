package com.big.shamba.ui.adapter.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.big.shamba.R;
import com.google.android.material.button.MaterialButton;

public class MainBanner {
    private final View banner;
    private final ImageView bannerIcon;
    private final TextView bannerMessage;
    private final MaterialButton bannerAction;

    public MainBanner(View banner) {
        this.banner = banner;
        this.bannerIcon = banner.findViewById(R.id.bannerIcon);
        this.bannerMessage = banner.findViewById(R.id.bannerMessage);
        this.bannerAction = banner.findViewById(R.id.bannerAction);
    }

    public void showBanner(
            int iconResId,
            String message,
            int backgroundColor,
            String buttonText,
            View.OnClickListener buttonListener
    ) {
        // Set the icon
        if (iconResId != 0) {
            bannerIcon.setVisibility(View.VISIBLE);
            bannerIcon.setImageResource(iconResId);
        } else {
            bannerIcon.setVisibility(View.GONE);
        }

        // Set the message
        bannerMessage.setText(message);

        // Set the background color
        banner.setBackgroundColor(backgroundColor);

        // Configure the button
        if (buttonText != null) {
            bannerAction.setVisibility(View.VISIBLE);
            bannerAction.setText(buttonText);
            bannerAction.setOnClickListener(buttonListener);
        } else {
            bannerAction.setVisibility(View.GONE);
        }

        // Show the banner
        banner.setVisibility(View.VISIBLE);
    }

    public void hideBanner() {
        banner.setVisibility(View.GONE);
    }
}
