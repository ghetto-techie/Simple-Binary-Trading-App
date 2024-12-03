package com.big.shamba.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.Investment;

import java.util.ArrayList;
import java.util.List;

import com.big.shamba.models.InvestmentPackage;
import com.google.android.material.chip.Chip;
import com.mikhaellopez.circularimageview.CircularImageView;


public class MyInvestmentAdapter extends RecyclerView.Adapter<MyInvestmentAdapter.InvestmentViewHolder> {

    private List<Investment> investmentList;

    public MyInvestmentAdapter(List<Investment> investmentList) {
        this.investmentList = investmentList;
    }

    @NonNull
    @Override
    public InvestmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_investment, parent, false);
        return new InvestmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestmentViewHolder holder, int position) {
        Investment investment = investmentList.get(position);
        holder.bind(investment);
    }

    @Override
    public int getItemCount() {
        return investmentList.size();
    }

    static class InvestmentViewHolder extends RecyclerView.ViewHolder {

        CircularImageView packageImage;
        TextView packageNameInvestmentTV;
        TextView investmentAmountTV;
        Chip investmentStatusChip;

        InvestmentViewHolder(View itemView) {
            super(itemView);
            packageImage = itemView.findViewById(R.id.packageImage);
            packageNameInvestmentTV = itemView.findViewById(R.id.packageNameInvestmentTV);
            investmentAmountTV = itemView.findViewById(R.id.investmentAmountTV);
            investmentStatusChip = itemView.findViewById(R.id.investmentStatusChip);
        }

        void bind(Investment investment) {
            // Assuming you have a method to get the InvestmentPackage object by packageId
            InvestmentPackage investmentPackage = getInvestmentPackageById(investment.getPackageId());
            packageNameInvestmentTV.setText(investmentPackage.getName());
            investmentAmountTV.setText(String.format("$%.2f", investment.getAmount()));

            if (investmentPackage.getImgUrl() != null && !investmentPackage.getImgUrl().isEmpty()) {
//                Picasso.get().load(investmentPackage.getImgUrl()).into(packageImage);
            } else {
                packageImage.setImageResource(R.drawable.icon_dark); // Provide a default image
            }

            if (investment.isMatured()) {
                investmentStatusChip.setText("Matured");
                investmentStatusChip.setChipIconResource(R.drawable.ic_matured); // Provide an appropriate icon
//                investmentStatusChip.setChipBackgroundColorResource(R.color.matured_chip_background); // Provide an appropriate background color
            } else {
                investmentStatusChip.setText("Pending");
                investmentStatusChip.setChipIconResource(R.drawable.ic_pending); // Provide an appropriate icon
//                investmentStatusChip.setChipBackgroundColorResource(R.color.pending_chip_background); // Provide an appropriate background color
            }
        }

        private InvestmentPackage getInvestmentPackageById(String packageId) {
            // Implement this method to fetch the InvestmentPackage object by packageId
            // This could be a lookup from a list or a database query, depending on your implementation
            return new InvestmentPackage(); // Placeholder return, replace with actual implementation
        }
    }
}
