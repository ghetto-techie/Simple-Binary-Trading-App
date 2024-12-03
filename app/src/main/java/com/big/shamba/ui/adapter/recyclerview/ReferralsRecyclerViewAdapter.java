package com.big.shamba.ui.adapter.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.Referral;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReferralsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ReferralsRecyclerView";
    private static final int VIEW_TYPE_SHIMMER = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    private final Context context;
    private final List<Referral> teamMembers;
    private boolean showShimmer = true;

    public ReferralsRecyclerViewAdapter(Context context, List<Referral> teamMembers) {
        this.context = context;
        this.teamMembers = teamMembers;
        Log.d(TAG, "ReferralsRecyclerViewAdapter initialized.");
    }

    public void setShowShimmer(boolean showShimmer) {
        this.showShimmer = showShimmer;
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
            View view = LayoutInflater.from(context).inflate(R.layout.item_referral_shimmer, parent, false);
            return new ShimmerViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_referral, parent, false);
            return new ReferralViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReferralViewHolder) {
            Referral teamMember = teamMembers.get(position);
            if (teamMember != null) {
                ((ReferralViewHolder) holder).bind(teamMember);
            }
        }
    }

    @Override
    public int getItemCount() {
        return showShimmer ? 10 : teamMembers.size(); // Display 5 shimmer items while loading.
    }

    public class ReferralViewHolder extends RecyclerView.ViewHolder {
        CircularImageView teamImageTV;
        TextView teamUserNameTV;
        TextView teamDateJoinedTV;

        public ReferralViewHolder(@NonNull View itemView) {
            super(itemView);
            teamImageTV = itemView.findViewById(R.id.teamMemberImage);
            teamUserNameTV = itemView.findViewById(R.id.teamMemberNameTV);
            teamDateJoinedTV = itemView.findViewById(R.id.teamMemberDateJoinedTV);
        }

        public void bind(Referral teamMember) {
            Log.d(TAG, "bind: Referral > " + teamMember);
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());

//            Glide.with(context)
//                    .load(teamMember.getImageUrl())
//                    .placeholder(R.color.color_primary_light)
//                    .into(teamImageTV);

            teamUserNameTV.setText(teamMember.getName());
            teamDateJoinedTV.setText(dateFormat.format(teamMember.getDateJoined()));
        }
    }

    static class ShimmerViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;

        public ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayout);
        }
    }
}
