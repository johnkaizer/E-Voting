package com.example.e_voting.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_voting.Models.CandidatesModel;
import com.example.e_voting.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class CandidatesAdapter extends FirebaseRecyclerAdapter<CandidatesModel,CandidatesAdapter.ViewHolder> {
    Context context;
    public CandidatesAdapter(@NonNull FirebaseRecyclerOptions<CandidatesModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CandidatesAdapter.ViewHolder holder, int i, @NonNull CandidatesModel model) {
        getRef(i).getKey();
        holder.nameText.setText(model.getFullName());
        holder.partyName.setText(model.getParty());
        holder.category.setText(model.getCategory());
        Picasso.get().load(model.getPartyLogo()).into(holder.partyLogo);
        Picasso.get().load(model.getImage()).into(holder.picture);

    }

    @NonNull
    @Override
    public CandidatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_items, parent, false));

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView partyName;
        TextView category;
        ImageView picture;
        ImageView partyLogo;
        Button voteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textView7);
            partyName = itemView.findViewById(R.id.textView8);
            category = itemView.findViewById(R.id.category);
            picture = itemView.findViewById(R.id.imageView2);
            partyLogo = itemView.findViewById(R.id.imageView3);
            voteBtn = itemView.findViewById(R.id.button3);


        }
    }
}
