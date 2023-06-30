package com.example.e_voting.Admin;

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

import com.airbnb.lottie.LottieAnimationView;
import com.example.e_voting.Adapters.VoterAdapter;
import com.example.e_voting.Models.VotersModel;
import com.example.e_voting.R;
import com.example.e_voting.databinding.FragmentHomeVBinding;
import com.example.e_voting.databinding.FragmentVotesManagementBinding;
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

        return root;
    }
}

