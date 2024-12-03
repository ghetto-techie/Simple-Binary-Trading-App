package com.big.shamba.ui.adapter.recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.Investment;
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.ui.fragments.bottomsheets.EarningsInvestmentDetailsBottomSheet;
import com.big.shamba.utility.FirestoreCollections;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class EarningRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EarningRecyclerViewAdap";
    private final List<Investment> investments;
    private FragmentActivity activity;
    private boolean showShimmer = true;

    private static final int VIEW_TYPE_SHIMMER = 0;
    private static final int VIEW_TYPE_NORMAL = 1;


    public EarningRecyclerViewAdapter(FragmentActivity fragmentActivity, List<Investment> investments) {
        this.activity = fragmentActivity;
        this.investments = investments;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earnings_shimmer, parent, false);
            return new ShimmerViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earnings, parent, false);
            return new InvestmentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InvestmentViewHolder) {
            Log.d(TAG, "onBindViewHolder: ");
            Investment investment = investments.get(position);
            InvestmentViewHolder investmentViewHolder = (InvestmentViewHolder) holder;

            FirebaseFirestore.getInstance()
                    .collection(FirestoreCollections.PACKAGES)
                    .document(investment.getPackageId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String investmentPackageId = documentSnapshot.getId();
                            InvestmentPackage investmentPackage = documentSnapshot.toObject(InvestmentPackage.class);
                            investmentViewHolder.packageNameTV.setText(investmentPackage.getName());
                            investmentViewHolder.amount.setText(String.valueOf(investment.getAmount()));
                            String profit = "+" + getProfitOfIndividualInvestment(investment, investmentPackage);
                            investmentViewHolder.investmentProfitTV.setText(
                                    profit
                            );
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, d MMM", Locale.getDefault());
                            investmentViewHolder.investmentEndDateTV.setText(dateFormat.format(investment.getEndDate()));

                            Glide.with(investmentViewHolder.packageImage.getContext())
                                    .load(investmentPackage.getImgUrl()) // Assuming you have an image URL in your Package class
                                    .centerCrop()
                                    .placeholder(R.color.color_primary_light) // Placeholder image while loading
                                    .into(investmentViewHolder.packageImage)
                            ;

                            investmentViewHolder.itemView.setOnClickListener(view -> {
                                EarningsInvestmentDetailsBottomSheet earningsInvestmentDetailsBottomSheet =
                                        EarningsInvestmentDetailsBottomSheet.newInstance(investmentPackage, investment);
                                earningsInvestmentDetailsBottomSheet
                                        .show(activity.getSupportFragmentManager(), earningsInvestmentDetailsBottomSheet.getTag());
                            });
                        } else {
                            investmentViewHolder.packageNameTV.setText("Loading...");
                            investmentViewHolder.investmentProfitTV.setText("Calculating...");
                            investmentViewHolder.investmentEndDateTV.setText("...");
                        }
                    });
        } else if (holder instanceof ShimmerViewHolder) {
            // Do nothing, as ShimmerViewHolder is just a placeholder for the shimmer effect
        }
    }

    @Override
    public int getItemCount() {
        return showShimmer ? 10 : investments.size();
    }

    static class InvestmentViewHolder extends RecyclerView.ViewHolder {
        TextView packageNameTV;
        TextView investmentProfitTV;
        TextView investmentEndDateTV;
        TextView amount;
        ImageView packageImage;

        public InvestmentViewHolder(@NonNull View itemView) {
            super(itemView);
            packageNameTV = itemView.findViewById(R.id.packageNameTV);
            investmentProfitTV = itemView.findViewById(R.id.investmentProfitTV);
            investmentEndDateTV = itemView.findViewById(R.id.investmentEndDateTV);
            amount = itemView.findViewById(R.id.principleAmount);
            packageImage = itemView.findViewById(R.id.packageImage);
        }
    }

    static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;

        public ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayout);
        }
    }

    private double getProfitOfIndividualInvestment(Investment investment, InvestmentPackage investmentPackage) {
        Log.d(TAG, "getProfitOfIndividualInvestment: investmentPackage -> " + investmentPackage);
        if (investmentPackage != null) {
            Log.d(TAG, "calculateProfit: Investment package -> " + investmentPackage);
            double interestRate = investmentPackage.getInterestRate();
            double amount = investment.getAmount();
            double profit = amount * (interestRate / 100);

            Log.d(TAG, "getProfitOfIndividualInvestment: Amount -> " + amount);
            Log.d(TAG, "getProfitOfIndividualInvestment: Interest rate -> " + interestRate);
            Log.d(TAG, "getProfitOfIndividualInvestment: Profit " + amount + " x ( " + interestRate + " / 100 ) -> " + profit);
            return profit;
        } else return 0;
    }
}