package com.example.wedwise_java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        // Set title
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText("Support");

        // Back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish(); // Go back to previous screen
        });

        // Start Chat button - connect to ChatbotActivity
        Button startChatButton = findViewById(R.id.startChatButton);
        startChatButton.setOnClickListener(v -> {
            Intent intent = new Intent(SupportActivity.this, ChatbotActivity.class);
            startActivity(intent);
        });
    }
}