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

import com.example.e_voting.Models.VoterEducation;
import com.example.e_voting.R;
import com.example.e_voting.Voter.HomeVFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VoterEducationAdapter extends RecyclerView.Adapter<VoterEducationAdapter.ViewHolder> {

    Context context;
    ArrayList<VoterEducation>list;

    public VoterEducationAdapter(Context context, ArrayList<VoterEducation> list, HomeVFragment homeVFragment) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VoterEducationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.edu_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull VoterEducationAdapter.ViewHolder holder, int position) {
        VoterEducation voterEducation = list.get(position);
        holder.titleText.setText(voterEducation.getTitle());
        Picasso.get().load(voterEducation.getImage()).into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the dialog pop-up
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(voterEducation.getTitle());
                builder.setMessage(voterEducation.getDescription());
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
