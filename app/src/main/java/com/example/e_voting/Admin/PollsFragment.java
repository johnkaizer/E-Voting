package com.example.e_voting.Admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.e_voting.Adapters.PollsAdapter;
import com.example.e_voting.DB.EVotingDB;
import com.example.e_voting.Models.CandidatesModel;
import com.example.e_voting.Models.Polls;
import com.example.e_voting.R;
import com.example.e_voting.databinding.FragmentPollsBinding;
import com.example.e_voting.databinding.FragmentVotesManagementBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class PollsFragment extends Fragment {
    private FragmentPollsBinding binding;
    RecyclerView pollsRec;
    ArrayList<Polls> polls;
    PollsAdapter pollsAdapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPollsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Polls");
        pollsRec =  root.findViewById(R.id.pollsRV);
        polls = new ArrayList<>();
        pollsAdapter = new PollsAdapter(getActivity(), polls);
        pollsRec.setAdapter(pollsAdapter);
        pollsRec.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchPolls();
        binding.addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.add_polls, null);
                EditText titleET = dialogView.findViewById(R.id.idEdtPollName);
                TextView date = dialogView.findViewById(R.id.textView12);
                TextView time = dialogView.findViewById(R.id.textView13);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.dateTimePickerBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get current date and time
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        // Create a date picker dialog
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        // The date has been selected
                                        // Save the selected date and show a time picker dialog
                                        final int selectedYear = year;
                                        final int selectedMonth = month;
                                        final int selectedDay = dayOfMonth;

                                        // Create a time picker dialog
                                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                                getContext(),
                                                new TimePickerDialog.OnTimeSetListener() {
                                                    @Override
                                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                        // The time has been selected
                                                        // Save the selected date and time
                                                        final int selectedHour = hourOfDay;
                                                        final int selectedMinute = minute;

                                                        // Format the date and time strings
                                                        String dateString = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                                                        String timeString = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);

                                                        // Update the date and time TextViews
                                                        date.setText(dateString);
                                                        time.setText(timeString);
                                                    }
                                                },
                                                hour,
                                                minute,
                                                false
                                        );

                                        timePickerDialog.show();
                                    }
                                },
                                year,
                                month,
                                day
                        );

                        datePickerDialog.show();
                    }
                });

                dialogView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the selected title, date, and time
                        String title = titleET.getText().toString();
                        String selectedDate = date.getText().toString();
                        String selectedTime = time.getText().toString();

                        // Create a new Polls object with the selected data
                        Polls poll = new Polls(title, selectedTime, selectedDate);

                        // Get the reference to the "Polls" node in the database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Polls");

                        // Generate a new unique key for the poll
                        String pollId = databaseReference.push().getKey();

                        // Save the poll to the database using the unique key
                        databaseReference.child(pollId).setValue(poll)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Poll saved successfully
                                        Toast.makeText(getContext(), "Poll saved successfully.", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to save the poll
                                        Toast.makeText(getContext(), "Failed to save poll.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });


                dialogView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }

                dialog.show();
            }
        });
        // Set long-press listener for item deletion
        pollsAdapter.setOnItemLongClickListener(new PollsAdapter.OnItemLongClickListener() {
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
        builder.setTitle("Delete Poll");
        builder.setMessage("Are you sure you want to delete this poll?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Polls polls1 = polls.get(position);
                String pollsId = polls1.getTitle();

                // Get a reference to the "Polls" node in the database
                DatabaseReference pollsRef = FirebaseDatabase.getInstance().getReference().child("Polls");

                // Remove the polls from the "Polls" node
                pollsRef.child(pollsId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Get a reference to the "Polls" node in the database
                        DatabaseReference pollRef = FirebaseDatabase.getInstance().getReference().child("Polls");

                        // Remove the candidate node from the category in "Votes"
                        pollRef.child(pollsId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Delete the polls from the list
                                polls.remove(position);

                                // Notify the adapter about the removal
                                pollsAdapter.notifyItemRemoved(position);

                                Toast.makeText(getContext(), "Poll deleted successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to delete poll from Polls.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to delete poll from Polls.", Toast.LENGTH_SHORT).show();
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