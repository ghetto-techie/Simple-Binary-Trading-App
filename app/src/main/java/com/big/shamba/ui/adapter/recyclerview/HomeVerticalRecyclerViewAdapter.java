package com.big.shamba.ui.adapter.recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.dto.Category;

import java.util.List;

public class HomeVerticalRecyclerViewAdapter extends RecyclerView.Adapter<HomeVerticalRecyclerViewAdapter.HomeVerticalViewHolder> {
    private static final String TAG = "HomeVerticalRecyclerView";
    private List<Category> categories;
    private FragmentActivity fragmentActivity;

    public HomeVerticalRecyclerViewAdapter(FragmentActivity fragmentActivity, List<Category> categories) {
        this.categories = categories;
        this.fragmentActivity = fragmentActivity;
        Log.d(TAG, "HomeVerticalRecyclerViewAdapter: Initiated");
    }

    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged(); // Important to refresh the adapter
    }

    @NonNull
    @Override
    public HomeVerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_packages_layout, parent, false);
        return new HomeVerticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeVerticalViewHolder holder, int position) {
        Category category = categories.get(position);
        if (category != null) {
            holder.packageTypeTV.setText(category.getName());
            HomeHorizontalRecyclerViewAdapter horizontalRecyclerViewAdapter =
                    new HomeHorizontalRecyclerViewAdapter(fragmentActivity, category.getPackageList());
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(
                    holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(horizontalRecyclerViewAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    // ViewHolder as an inner class
    static class HomeVerticalViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recyclerView;
        private final TextView packageTypeTV;

        public HomeVerticalViewHolder(@NonNull View itemView) {
            super(itemView);
            packageTypeTV = itemView.findViewById(R.id.packageTypeTV);
            recyclerView = itemView.findViewById(R.id.horizontalPackagesRecyclerView);
        }
    }
}