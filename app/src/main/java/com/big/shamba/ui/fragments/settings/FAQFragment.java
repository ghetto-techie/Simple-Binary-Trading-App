package com.big.shamba.ui.fragments.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.FAQ;
import com.big.shamba.ui.adapter.recyclerview.FaqRecyclerViewAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FAQFragment extends Fragment {
    private static final String TAG = "FAQFragment";
    private RecyclerView recyclerView;
    private FaqRecyclerViewAdapter faqRecyclerViewAdapter;
    private List<FAQ> faqList;
    private MaterialToolbar toolbar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("faq");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        toolbar = view.findViewById(R.id.faqToolbar);
        recyclerView = view.findViewById(R.id.recyclerViewFaqs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        faqList = new ArrayList<>();
        faqRecyclerViewAdapter = new FaqRecyclerViewAdapter(faqList);
        recyclerView.setAdapter(faqRecyclerViewAdapter);

        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Load FAQs from Firebase
        loadFaqs();
    }

    private void loadFaqs() {
        Log.d(TAG, "loadFaqs: ");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                faqList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FAQ faq = dataSnapshot.getValue(FAQ.class);
                    if (faq != null) {
                        faqList.add(faq);
                    }
                }
                faqRecyclerViewAdapter.notifyDataSetChanged();
                Log.d(TAG, "onDataChange: FAQ list updated, size: " + faqList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: Error fetching data", error.toException());
                Toast.makeText(requireContext(), "Failed to load FAQs", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
