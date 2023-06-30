package com.example.e_voting.Voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.e_voting.Admin.AdminActivity;
import com.example.e_voting.Auth.SignInActivity;
import com.example.e_voting.Models.User;
import com.example.e_voting.R;
import com.example.e_voting.Voter.HomeVFragment;
import com.example.e_voting.Voter.ResultsFragment;
import com.example.e_voting.Voter.VoteFragment;
import com.example.e_voting.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = preferences.edit();
        replaceFragment(new HomeVFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeVFragment());
            } else if (item.getItemId() == R.id.vote) {
                replaceFragment(new VoteFragment());
            } else if (item.getItemId() == R.id.results) {
                replaceFragment(new ResultsFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_account) {
            // Get the currently logged-in user
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            // Check if the user is logged in
            if (currentUser != null) {
                // Retrieve the user's ID or any other identifier
                String userId = currentUser.getUid();

                // Get a reference to the user's data in the Firebase Realtime Database
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                // Read the user's data from the database
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Check if the user data exists
                        if (dataSnapshot.exists()) {
                            // Get the user object from the snapshot
                            User user = dataSnapshot.getValue(User.class);

                            // Create and show a dialog to display the user details
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("User Details");
                            builder.setMessage("Name: " + user.getFullName() + "\nEmail: " + user.getEmail() + "\nPhone: " + user.getPhone() + "\nPassword: " + user.getPassword()+ "\nRole: " + user.getRole());
                            builder.setPositiveButton("OK", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error cases
                        Log.e("AdminActivity", "Failed to read user data: " + databaseError.getMessage());
                    }
                });
            }

            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}