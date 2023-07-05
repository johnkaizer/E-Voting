package com.example.e_voting.Admin;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.e_voting.Adapters.PollsAdapter;
import com.example.e_voting.Adapters.VoterAdapter;
import com.example.e_voting.Models.Polls;
import com.example.e_voting.Models.VotersModel;
import com.example.e_voting.R;
import com.example.e_voting.databinding.FragmentHomeVBinding;
import com.example.e_voting.databinding.FragmentVotesManagementBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VotesManagementFragment extends Fragment {
    private FragmentVotesManagementBinding binding;
    private RecyclerView voterRec;
    private VoterAdapter voterAdapter;
    private ArrayList<VotersModel> voterList;
    private DatabaseReference databaseReference;
    private LottieAnimationView noVoterAnimationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVotesManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        voterRec = root.findViewById(R.id.votersRV);
        noVoterAnimationView = root.findViewById(R.id.lottieAnimationView);

        // Set up RecyclerView and adapter
        voterRec.setLayoutManager(new LinearLayoutManager(getActivity()));
        voterList = new ArrayList<>(); // Initialize the voterList
        voterAdapter = new VoterAdapter(getActivity(), voterList);
        voterRec.setAdapter(voterAdapter);

        // Get a reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("Voters");

        // Retrieve voters from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing list
                voterList.clear();

                // Iterate through the snapshot to retrieve voters
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VotersModel voter = dataSnapshot.getValue(VotersModel.class);
                    if (voter != null) {
                        voterList.add(voter);
                    }
                }

                // Update the adapter with the new data
                voterAdapter.setVoterList(voterList);

                // Show/hide animation based on the availability of voters
                if (voterList.isEmpty()) {
                    noVoterAnimationView.setVisibility(View.VISIBLE);
                    noVoterAnimationView.playAnimation();
                } else {
                    noVoterAnimationView.setVisibility(View.GONE);
                    noVoterAnimationView.cancelAnimation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error cases
                Log.e("VotesManagementFragment", "Failed to retrieve voters: " + error.getMessage());
            }
        });
        // Set long-press listener for item deletion
        voterAdapter.setOnItemLongClickListener(new VoterAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                // Show a confirmation dialog
                showConfirmationDialog(position);

            }
        });

        return root;
    }

    private void showConfirmationDialog(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Delete Voter");
        builder.setMessage("Are you sure you want to delete this Voter?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VotersModel votersModel = voterList.get(position);
                String voterId = votersModel.getIdNumber();

                // Get a reference to the "Voter" node in the database
                DatabaseReference voterRef = FirebaseDatabase.getInstance().getReference().child("Voters");

                // Remove the Voter from the "Voters" node
                voterRef.child(voterId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Get a reference to the "Voter" node in the database
                        DatabaseReference voterRef = FirebaseDatabase.getInstance().getReference().child("Voters");

                        // Remove the voterRef node from the category in "voters"
                        voterRef.child(voterId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Delete the voter from the list
                                voterList.remove(position);

                                // Notify the adapter about the removal
                                voterAdapter.notifyItemRemoved(position);

                                Toast.makeText(getContext(), "Voter deleted successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to delete voter from Voters.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to delete voter from Voters.", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}

