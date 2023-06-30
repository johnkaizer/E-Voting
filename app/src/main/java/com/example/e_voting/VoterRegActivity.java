package com.example.e_voting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.e_voting.Models.VotersModel;
import com.example.e_voting.Voter.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class VoterRegActivity extends AppCompatActivity {
    AppCompatButton uploadImage, saveBtn;
    EditText nameET, lnameET, IdET;
    Spinner spinner;
    ImageView imageV;
    //upload image
    public static final int REQUEST_CODE_IMAGE=101;
    Uri imageUri;
    boolean isImageAdded= false;
    DatabaseReference dataRef;
    //progressbar
    ProgressBar progressBar;
    StorageReference storageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_reg);
        dataRef= FirebaseDatabase.getInstance().getReference().child("Voters");
        storageRef= FirebaseStorage.getInstance().getReference().child("VotersImages");
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //Button and EditText and imageView
        uploadImage = findViewById(R.id.appCompatButton2);
        saveBtn = findViewById(R.id.appCompatButton3);
        nameET = findViewById(R.id.first_name);
        lnameET = findViewById(R.id.sec_name);
        IdET = findViewById(R.id.id_no);
        spinner = findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this ,R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        imageV = findViewById(R.id.company_image);
        progressBar= findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE_IMAGE);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userUid = Constants.getCurrentUserID();
                final String fName = nameET.getText().toString();
                final String sName = lnameET.getText().toString();
                final String Identity = IdET.getText().toString();
                final String gender = spinner.getSelectedItem().toString();

                if (isImageAdded!=false && fName != null && sName != null && Identity != null && gender != null ) {
                    saveVoter(userUid, fName, sName, Identity, gender);
                }
            }
        });
    }
    private void saveVoter(String userUid, String fName, String sName, String Identity, String gender) {
        progressBar.setVisibility(View.VISIBLE);
        if (imageUri == null) {
            // Display an error message or show a Toast indicating that an image should be selected
            Toast.makeText(VoterRegActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        final String key = dataRef.push().getKey();
        storageRef.child(key + ".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Create the SubscriptionModel instance
                        VotersModel votersModel = new VotersModel();
                        votersModel.setUid(userUid);

                        // Save the SubscriptionModel to the database
                        DatabaseReference subscriptionRef = FirebaseDatabase.getInstance().getReference().child("Voters");
                        subscriptionRef.child(userUid).setValue(votersModel);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("UId", userUid);
                        hashMap.put("fName", fName);
                        hashMap.put("sName", sName);
                        hashMap.put("idNumber", Identity);
                        hashMap.put("gender", gender);
                        hashMap.put("ImageUrl", uri.toString());

                        dataRef.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(VoterRegActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                nameET.setText("");
                                lnameET.setText("");
                                IdET.setText("");
                                imageV.setImageResource(R.drawable.image_24);
                                progressBar.setVisibility(View.GONE);

                                // Create an AlertDialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(VoterRegActivity.this);
                                builder.setTitle("Voter Registration")
                                        .setMessage("Your details have been submitted successfully. Click OK to proceed.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Move to the main activity
                                                startActivity(new Intent(VoterRegActivity.this, MainActivity.class));
                                                finish();

                                                // Update the registered field in the User model
                                                userRef.child(userUid).child("additionalInfoRegistered").setValue(true);
                                            }
                                        })
                                        .setCancelable(false) // Prevent dialog dismissal by tapping outside
                                        .show();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = snapshot.getBytesTransferred() * 100 / snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            }
        });
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