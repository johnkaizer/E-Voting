package com.example.e_voting.Admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.e_voting.R;
import com.example.e_voting.databinding.FragmentAdminResultsBinding;
import com.example.e_voting.databinding.FragmentPollsBinding;
import com.github.mikephil.charting.charts.BarChart;

public class AdminResultsFragment extends Fragment {
    private FragmentAdminResultsBinding binding;
    BarChart barChart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminResultsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        barChart = root.findViewById(R.id.barchart);


        return root;
    }
}