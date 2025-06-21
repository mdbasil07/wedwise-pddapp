package com.example.wedwise_java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword, editConfirmPassword;
    private ImageView passwordVisibility;
    private boolean isPasswordVisible = false;
    private static final String SIGNUP_URL = ApiConfig.BASE_URL + "signup.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Views
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        passwordVisibility = findViewById(R.id.passwordVisibility);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        CheckBox checkboxRemember = findViewById(R.id.checkboxRemember);
        TextView textLogin = findViewById(R.id.textLogin);

        // Navigate to LoginActivity
        textLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Toggle Password Visibility
        passwordVisibility.setOnClickListener(view -> togglePasswordVisibility());

        // Sign Up Button Click Logic
        buttonSignUp.setOnClickListener(view -> {
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String confirmPassword = editConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Call signup method using Volley
                signupUser(name, email, password, confirmPassword);
            }
        });
    }

    // Toggle Password Visibility
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordVisibility.setImageResource(R.drawable.ic_eye_closed);
        } else {
            editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordVisibility.setImageResource(R.drawable.ic_eye);
        }
        editPassword.setSelection(editPassword.getText().length());
        editConfirmPassword.setSelection(editConfirmPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    // Signup User Using Volley
    private void signupUser(String name, String email, String password, String confirmPassword) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SIGNUP_URL,
                response -> handleSignupResponse(response, email),
                error -> {
                    Toast.makeText(SignupActivity.this, "Network Error! Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e("SignupVolleyError", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("confirm_password", confirmPassword);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    // Handle API Response
    private void handleSignupResponse(String response, String email) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");
            String message = jsonResponse.getString("message");

            if (status.equals("success")) {
                // Save account creation state
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("hasAccount", true);
                editor.putBoolean("isLoggedIn", true);

                // Extract user id from response and save it
                JSONObject user = jsonResponse.getJSONObject("user");
                int userId = user.getInt("id");
                editor.putInt("user_id", userId);

                editor.apply();

                // Redirect to OTP verification or HomeActivity
                Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, OtpSigninActivity.class);
                intent.putExtra("source", "signup");
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Toast.makeText(SignupActivity.this, "Response Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("SignupError", e.getMessage());
        }
    }

}
