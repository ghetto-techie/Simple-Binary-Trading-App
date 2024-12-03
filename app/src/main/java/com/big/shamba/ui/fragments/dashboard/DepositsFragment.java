package com.big.shamba.ui.fragments.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.ui.adapter.recyclerview.TransactionRecyclerViewAdapter;
import com.big.shamba.models.WalletTransaction;

import java.util.ArrayList;
import java.util.List;

public class DepositsFragment extends Fragment {
    private static final String TAG = "DepositsFragment";
    private RecyclerView recyclerView;
    private TransactionRecyclerViewAdapter transactionRecyclerViewAdapter;
    private List<WalletTransaction> walletTransactionList;

    public DepositsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.frament_deposits, container, false);
        Log.d(TAG, "onCreateView: Deposit fragment initialized");

        recyclerView = view.findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL));

        walletTransactionList = new ArrayList<>();
        // Add sample data to the list


        // Add more data as needed

        transactionRecyclerViewAdapter = new TransactionRecyclerViewAdapter(walletTransactionList);
        recyclerView.setAdapter(transactionRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // TODO: Fetch deposits from database
    public void fetchDeposits() {

    }
}
