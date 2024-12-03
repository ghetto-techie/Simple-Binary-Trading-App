package com.big.shamba.ui.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.big.shamba.R;
import com.big.shamba.models.FAQ;

import java.util.List;

public class FaqRecyclerViewAdapter extends RecyclerView.Adapter<FaqRecyclerViewAdapter.FaqViewHolder> {

    private List<FAQ> faqList;

    public FaqRecyclerViewAdapter(List<FAQ> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        FAQ faq = faqList.get(position);
        holder.questionTextView.setText(faq.getQuestion());
        holder.answerTextView.setText(faq.getAnswer());
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public static class FaqViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        TextView answerTextView;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.faqQuestionTV);
            answerTextView = itemView.findViewById(R.id.faqAnswerTV);
        }
    }
}
