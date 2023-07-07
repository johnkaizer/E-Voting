package com.example.e_voting.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_voting.Models.VotersModel;
import com.example.e_voting.Models.VotesModel;
import com.example.e_voting.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VotesAdapter extends RecyclerView.Adapter<VotesAdapter.ViewHolder> {

    Context context;
    ArrayList<VotesModel> list;

    public VotesAdapter(Context context, ArrayList<VotesModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.results_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VotesAdapter.ViewHolder holder, int position) {
        VotesModel votesModel = list.get(position);
        holder.nameTxt.setText(votesModel.getCategory());
        holder.category.setText(votesModel.getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        TextView category;
        TextView party;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.textView7);
            category = itemView.findViewById(R.id.category);
            party = itemView.findViewById(R.id.textView8);
        }
    }
}

