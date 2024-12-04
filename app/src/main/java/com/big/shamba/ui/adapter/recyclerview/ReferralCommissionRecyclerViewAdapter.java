package com.big.shamba.ui.adapter.recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.Referral;
import com.big.shamba.models.ReferralCommissionWalletTransaction;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReferralCommissionRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ReferralCommissionRecyc";
    private static final int VIEW_TYPE_SHIMMER = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private List<Pair<ReferralCommissionWalletTransaction, Referral>> referralCommissions;
    private boolean showShimmer = true;

    public ReferralCommissionRecyclerViewAdapter(List<Pair<ReferralCommissionWalletTransaction, Referral>> referralCommissions) {
        this.referralCommissions = referralCommissions;
    }

    public void updateReferralCommissions(List<Pair<ReferralCommissionWalletTransaction, Referral>> newReferralCommissions) {
        this.referralCommissions.clear();
        this.referralCommissions.addAll(newReferralCommissions);
        notifyDataSetChanged();
    }

    public void setShowShimmer(boolean showShimmer) {
        this.showShimmer = showShimmer;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return showShimmer ? VIEW_TYPE_SHIMMER : VIEW_TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SHIMMER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commission_shimmer, parent, false);
            return new ShimmerViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commission, parent, false);
            return new ReferralCommissionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReferralCommissionViewHolder) {
            Pair<ReferralCommissionWalletTransaction, Referral> pair = referralCommissions.get(position);
            if (pair != null) {
                Referral referral = pair.second;
                ReferralCommissionWalletTransaction transaction = pair.first;

                ((ReferralCommissionViewHolder) holder).onBind(referral, transaction);
            }
        } else if (holder instanceof ShimmerViewHolder) {
            // ShimmerViewHolder is just for the placeholder effect, no binding required.
        }
    }

    @Override
    public int getItemCount() {
        return showShimmer ? 10 : referralCommissions.size(); // Show 5 shimmer placeholders by default.
    }

    static class ReferralCommissionViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView commission;
        private final TextView date;

        public ReferralCommissionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.referralName);
            commission = itemView.findViewById(R.id.referralCommission);
            date = itemView.findViewById(R.id.referralCommissionDate);
        }

        public void onBind(Referral referral, ReferralCommissionWalletTransaction transaction) {
            Log.d(TAG, "onBind: Transactions -> " + transaction.toString() + "\n: Referral -> " + referral.toString());

            // Handle referral name
            name.setText(referral != null ? referral.getName() : "Unknown Referral");

            // Handle commission
            commission.setText(String.format("+%s", transaction.getAmount()));

            // Handle date formatting with null-check
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            if (transaction.getTimeStamp() != null) {
                Log.d(TAG, "onBind: Timestamp -> " + transaction.getTimeStamp());
                date.setText(sdf.format(transaction.getTimeStamp()));
            } else {
                Log.d(TAG, "onBind: Timestamp is null");
                date.setText("Date unavailable");
            }
        }

    }

    static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;

        public ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayout);
        }
    }
}
