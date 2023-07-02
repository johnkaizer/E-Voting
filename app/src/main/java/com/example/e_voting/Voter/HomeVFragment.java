package com.example.e_voting.Voter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e_voting.Adapters.PartyAdapter;
import com.example.e_voting.Adapters.PollsAdapter;
import com.example.e_voting.Adapters.VoterEducationAdapter;
import com.example.e_voting.Models.PartiesModel;
import com.example.e_voting.Models.Polls;
import com.example.e_voting.Models.VoterEducation;
import com.example.e_voting.R;
import com.example.e_voting.databinding.FragmentHomeVBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeVFragment extends Fragment {
    private FragmentHomeVBinding binding;
    RecyclerView voterEdu;
    ArrayList<VoterEducation> voterEducations;
    VoterEducationAdapter voterEducationAdapter ;
    //Parties
    RecyclerView partyRec;
    ArrayList<PartiesModel> partiesModels;
    PartyAdapter partyAdapter;

    //Upcoming Polls
    RecyclerView pollsRec;
    ArrayList<Polls> polls;
    PollsAdapter pollsAdapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeVBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        voterEdu =root.findViewById(R.id.voterEduRec);
        partyRec =root.findViewById(R.id.partyRec);
        pollsRec =root.findViewById(R.id.upcomingRec);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Polls");
        polls = new ArrayList<>();
        pollsAdapter = new PollsAdapter(getActivity(), polls);
        pollsRec.setAdapter(pollsAdapter);
        pollsRec.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchPolls();

        voterEducations =new ArrayList<>();

        voterEducations.add(new VoterEducation(R.drawable.education,"Voter Education",R.string.voter_education));
        voterEducations.add(new VoterEducation(R.drawable.faq,"FAQs",R.string.faqs));
        voterEducations.add(new VoterEducation(R.drawable.information,"About",R.string.about));

        voterEducationAdapter = new VoterEducationAdapter(getActivity(), voterEducations, this);
        voterEdu.setAdapter(voterEducationAdapter);
        voterEdu.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        voterEdu.setHasFixedSize(true);
        voterEdu.setNestedScrollingEnabled(false);

        //Party
        partiesModels =new ArrayList<>();

        partiesModels.add(new PartiesModel(R.drawable.odm,"ODM",R.string.odm));
        partiesModels.add(new PartiesModel(R.drawable.uda,"UDA",R.string.uda));
        partiesModels.add(new PartiesModel(R.drawable.roots,"Roots Party",R.string.roots));
        partiesModels.add(new PartiesModel(R.drawable.jubilee,"Jubilee",R.string.jubilee));
        partiesModels.add(new PartiesModel(R.drawable.amaninat,"ANC",R.string.anc));
        partiesModels.add(new PartiesModel(R.drawable.kanu,"KANU",R.string.anc));
        partiesModels.add(new PartiesModel(R.drawable.wiper,"Wiper",R.string.wiper));

        partyAdapter = new PartyAdapter(getActivity(), partiesModels, this);
        partyRec.setAdapter(partyAdapter);
        partyRec.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        partyRec.setHasFixedSize(true);
        partyRec.setNestedScrollingEnabled(false);


        return root;
    }

    private void fetchPolls() {
        // Clear the existing list of polls
        polls.clear();

        // Add a listener to retrieve the polls from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Get the poll data from the snapshot
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String pTime = dataSnapshot.child("pTime").getValue(String.class);
                    String pDate = dataSnapshot.child("pDate").getValue(String.class);

                    // Create a new Polls object
                    Polls poll = new Polls(title, pTime, pDate);

                    // Add the poll to the list
                    polls.add(poll);
                }

                // Update the adapter with the new list of polls
                pollsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur during data retrieval
                Toast.makeText(getContext(), "Failed to fetch polls.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}