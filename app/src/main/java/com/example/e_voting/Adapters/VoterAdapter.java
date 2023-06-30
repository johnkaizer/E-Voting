package com.example.e_voting.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_voting.Models.VoterEducation;
import com.example.e_voting.Models.VotersModel;
import com.example.e_voting.R;

import java.util.ArrayList;

public class VoterAdapter extends RecyclerView.Adapter<VoterAdapter.ViewHolder> {
    Context context;
    ArrayList<VotersModel> list;

    public VoterAdapter(Context context, ArrayList<VotersModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setVoterList(ArrayList<VotersModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.voters_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VotersModel votersModel = list.get(position);
        holder.nameTxt.setText(votersModel.getfName());
        holder.lastTxt.setText(votersModel.getsName());
        holder.idText.setText(votersModel.getIdNumber());
        holder.genderTxt.setText(votersModel.getGender());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        TextView genderTxt;
        TextView idText;
        TextView lastTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.username);
            lastTxt = itemView.findViewById(R.id.email);
            idText = itemView.findViewById(R.id.password);
            genderTxt = itemView.findViewById(R.id.phone);
        }
    }
}


