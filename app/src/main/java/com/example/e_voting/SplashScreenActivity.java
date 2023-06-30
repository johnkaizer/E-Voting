package com.example.e_voting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.e_voting.Auth.SignInActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 4000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Delayed task to move to the SignInActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the SignInActivity
                Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                startActivity(intent);
                finish(); // Optional: Close the SplashScreenActivity
            }
        }, SPLASH_DELAY);
    }
}