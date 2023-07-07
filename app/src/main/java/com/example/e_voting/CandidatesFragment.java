package com.example.e_voting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.e_voting.Adapters.CandidateCategoryAdapter;
import com.example.e_voting.Adapters.CandidatesAdapter;
import com.example.e_voting.Models.CandidateCategory;
import com.example.e_voting.Models.CandidatesModel;
import com.example.e_voting.databinding.FragmentCandidatesBinding;
import com.example.e_voting.databinding.FragmentVoteBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CandidatesFragment extends Fragment {
    private FragmentCandidatesBinding binding;
    private RecyclerView candidateRec;
    private CandidatesAdapter candidatesAdapter;
    private ArrayList<CandidatesModel> candidatesModels;
    private DatabaseReference databaseReference;
    private LottieAnimationView noCandidateAnimationView;
    private String category;

    public static CandidatesFragment newInstance(String category) {
        CandidatesFragment fragment = new CandidatesFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCandidatesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        candidateRec = root.findViewById(R.id.candidateRV);
        noCandidateAnimationView = root.findViewById(R.id.lottieAnimationView);

        // Get the category from the arguments
        if (getArguments() != null) {
            category = getArguments().getString("category");
        }

        // Set up RecyclerView and its adapter
        candidateRec.setLayoutManager(new LinearLayoutManager(getContext()));
        candidatesModels = new ArrayList<>();
        candidatesAdapter = new CandidatesAdapter(getContext(), candidatesModels);
        candidateRec.setAdapter(candidatesAdapter);
        // Set long-press listener for item deletion
        candidatesAdapter.setOnItemLongClickListener(new CandidatesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                // Show a confirmation dialog
                showConfirmationDialog(position);
            }
        });

        // Fetch candidates from Firebase Realtime Database for the selected category
        fetchCandidates();

        return root;
    }

    private void showConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Candidate");
        builder.setMessage("Are you sure you want to delete this candidate?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CandidatesModel candidate = candidatesModels.get(position);
                String candidateId = candidate.getImageUrl();

                // Get a reference to the "Candidates" node in the database
                DatabaseReference candidatesRef = FirebaseDatabase.getInstance().getReference().child("Candidates");

                // Remove the candidate from the "Candidates" node
                candidatesRef.child(category).child(candidateId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Get a reference to the "Votes" node in the database
                        DatabaseReference votesRef = FirebaseDatabase.getInstance().getReference().child("Votes");

                        // Remove the candidate node from the category in "Votes"
                        votesRef.child(category).child(candidateId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Delete the candidate from the list
                                candidatesModels.remove(position);

                                // Notify the adapter about the removal
                                candidatesAdapter.notifyItemRemoved(position);

                                Toast.makeText(getContext(), "Candidate deleted successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to delete candidate from Votes.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to delete candidate from Candidates.", Toast.LENGTH_SHORT).show();
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



    private void fetchCandidates() {
        // Get a reference to the "Candidates" node in the database for the selected category
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Candidates").child(category);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot candidateSnapshot : dataSnapshot.getChildren()) {
                        CandidatesModel candidate = candidateSnapshot.getValue(CandidatesModel.class);
                        candidatesModels.add(candidate);
                    }
                    candidatesAdapter.notifyDataSetChanged();
                } else {
                    // Show animation view when no candidates are found
                    noCandidateAnimationView.setVisibility(View.VISIBLE);
                    candidateRec.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve candidates", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
