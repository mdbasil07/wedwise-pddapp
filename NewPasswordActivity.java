package com.example.wedwise_java;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class NewPasswordActivity extends AppCompatActivity {

    private TextInputEditText inputNewPassword, inputConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        // Initialize input fields and button
        inputNewPassword = findViewById(R.id.inputNewPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        Button buttonUpdatePassword = findViewById(R.id.buttonUpdatePassword);

        // Handle Update Password Click
        buttonUpdatePassword.setOnClickListener(view -> {
            String newPassword = inputNewPassword.getText().toString().trim();
            String confirmPassword = inputConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(NewPasswordActivity.this, "Fill both fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(NewPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NewPasswordActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();

                // Navigate to CongratulationsActivity
                Intent intent = new Intent(NewPasswordActivity.this, CongratulationsActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }
}
