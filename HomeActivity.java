package com.example.wedwise_java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Views
        welcomeText = findViewById(R.id.welcomeText);

        // Load and display user name
        loadUserName();

        // Initialize Bottom Navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        // Fix: Set the selected item dynamically
        MenuItem homeItem = bottomNavigation.getMenu().findItem(R.id.nav_home);
        if (homeItem != null) {
            homeItem.setChecked(true);
        }

        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true; // Already on Home, do nothing
                } else if (itemId == R.id.nav_budget) {
                    startActivity(new Intent(HomeActivity.this, BudgetActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_tasks) {
                    startActivity(new Intent(HomeActivity.this, TasksActivity.class));
                    finish();
                    return true;

                } else if (itemId == R.id.nav_suppliers) {
                    startActivity(new Intent(HomeActivity.this, SuppliersActivity.class));
                    finish();
                    return true;
                }else if (itemId == R.id.nav_venues) {
                    startActivity(new Intent(HomeActivity.this, VenuesActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

        // ✅ Click Listener for Budget Spent Section
        CardView budgetCard = findViewById(R.id.budgetCard);
        budgetCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        // ✅ Click Listener for Tasks Due Section
        CardView tasksCard = findViewById(R.id.tasksCard);
        tasksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TasksActivity.class);
                startActivity(intent);
            }
        });

        // ✅ Click Listener for Settings Icon
        ImageView settingsIcon = findViewById(R.id.settingsIcon);
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SettingsActivity
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // ✅ Click Listener for Profile Icon
        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // ✅ Click Listener for Message Icon
        ImageView messageIcon = findViewById(R.id.messageIcon);
        messageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ChatbotActivity
                Intent intent = new Intent(HomeActivity.this, ChatbotActivity.class);
                startActivity(intent);
            }
        });

        // ✅ Click Listener for Plus Button to open MenuBudgetActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MenuBudgetActivity.class);
                startActivity(intent);
            }
        });

        // Find the ImageView
        ImageView menuHomeImage = findViewById(R.id.menuhome);

        // Set a click listener
        menuHomeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MenuBudgetActivity
                Intent intent = new Intent(HomeActivity.this, MenuBudgetActivity.class);
                startActivity(intent);
            }
        });

        // ✅ Click listener for Budget Recommendation Card
        CardView budgetRecommendationCard = findViewById(R.id.budgetRecommendationCard);
        budgetRecommendationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BudgetRecommendationActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the welcome text when returning to this activity
        loadUserName();
    }

    private void loadUserName() {
        // Use the same SharedPreferences file and key as ProfileActivity
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = preferences.getString("profile_name", "");

        if (userName.isEmpty()) {
            welcomeText.setText("Welcome");
        } else {
            welcomeText.setText("Welcome " + userName + "!");
        }
    }
}