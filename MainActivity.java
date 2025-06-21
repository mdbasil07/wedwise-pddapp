package com.example.wedwise_java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences userSession = getSharedPreferences("UserSession", MODE_PRIVATE);

        boolean isFirstTime = appPrefs.getBoolean("isFirstTime", true);
        boolean isLoggedIn = userSession.getBoolean("isLoggedIn", false);

        // If user is already logged in, go directly to home
        if (isLoggedIn) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
            return;
        }

        // If not first time but not logged in, go to login
        if (!isFirstTime) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // First time user - show onboarding
        btnGetStarted = findViewById(R.id.buttonGetStarted);
        btnGetStarted.setOnClickListener(v -> {
            SharedPreferences.Editor editor = appPrefs.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            startActivity(new Intent(MainActivity.this, SignupActivity.class));
            finish();
        });
    }
}