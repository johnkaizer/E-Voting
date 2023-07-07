package com.example.e_voting.Voter;

import android.graphics.Color;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ResultsFragment extends Fragment {
    private FragmentResultsBinding binding;
    BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentResultsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        barChart = root.findViewById(R.id.barchart);
        fetchVoteCounts();


        return root;
    }
    private void fetchVoteCounts() {
        DatabaseReference votesReference = FirebaseDatabase.getInstance().getReference().child("Votes");

        // Add a listener to retrieve the vote counts from the database
        votesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>(); // To store the candidates' names

                // Iterate over the categories
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String category = categorySnapshot.getKey();

                    // Iterate over the candidates within each category
                    for (DataSnapshot candidateSnapshot : categorySnapshot.getChildren()) {
                        String candidateName = candidateSnapshot.getKey();
                        int voteCount = (int) candidateSnapshot.getChildrenCount();

                        // Add the vote count as a BarEntry to the entries list
                        entries.add(new BarEntry(entries.size(), voteCount));
                        labels.add(candidateName); // Add the candidate's name to the labels list
                    }
                }

                // Create a BarDataSet with the entries
                BarDataSet dataSet = new BarDataSet(entries, "Vote Counts");

                // Customize the appearance of the bar chart
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Set different colors for each bar
                dataSet.setValueTextColor(Color.BLACK); // Set the color of the value text
                dataSet.setValueTextSize(12f); // Set the size of the value text

                // Create a BarData object with the dataSet
                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.9f); // Set the width of the bars

                // Set the bar chart data
                barChart.setData(barData);

                // Set custom labels for the X-axis (candidates' names)
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Use the labels list as labels
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f); // Set the granularity to 1 to prevent skipping labels
                xAxis.setLabelRotationAngle(45f); // Rotate the labels by 45 degrees for better visibility

                // Refresh the chart
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur during data retrieval
                Toast.makeText(getContext(), "Failed to fetch vote counts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
