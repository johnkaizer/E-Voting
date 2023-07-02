package com.example.e_voting;

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
import com.example.e_voting.Adapters.CandidatesAdapter;
import com.example.e_voting.Adapters.VotesAdapter;
import com.example.e_voting.Models.CandidatesModel;
import com.example.e_voting.Models.VotesModel;
import com.example.e_voting.databinding.FragmentCandidatesBinding;
import com.example.e_voting.databinding.FragmentVotesCountBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VotesCountFragment extends Fragment {
    private FragmentVotesCountBinding binding;
    private RecyclerView candidateRec;
    private VotesAdapter votesAdapter;
    private ArrayList<VotesModel> votesModels;
    private DatabaseReference databaseReference;
    private LottieAnimationView noVotesAnimationView;
    private String category;
    public static VotesCountFragment newInstance(String category) {
        VotesCountFragment fragment = new VotesCountFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVotesCountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        candidateRec = root.findViewById(R.id.candidateRV);
        noVotesAnimationView = root.findViewById(R.id.lottieAnimationView);

        // Get the category from the arguments
        if (getArguments() != null) {
            category = getArguments().getString("category");
        }

        // Set up RecyclerView and its adapter
        candidateRec.setLayoutManager(new LinearLayoutManager(getContext()));
        votesModels = new ArrayList<>();
        votesAdapter = new VotesAdapter(getContext(), votesModels);
        candidateRec.setAdapter(votesAdapter);

        // Fetch Votes from Firebase Realtime Database for the selected category
        fetchVotes();

        return root;
    }

    private void fetchVotes() {
        // Get a reference to the "Votes" node in the database for the selected category
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Votes").child(category);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String categoryTitle = categorySnapshot.getKey();

                        for (DataSnapshot candidateSnapshot : categorySnapshot.getChildren()) {
                            String candidateName = candidateSnapshot.getKey();

                            // Create a new VotesModel object with the category and name
                            VotesModel votesModel = new VotesModel();
                            votesModel.setCategory(categoryTitle);
                            votesModel.setName(candidateName);

                            // Add the VotesModel to the list
                            votesModels.add(votesModel);
                        }
                    }

                    // Notify the adapter that the data has changed
                    votesAdapter.notifyDataSetChanged();
                } else {
                    // Show animation view when no categories are found
                    noVotesAnimationView.setVisibility(View.VISIBLE);
                    candidateRec.setVisibility(View.GONE);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve votes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}