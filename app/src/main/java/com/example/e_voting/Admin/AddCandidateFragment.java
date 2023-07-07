package com.example.e_voting.Admin;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_voting.DB.EVotingDB;
import com.example.e_voting.Models.CandidatesModel;
import com.example.e_voting.R;
import com.example.e_voting.databinding.FragmentAddCandidateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;

public class AddCandidateFragment extends Fragment {
    private FragmentAddCandidateBinding binding;
    EditText fullNameET, partyET;
    Spinner cat;
    ImageView imageV;
    AppCompatButton submitBtn, uploadImageBtn;
    public static final int REQUEST_CODE_IMAGE = 101;
    Uri imageUri;
    boolean isImageAdded = false;
    EVotingDB eVotingDB;
    StorageReference storageRef;
    DatabaseReference dataRef;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddCandidateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        eVotingDB = new EVotingDB(getContext());
        fullNameET = root.findViewById(R.id.fullname);
        partyET = root.findViewById(R.id.party);
        imageV = root.findViewById(R.id.candidate_image);
        submitBtn = root.findViewById(R.id.submit_btn);
        uploadImageBtn = root.findViewById(R.id.image_btn);
        cat = root.findViewById(R.id.category_spinner);
        progressBar = root.findViewById(R.id.progressbar1);
        progressBar.setVisibility(View.GONE);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cat.setAdapter(spinnerAdapter);
        // Fetch categories from SQLite table and add them to the spinner adapter
        List<String> categories = eVotingDB.getCategories();
        spinnerAdapter.addAll(categories);
        spinnerAdapter.notifyDataSetChanged();
        dataRef = FirebaseDatabase.getInstance().getReference().child("Candidates");
        storageRef = FirebaseStorage.getInstance().getReference().child("CandidatesImages");
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameET.getText().toString();
                String party = partyET.getText().toString();
                String category = cat.getSelectedItem().toString();

                if (isImageAdded && fullName != null && party != null && category != null) {
                    saveCandidate(fullName, party, category);
                } else {
                    Toast.makeText(getContext(), "Please fill all fields and add an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private void saveCandidate(String fullName, String party, String category) {
        progressBar.setVisibility(View.VISIBLE);

        final String candidateId = dataRef.child(category).push().getKey();
        StorageReference imageRef = storageRef.child(category).child(candidateId + ".jpg");
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();

                        CandidatesModel candidate = new CandidatesModel(imageUrl, fullName, party, category);

                        dataRef.child(category).child(candidateId).setValue(candidate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Candidate saved successfully", Toast.LENGTH_SHORT).show();
                                resetForm();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to save candidate", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to upload candidate image", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            }
        });
    }


    private void resetForm() {
        fullNameET.setText("");
        partyET.setText("");
        imageV.setImageResource(R.drawable.pic);
        isImageAdded = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && data != null) {
            imageUri = data.getData();
            isImageAdded = true;
            imageV.setImageURI(imageUri);
        }
    }
}

