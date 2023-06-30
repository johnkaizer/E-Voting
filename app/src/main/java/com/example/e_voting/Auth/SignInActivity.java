package com.example.e_voting.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.e_voting.Admin.AdminActivity;
import com.example.e_voting.Models.User;
import com.example.e_voting.R;
import com.example.e_voting.Voter.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {
    AppCompatButton loginBtn;
    private EditText EditTextEmail,editTextPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        progressBar = findViewById(R.id.progressBar);
        EditTextEmail = findViewById(R.id.editText2);
        editTextPassword = findViewById(R.id.editText3);
        mAuth = FirebaseAuth.getInstance();
        loginBtn= findViewById(R.id.appCompatButton);
        preferences = getSharedPreferences("MyPreferences",MODE_PRIVATE);
        editor = preferences.edit();
        if (preferences.contains("saved_email")){
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }else {
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = EditTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();

                    if (email.isEmpty()) {
                        EditTextEmail.setError(" email is required!!");
                        EditTextEmail.requestFocus();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        EditTextEmail.setError("Please provide a valid email address!");
                        EditTextEmail.requestFocus();
                        return;
                    }
                    if (password.isEmpty()) {
                        editTextPassword.setError(" password is required!!");
                        editTextPassword.requestFocus();
                        return;
                    }
                    if (!isInternetConnected()) {
                        Toast.makeText(SignInActivity.this, "No internet connection available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Retrieve the user's role from Firebase database
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            User user = snapshot.getValue(User.class);
                                            if (user != null) {
                                                if (user.getRole().equals("admin")) {
                                                    // User is an admin
                                                    startActivity(new Intent(SignInActivity.this, AdminActivity.class));
                                                    Toast.makeText(SignInActivity.this, "Logged in as admin", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else if (user.getRole().equals("voter")) {
                                                    // User is a voter
                                                    redirectToAppropriateScreen();
                                                    Toast.makeText(SignInActivity.this, "Logged in as voter", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        } else {
                                            // User data does not exist in the database
                                            Toast.makeText(SignInActivity.this, "Failed to log in. User data not found", Toast.LENGTH_SHORT).show();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(SignInActivity.this, "Failed to log in. Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            } else {
                                Toast.makeText(SignInActivity.this, "Failed to log in. Check your credentials", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });
        }
    }

    // Method to redirect the user to the appropriate screen based on their role
    private void redirectToAppropriateScreen() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && !user.isAdditionalInfoRegistered()) {
                        // User is a voter and needs to register voter information
                        startActivity(new Intent(SignInActivity.this, VoterRegActivity.class));
                        Toast.makeText(SignInActivity.this, "Please register your voter information", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // User is a registered voter
                        editor.putString("saved_email", EditTextEmail.getText().toString().trim());
                        editor.putString("saved_pass", editTextPassword.getText().toString().trim());
                        editor.apply();
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        Toast.makeText(SignInActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    // User data does not exist in the database
                    Toast.makeText(SignInActivity.this, "Failed to log in. User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignInActivity.this, "Failed to log in. Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void sigup(View view) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        finish();
    }
}