package com.example.e_voting.Adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_voting.Models.Polls;
import com.example.e_voting.Models.VotersModel;
import com.example.e_voting.R;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PollsAdapter extends RecyclerView.Adapter<PollsAdapter.ViewHolder> {

    Context context;
    ArrayList<Polls>list;

    public PollsAdapter(Context context, ArrayList<Polls> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PollsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.polls_items, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull PollsAdapter.ViewHolder holder, int position) {
        Polls polls = list.get(position);
        holder.titleTxt.setText(polls.getTitle());
        holder.dateTxt.setText(polls.getpDate());
        holder.timeText.setText(polls.getpTime());


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        TextView dateTxt;
        TextView timeText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.textView);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            timeText = itemView.findViewById(R.id.pollTime);
        }
    }
}
