package com.example.wedwise_java;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editEmail = findViewById(R.id.editEmail);
        Button buttonSend = findViewById(R.id.buttonSend);  // ❌ Removed extra "});" after this line

        // Handle Send Button Click
        buttonSend.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to OTP Sign-in page
                Intent intent = new Intent(ForgotPasswordActivity.this, OtpSigninActivity.class);
                intent.putExtra("source", "forgot_password");  // Pass source as forgot_password
                intent.putExtra("email", email); // Pass email to OTP page
                startActivity(intent);
            }
        });
    }
}
