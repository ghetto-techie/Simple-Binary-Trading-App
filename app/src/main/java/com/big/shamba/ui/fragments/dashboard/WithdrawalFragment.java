package com.big.shamba.ui.fragments.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.big.shamba.R;
import com.big.shamba.ui.adapter.recyclerview.TransactionRecyclerViewAdapter;
import com.big.shamba.models.WalletTransaction;

import java.util.ArrayList;
import java.util.List;


public class WithdrawalFragment extends Fragment {
    private RecyclerView recyclerView;
    private TransactionRecyclerViewAdapter transactionRecyclerViewAdapter;
    private List<WalletTransaction> walletTransactionList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_withdrawal, container, false);
        recyclerView = view.findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        walletTransactionList = new ArrayList<>();

        transactionRecyclerViewAdapter = new TransactionRecyclerViewAdapter(walletTransactionList);
        recyclerView.setAdapter(transactionRecyclerViewAdapter);
        return view;
    }

    // TODO: Fetch Withdrawals from database
    public void fetchDeposits() {

    }
}