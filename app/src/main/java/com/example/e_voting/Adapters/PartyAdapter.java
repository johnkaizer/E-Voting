package com.example.e_voting.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_voting.Models.PartiesModel;
import com.example.e_voting.Models.VoterEducation;
import com.example.e_voting.R;
import com.example.e_voting.Voter.HomeVFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PartyAdapter extends RecyclerView.Adapter<PartyAdapter.ViewHolder> {

    Context context;
    ArrayList<PartiesModel>list;

    public PartyAdapter(Context context, ArrayList<PartiesModel> list, HomeVFragment homeVFragment) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PartyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.edu_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull PartyAdapter.ViewHolder holder, int position) {
        PartiesModel partiesModel = list.get(position);
        holder.titleText.setText(partiesModel.getTitle());
        Picasso.get().load(partiesModel.getLogo()).into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the dialog pop-up
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(partiesModel.getTitle());
                builder.setMessage(partiesModel.getDescription());
                builder.setPositiveButton("OK", null); // Add a button to close the dialog
                builder.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleText = itemView.findViewById(R.id.textView);
            cardView = itemView.findViewById(R.id.cardView1);

        }
    }
}
