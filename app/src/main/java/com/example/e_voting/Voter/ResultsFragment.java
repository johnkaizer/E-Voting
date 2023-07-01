package com.example.e_voting.Voter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.e_voting.Adapters.CandidateCategoryAdapter;
import com.example.e_voting.CandidatesFragment;
import com.example.e_voting.Models.CandidateCategory;
import com.example.e_voting.R;
import com.example.e_voting.VotesCountFragment;
import com.example.e_voting.databinding.FragmentResultsBinding;
import com.example.e_voting.databinding.FragmentVoteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ResultsFragment extends Fragment {
    private FragmentResultsBinding binding;
    private RecyclerView categoryRec;
    private CandidateCategoryAdapter candidateCategoryAdapter;
    private ArrayList<CandidateCategory> categories;
    private DatabaseReference databaseReference;
    private LottieAnimationView noCategoryAnimationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResultsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        categoryRec = root.findViewById(R.id.candidateCategory);
        noCategoryAnimationView = root.findViewById(R.id.lottieAnimationView);

        // Set up RecyclerView and its adapter
        categoryRec.setLayoutManager(new LinearLayoutManager(getContext()));
        categories = new ArrayList<>();
        candidateCategoryAdapter = new CandidateCategoryAdapter(getContext(), categories);
        categoryRec.setAdapter(candidateCategoryAdapter);

        // Fetch categories from Firebase Realtime Database
        fetchCategories();

        // Set item click listener for category items
        candidateCategoryAdapter.setOnItemClickListener(new CandidateCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CandidateCategory selectedCategory = categories.get(position);
                String category = selectedCategory.getTitle();

                // Start VotesFragment and pass the selected category
                VotesCountFragment votesCountFragment = VotesCountFragment.newInstance(category);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, votesCountFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return root;
    }

    private void fetchCategories() {
        // Get a reference to the "Votes" node in the database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Votes");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate through the children of the "Votes" node
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String categoryTitle = categorySnapshot.getKey();

                        // Create a new VotesCategory object with the category title
                        CandidateCategory category = new CandidateCategory(categoryTitle);

                        // Add the category to the list
                        categories.add(category);
                    }

                    // Notify the adapter that the data has changed
                    candidateCategoryAdapter.notifyDataSetChanged();
                } else {
                    // Show animation view when no categories are found
                    noCategoryAnimationView.setVisibility(View.VISIBLE);
                    categoryRec.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval
                Toast.makeText(getContext(), "Failed to retrieve categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
