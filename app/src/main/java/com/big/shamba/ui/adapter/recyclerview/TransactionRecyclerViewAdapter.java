package com.big.shamba.ui.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.WalletTransaction;
import com.big.shamba.utility.TransactionType;

import java.util.List;

public class TransactionRecyclerViewAdapter extends RecyclerView.Adapter<TransactionRecyclerViewAdapter.TransactionViewHolder> {
    private List<WalletTransaction> walletTransactionList;

    public TransactionRecyclerViewAdapter(List<WalletTransaction> walletTransactionList) {
        this.walletTransactionList = walletTransactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deposit_withdrawl_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        WalletTransaction walletTransaction = walletTransactionList.get(position);
        if (walletTransaction != null) {
            if (walletTransaction.getType().equals(TransactionType.DEPOSIT)) {
                holder.depositValueTV.setText(String.format("+ Ksh. %s", walletTransaction.getAmount()));
            } else if (walletTransaction.getType().equals(TransactionType.WITHDRAWAL)) {
                holder.depositValueTV.setText(String.format("- Ksh. %s", walletTransaction.getAmount()));
            }
//            holder.transactionTypeTV.setText(transaction.getPlatform().toString());
            holder.transactionTimeStampTV.setText(walletTransaction.getTimeStamp().toString());
        }

    }

    @Override
    public int getItemCount() {
        return walletTransactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView depositValueTV;
        public TextView transactionTypeTV;
        public TextView transactionTimeStampTV;

        public TransactionViewHolder(View view) {
            super(view);
            depositValueTV = view.findViewById(R.id.depositValueTV);
            transactionTypeTV = view.findViewById(R.id.transactionTypeTV);
            transactionTimeStampTV = view.findViewById(R.id.transactionTimeStampTV);
        }
    }
}

