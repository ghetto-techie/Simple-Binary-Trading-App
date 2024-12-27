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
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserInvestmentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "UserInvestmentsAdapter";
    private static final int VIEW_TYPE_SHIMMER = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private FragmentActivity fragmentActivity;
    private List<Investment> investmentList;
    private List<String> newlyMaturedIds;
    private List<String> newlyPendingIds;
    private Map<String, InvestmentPackage> packageMap = new HashMap<>();
    private boolean showShimmer = true; // Initially show shimmer

    public UserInvestmentsRecyclerViewAdapter(FragmentActivity fragmentActivity, List<Investment> investmentList) {
        this.fragmentActivity = fragmentActivity;
        this.investmentList = investmentList;
    }

    public void setShowShimmer(boolean showShimmer) {
        this.showShimmer = showShimmer;
        notifyDataSetChanged(); // Notify the adapter to switch view types
    }

    public void updateInvestments(List<Investment> newInvestments, List<String> newMaturedIds) {
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
                return investmentList.get(oldItemPosition).getInvestmentId()
                        .equals(newInvestments.get(newItemPosition).getInvestmentId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return investmentList.get(oldItemPosition).equals(newInvestments.get(newItemPosition));
            }
        });

        this.investmentList = newInvestments;
        this.newlyMaturedIds = newMaturedIds; // Track new matured IDs for highlighting
        diffResult.dispatchUpdatesTo(this);
    }

    public void updateInvestments(List<Investment> newInvestments, List<String> newPendingIds, List<String> newMaturedIds) {
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
                return investmentList.get(oldItemPosition).getInvestmentId()
                        .equals(newInvestments.get(newItemPosition).getInvestmentId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return investmentList.get(oldItemPosition).equals(newInvestments.get(newItemPosition));
            }
        });

        this.investmentList = newInvestments;
        this.newlyPendingIds = newPendingIds;
        this.newlyMaturedIds = newMaturedIds; // Track for highlighting
        diffResult.dispatchUpdatesTo(this);
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
            View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.item_investment_shimmer, parent, false);
            return new ShimmerViewHolder(view);
        } else {
            View view = LayoutInflater.from(fragmentActivity).inflate(R.layout.item_investment, parent, false);
            return new InvestmentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InvestmentViewHolder) {
            Investment investment = investmentList.get(position);
            if (investment != null) {
                // Set package name from the cache
                InvestmentPackage investmentPackage = packageMap.get(investment.getPackageId());

                holder.itemView.setOnClickListener(view -> {
                    EarningsInvestmentDetailsBottomSheet earningsInvestmentDetailsBottomSheet =
                            EarningsInvestmentDetailsBottomSheet.newInstance(investmentPackage, investment, false);
                    earningsInvestmentDetailsBottomSheet
                            .show(fragmentActivity.getSupportFragmentManager(), earningsInvestmentDetailsBottomSheet.getTag());
                });
                ((InvestmentViewHolder) holder).packageNameInvestmentTV.setText(
                        investmentPackage != null ? investmentPackage.getName() : "Loading..."
                );
                ((InvestmentViewHolder) holder).bind(investment);
            }
        }
    }

    @Override
    public int getItemCount() {
        return showShimmer ? 25 : investmentList.size(); // Show shimmer for 10 items by default
    }


    // Normal ViewHolder
    public static class InvestmentViewHolder extends RecyclerView.ViewHolder {
        private ImageView packageImage;
        private TextView packageNameInvestmentTV;
        private TextView investmentAmountTV;
        private TextView investmentDate;
        private ImageView investmentStatus;

        public InvestmentViewHolder(@NonNull View itemView) {
            super(itemView);
            packageImage = itemView.findViewById(R.id.packageImage);
            packageNameInvestmentTV = itemView.findViewById(R.id.packageNameInvestmentTV);
            investmentAmountTV = itemView.findViewById(R.id.investmentAmountTV);
            investmentDate = itemView.findViewById(R.id.investmentDate);
            investmentStatus = itemView.findViewById(R.id.investmentStatusIcon);
        }

        public void bind(Investment investment) {
            investmentAmountTV.setText("Ksh. " + investment.getAmount());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            investmentDate.setText(sdf.format(investment.getStartDate()));

            if (investment.isMatured()) {
                investmentStatus.setImageResource(R.drawable.ic_done);
            } else {
                investmentStatus.setImageResource(R.drawable.ic_pending);
            }
        }
    }

    // Shimmer ViewHolder
    static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        private ShimmerFrameLayout shimmerFrameLayout;

        public ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayout);
        }
    }
}
