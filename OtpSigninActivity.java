package com.example.wedwise_java;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OtpSigninActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4;
    private String email, source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_signin);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        Button buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp);
        TextView textResendOtp = findViewById(R.id.textResendOtp);

        // Retrieve intent extras
        source = getIntent().getStringExtra("source");
        email = getIntent().getStringExtra("email");

        // Auto Move to Next OTP Field
        setupOtpAutoMove();

        // Resend OTP Logic
        textResendOtp.setOnClickListener(view -> {
            Toast.makeText(OtpSigninActivity.this, "OTP resent to " + email, Toast.LENGTH_SHORT).show();
            // TODO: Implement actual OTP resend logic
        });

        // Verify OTP Button Click
        buttonVerifyOtp.setOnClickListener(view -> {
            String otp = otp1.getText().toString().trim() +
                    otp2.getText().toString().trim() +
                    otp3.getText().toString().trim() +
                    otp4.getText().toString().trim();

            if (otp.length() < 4) {
                Toast.makeText(OtpSigninActivity.this, "Enter complete OTP", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(OtpSigninActivity.this, "OTP Verified!", Toast.LENGTH_SHORT).show();
                if ("signup".equals(source)) {
                    Intent intent = new Intent(OtpSigninActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else if ("forgot_password".equals(source)) {
                    Intent intent = new Intent(OtpSigninActivity.this, NewPasswordActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    private void setupOtpAutoMove() {
        EditText[] otpFields = {otp1, otp2, otp3, otp4};

        for (int i = 0; i < otpFields.length; i++) {
            int nextIndex = i + 1;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && nextIndex < otpFields.length) {
                        otpFields[nextIndex].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
}