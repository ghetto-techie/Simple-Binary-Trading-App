package com.big.shamba.ui.adapter.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.Investment;
import com.big.shamba.models.InvestmentPackage;
import com.big.shamba.utility.FirestoreCollections;

import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;


public class MyInvestmentsRecyclerViewAdapter extends RecyclerView.Adapter<MyInvestmentsRecyclerViewAdapter.InvestmentViewHolder> {
    private static final String TAG = "MyInvestmentsRecyclerVi";
    private Context context;
    private List<Investment> investmentList;

    public MyInvestmentsRecyclerViewAdapter(Context context, List<Investment> investmentList) {
        this.context = context;
        this.investmentList = investmentList;
        Log.d(TAG, "MyInvestmentsRecyclerViewAdapter: ");
    }

    @NonNull
    @Override
    public InvestmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_investment, parent, false);
        return new InvestmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestmentViewHolder holder, int position) {
        Investment investment = investmentList.get(position);
        if (investment != null) {
            Log.d(TAG, "onBindViewHolder: Investment > " + investment);
            FirebaseFirestore
                    .getInstance()
                    .collection(FirestoreCollections.PACKAGES)
                    .document(investment.getPackageId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            InvestmentPackage investmentPackage = documentSnapshot.toObject(InvestmentPackage.class);
                            Log.d(TAG, "onBindViewHolder: Investment Package > " + investmentPackage);
                            if (investmentPackage != null) {
                                holder.packageNameInvestmentTV.setText(investmentPackage.getName());
                            }
                        } else {
                            holder.packageNameInvestmentTV.setText("--");
                            Log.d(TAG, "onBindViewHolder: Investment package not found");
                        }
                    })
                    .addOnFailureListener(e -> e.printStackTrace());

            holder.investmentAmountTV.setText("Ksh. " + investment.getAmount());
            if (!investment.isMatured()) {
                holder.investmentStatusChip.setText("Pending");
                holder.investmentStatusChip.setChipBackgroundColorResource(R.color.color_primary_light); // Reset color for pending
                holder.investmentStatusChip.setTextColor(ContextCompat.getColor(context, R.color.pendingTextColor)); // Reset text color for pending
            } else {
                holder.investmentStatusChip.setText("Matured");
                holder.investmentStatusChip.setChipBackgroundColorResource(R.color.color_accent);
                holder.investmentStatusChip.setTextColor(ContextCompat.getColor(context, R.color.pendingTextColor)); // Set text color for matured
            }
        }
    }

    @Override
    public int getItemCount() {
        return investmentList.size();
    }

    public static class InvestmentViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView packageImage;
        public TextView packageNameInvestmentTV;
        public TextView investmentAmountTV;
        public Chip investmentStatusChip;

        public InvestmentViewHolder(@NonNull View itemView) {
            super(itemView);
            packageImage = itemView.findViewById(R.id.packageImage);
            packageNameInvestmentTV = itemView.findViewById(R.id.packageNameInvestmentTV);
            investmentAmountTV = itemView.findViewById(R.id.investmentAmountTV);
            investmentStatusChip = itemView.findViewById(R.id.investmentStatusChip);
        }
    }
}
