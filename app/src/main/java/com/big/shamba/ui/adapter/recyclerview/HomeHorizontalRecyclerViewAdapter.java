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
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.ui.fragments.bottomsheets.InvestBottomSheet;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomeHorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HomeHorizontalRecyclerViewAdapter.HorizontalViewHolder> {
    private static final String TAG = "HomeHorizontalRecyclerV";
    private final List<InvestmentPackage> investmentPackageList;
    private final FragmentActivity activity;

    public HomeHorizontalRecyclerViewAdapter(FragmentActivity activity, List<InvestmentPackage> investmentPackageList) {
        Log.d(TAG, "HomeHorizontalRecyclerViewAdapter: ");
        this.activity = activity;
        this.investmentPackageList = investmentPackageList;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_investment_package, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        InvestmentPackage investmentPackage = investmentPackageList.get(position);
        if (investmentPackage != null) {
            Log.d(TAG, "onBindViewHolder: Package -> " + investmentPackage.toString());
            holder.bind(investmentPackage);
        }
    }

    @Override
    public int getItemCount() {
        if (investmentPackageList != null)
            return investmentPackageList.size();
        else return 0;
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView packageImage;
        TextView packageNameHomeTV;
        TextView packageRangeHomeTV;
        TextView packageTimePeriodHomeTV;
        TextView packageInterestHomeTV;
        MaterialButton packageInvestButton;

        HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "HorizontalViewHolder: ");
            cardView = itemView.findViewById(R.id.packageCardView);
            packageImage = itemView.findViewById(R.id.packageImageHome);
            packageNameHomeTV = itemView.findViewById(R.id.packageNameHomeTV);
            packageRangeHomeTV = itemView.findViewById(R.id.packageRangeHomeTV);
            packageTimePeriodHomeTV = itemView.findViewById(R.id.packageTimePeriodHomeTV);
            packageInterestHomeTV = itemView.findViewById(R.id.packageInterestHomeTV);
//            packageInvestButton = itemView.findViewById(R.id.packageInvestButton);
        }

        void bind(InvestmentPackage investmentPackage) {
            Log.d(TAG, "bind: ");
            String imgUrl = investmentPackage.getImgUrl();
            if (imgUrl != null && !imgUrl.isEmpty()) {
                Glide
                        .with(activity)
                        .load(imgUrl)
                        .placeholder(R.color.color_primary_light)
                        .into(packageImage)
                ;
            }
            packageNameHomeTV.setText(investmentPackage.getName());
            InvestmentPackage.Range investmentPackageRange = investmentPackage.getRange();
            double min = investmentPackageRange.getMin();
            double max = investmentPackageRange.getMax();
            String amountRange = "Ksh. " + min + " - Ksh. " + max;
            packageRangeHomeTV.setText(amountRange);
            String timePeriodInDays = investmentPackage.getTimePeriod() + " days";
            packageTimePeriodHomeTV.setText(timePeriodInDays);
            String interestRateInPercentage = investmentPackage.getInterestRate() + "% interest rate";
            packageInterestHomeTV.setText(interestRateInPercentage);

            this.itemView.setOnClickListener(view -> {
                InvestBottomSheet bottomSheet = InvestBottomSheet.newInstance(investmentPackage);
                bottomSheet.show(activity.getSupportFragmentManager(), bottomSheet.getTag());
            });
        }
    }
}
