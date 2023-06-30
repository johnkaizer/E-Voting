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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class AddCandidateFragment extends Fragment {
    private FragmentAddCandidateBinding binding;
    EditText fullNameET, partyET;
    Spinner cat;
    ImageView imageV, partyLogo;
    AppCompatButton submitBtn, uploadImageBtn, uploadLogoBtn;
    public static final int REQUEST_CODE_IMAGE = 101;
    public static final int REQUEST_CODE_PARTY_LOGO = 102;

    Uri imageUri;
    Uri partyLogoUri;

    boolean isImageAdded = false;
    EVotingDB eVotingDB;
    StorageReference storageRef;
    DatabaseReference dataRef;

    // Declare clickedImageView as an ImageView
    ImageView clickedImageView;

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
        partyLogo = root.findViewById(R.id.party_logo);
        submitBtn = root.findViewById(R.id.submit_btn);
        uploadImageBtn = root.findViewById(R.id.image_btn);
        uploadLogoBtn = root.findViewById(R.id.button2);
        cat = root.findViewById(R.id.category_spinner);
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
                // Set clickedImageView to imageV
                clickedImageView = imageV;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        uploadLogoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set clickedImageView to partyLogo
                clickedImageView = partyLogo;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_PARTY_LOGO);
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameET.getText().toString();
                String party = partyET.getText().toString();
                String category = cat.getSelectedItem().toString();

                if (isImageAdded && !fullName.isEmpty() && !party.isEmpty()) {
                    // Generate a unique ID for the candidate
                    String candidateId = dataRef.push().getKey();

                    // Upload the image to Firebase Storage
                    uploadImage(candidateId);

                    // Create a new CandidatesModel object with the candidate details
                    CandidatesModel candidate = new CandidatesModel();
                    candidate.setImage(candidateId);
                    candidate.setFullName(fullName);
                    candidate.setParty(party);
                    candidate.setPartyLogo(candidateId);
                    candidate.setCategory(category);

                    // Save the candidate to Firebase Realtime Database
                    saveCandidate(candidate);

                    // Reset the form
                    resetForm();
                } else {
                    Toast.makeText(getContext(), "Please fill all fields and add an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private void uploadImage(String candidateId) {
        if (imageUri != null && partyLogoUri != null) {
            // Upload candidate image
            StorageReference candidateImageRef = storageRef.child(candidateId + "_image");
            candidateImageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Candidate image upload success

                            // Upload party logo image
                            StorageReference partyLogoRef = storageRef.child(candidateId + "_partyLogo");
                            partyLogoRef.putFile(partyLogoUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Party logo image upload success
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Party logo image upload failed
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Candidate image upload failed
                        }
                    });
        }
    }


    private void saveCandidate(CandidatesModel candidate) {
        dataRef.child(candidate.getImage()).setValue(candidate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Candidate saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to save candidate", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetForm() {
        fullNameET.setText("");
        partyET.setText("");
        imageV.setImageResource(R.drawable.pic);
        partyLogo.setImageResource(R.drawable.pic);
        isImageAdded = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE && data != null) {
                imageUri = data.getData();
                isImageAdded = true;
                // Check which ImageView was clicked
                if (clickedImageView == imageV) {
                    // Set the image to imageV
                    imageV.setImageURI(imageUri);
                } else if (clickedImageView == partyLogo) {
                    // Set the image to partyLogo
                    partyLogo.setImageURI(partyLogoUri);
                }
            } else if (requestCode == REQUEST_CODE_PARTY_LOGO && data != null) {
                partyLogoUri = data.getData();
                // Set the image to partyLogo ImageView
                partyLogo.setImageURI(partyLogoUri);
            }
        }
    }
}

