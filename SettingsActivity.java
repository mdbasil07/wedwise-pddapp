package com.example.wedwise_java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("UserSession", MODE_PRIVATE);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                navigateTo(HomeActivity.class);
                return true;
            } else if (itemId == R.id.nav_budget) {
                navigateTo(BudgetActivity.class);
                return true;
            } else if (itemId == R.id.nav_tasks) {
                navigateTo(TasksActivity.class);
                return true;

            } else if (itemId == R.id.nav_suppliers) {
                navigateTo(SuppliersActivity.class);
                return true;

            } else if (itemId == R.id.nav_venues) {
                navigateTo(SuppliersActivity.class);
                return true;
            }
            return false;
        });

        View account = findViewById(R.id.accountItem);
        View support = findViewById(R.id.supportItem);
        View termsOfService = findViewById(R.id.termsItem);
        View logout = findViewById(R.id.logoutItem);
        View backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(view -> navigateTo(HomeActivity.class));

        // Modified account click listener to pass source information
        account.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            intent.putExtra("source", "settings"); // Pass source information
            startActivity(intent);
        });

        support.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, SupportActivity.class);
            startActivity(intent);
        });

        termsOfService.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, TermsOfServiceActivity.class);
            startActivity(intent);
        });

        logout.setOnClickListener(view -> {
            showToast("Logging out...");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void navigateTo(Class<?> targetActivity) {
        startActivity(new Intent(SettingsActivity.this, targetActivity));
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}