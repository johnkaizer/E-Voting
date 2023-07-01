package com.example.e_voting.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_voting.Models.CandidateCategory;
import com.example.e_voting.Models.CategoryModel;
import com.example.e_voting.R;

import java.util.ArrayList;

public class CandidateCategoryAdapter extends RecyclerView.Adapter<CandidateCategoryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CandidateCategory> list;
    private OnItemClickListener listener;

    public CandidateCategoryAdapter(Context context, ArrayList<CandidateCategory> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CandidateCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_category_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull CandidateCategoryAdapter.ViewHolder holder, int position) {
        CandidateCategory categoryModel = list.get(position);
        holder.title.setText(categoryModel.getTitle());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }
    }
}