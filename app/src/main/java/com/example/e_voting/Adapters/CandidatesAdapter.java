package com.example.e_voting.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_voting.Constants;
import com.example.e_voting.FaceDetectionFragment;
import com.example.e_voting.Models.CandidatesModel;
import com.example.e_voting.Models.VotesModel;
import com.example.e_voting.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CandidatesAdapter extends RecyclerView.Adapter<CandidatesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CandidatesModel> candidatesList;

    public CandidatesAdapter(Context context, ArrayList<CandidatesModel> candidatesList) {
        this.context = context;
        this.candidatesList = candidatesList;
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }


    @NonNull
    @Override
    public CandidatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_items, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidatesAdapter.ViewHolder holder, int position) {
        CandidatesModel model = candidatesList.get(position);
        holder.nameText.setText(model.getFullName());
        holder.partyName.setText(model.getParty());
        holder.category.setText(model.getCategory());
        String imageUrl = model.getImageUrl();
        // Load the image using Picasso
        Picasso.get().load(imageUrl).into(holder.picture);


        // Set long press listener for the card view
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(holder.getAdapterPosition());
                }
                return true;
            }
        });

        // Set click listener for vote
        holder.voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference votesReference = FirebaseDatabase.getInstance().getReference().child("Votes");

                String category = model.getCategory();
                String candidateName = model.getFullName();
                String userUid = Constants.getCurrentUserID();

                // Create a reference to the category node in Votes
                DatabaseReference categoryReference = votesReference.child(category);

                // Check if the user has already voted for this candidate
                categoryReference.child(candidateName).child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // User has already voted for this candidate
                            Toast.makeText(context, "You have already voted or failed to authenticate face contact admin.", Toast.LENGTH_SHORT).show();
                        } else {
                            // User has not voted for this candidate, save the vote
                            VotesModel vote = new VotesModel(candidateName, category, model.getParty(), userUid);

                            // Add the user's vote under the candidate's node
                            categoryReference.child(candidateName).child(userUid).setValue(vote)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            FaceDetectionFragment newFragment = new FaceDetectionFragment();
                                            fragmentTransaction.replace(R.id.frame_layout, newFragment);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                            Toast.makeText(context, "verify face for a successful vote.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Failed to save vote.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed to check vote status.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return candidatesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView partyName;
        TextView category;
        ImageView picture;
        Button voteBtn;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textView7);
            partyName = itemView.findViewById(R.id.textView8);
            category = itemView.findViewById(R.id.category);
            picture = itemView.findViewById(R.id.imageView2);
            voteBtn = itemView.findViewById(R.id.button3);
            cardView = itemView.findViewById(R.id.cardItem);
        }
    }
}

