package com.big.shamba.ui.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.Investment;
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.ui.fragments.bottomsheets.EarningsInvestmentDetailsBottomSheet;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserInvestmentsRecyclerViewAdapter extends RecyclerView.Adapter<UserInvestmentsRecyclerViewAdapter.InvestmentViewHolder> {
    private static final String TAG = "UserInvestmentsAdapter";
    private FragmentActivity fragmentActivity;
    private List<Investment> investmentList;
    private Map<String, InvestmentPackage> packageMap = new HashMap<>(); // Cached packages

    public UserInvestmentsRecyclerViewAdapter(FragmentActivity fragmentActivity, List<Investment> investmentList) {
        this.fragmentActivity = fragmentActivity;
        this.investmentList = investmentList;
    }

    public void updateInvestments(List<Investment> newInvestments) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return investmentList.size();
            }

            @Override
            public int getNewListSize() {
                return newInvestments.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                String oldId = investmentList.get(oldItemPosition).getInvestmentId();
                String newId = newInvestments.get(newItemPosition).getInvestmentId();

                // Use safe checks for null
                return oldId != null && oldId.equals(newId);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return investmentList.get(oldItemPosition).equals(newInvestments.get(newItemPosition));
            }
        });

        investmentList.clear();
        investmentList.addAll(newInvestments);
        diffResult.dispatchUpdatesTo(this);
    }

    public void updatePackages(Map<String, InvestmentPackage> newPackageMap) {
        packageMap.clear();
        packageMap.putAll(newPackageMap);
        notifyDataSetChanged(); // Notify RecyclerView to rebind items
    }

    @NonNull
    @Override
    public InvestmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.item_investment, parent, false);
        return new InvestmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestmentViewHolder holder, int position) {
        Investment investment = investmentList.get(position);
        if (investment != null) {
            holder.investmentAmountTV.setText("Ksh. " + investment.getAmount());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            holder.investmentDate.setText(sdf.format(investment.getStartDate()));

            if (investment.isMatured()) {
                holder.investmentStatus.setImageResource(R.drawable.ic_done);
            } else {
                holder.investmentStatus.setImageResource(R.drawable.ic_pending);
            }

            // Set package name from the cache
            InvestmentPackage investmentPackage = packageMap.get(investment.getPackageId());
            holder.packageNameInvestmentTV.setText(
                    investmentPackage != null ? investmentPackage.getName() : "Loading..."
            );
            holder.itemView.setOnClickListener(view -> {
                EarningsInvestmentDetailsBottomSheet earningsInvestmentDetailsBottomSheet =
                        EarningsInvestmentDetailsBottomSheet.newInstance(investmentPackage, investment, false);
                earningsInvestmentDetailsBottomSheet
                        .show(fragmentActivity.getSupportFragmentManager(), earningsInvestmentDetailsBottomSheet.getTag());
            });
        }
    }


    @Override
    public int getItemCount() {
        return investmentList.size();
    }

    public static class InvestmentViewHolder extends RecyclerView.ViewHolder {
        public ImageView packageImage;
        public TextView packageNameInvestmentTV;
        public TextView investmentAmountTV;
        public TextView investmentDate;
        public ImageView investmentStatus;

        public InvestmentViewHolder(@NonNull View itemView) {
            super(itemView);
            packageImage = itemView.findViewById(R.id.packageImage);
            packageNameInvestmentTV = itemView.findViewById(R.id.packageNameInvestmentTV);
            investmentAmountTV = itemView.findViewById(R.id.investmentAmountTV);
            investmentDate = itemView.findViewById(R.id.investmentDate);
            investmentStatus = itemView.findViewById(R.id.investmentStatusIcon);
        }
    }
}
