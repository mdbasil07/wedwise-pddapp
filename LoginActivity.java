package com.example.wedwise_java;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.VolleyError;

public class LoginActivity extends AppCompatActivity {

    public static String SESSION_COOKIE = null;
    private EditText editEmail, editPassword;
    private ImageView iconEye;
    private CheckBox rememberCheckBox;
    private SharedPreferences sharedPreferences;
    private boolean isPasswordVisible = false;

    private static final String LOGIN_URL = ApiConfig.BASE_URL + "login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Enable CookieManager
        CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        iconEye = findViewById(R.id.iconEye);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        TextView signUpText = findViewById(R.id.signUpText);

        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToHome();
            return;
        }

        loadSavedCredentials();

        iconEye.setOnClickListener(v -> togglePasswordVisibility());

        signUpText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        buttonLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                loginUser(email, password);
            }
        });
    }

    private void loadSavedCredentials() {
        if (sharedPreferences.getBoolean("remember_me", false)) {
            editEmail.setText(sharedPreferences.getString("saved_email", ""));
            editPassword.setText(sharedPreferences.getString("saved_password", ""));
            rememberCheckBox.setChecked(true);
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            iconEye.setImageResource(R.drawable.ic_eye_closed);
        } else {
            editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            iconEye.setImageResource(R.drawable.ic_eye);
        }
        editPassword.setSelection(editPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loginUser(String email, String password) {
        RequestQueue queue = Volley.newRequestQueue(this, new HurlStack());

        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> handleLoginResponse(response, email, password),
                this::handleLoginError) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(com.android.volley.NetworkResponse response) {
                // Extract and store session ID from response headers
                Map<String, String> headers = response.headers;
                String setCookie = headers.get("Set-Cookie");
                if (setCookie != null && setCookie.contains("PHPSESSID")) {
                    Log.d("SESSION_COOKIE", SESSION_COOKIE);
                    SESSION_COOKIE = setCookie.split(";")[0];
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                if (SESSION_COOKIE != null) {
                    headers.put("Cookie", SESSION_COOKIE);
                }
                return headers;
            }

        };

        queue.add(request);
    }

    private void handleLoginResponse(String response, String email, String password) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");

            if ("success".equals(status)) {
                JSONObject userObject = jsonResponse.getJSONObject("user");
                int userId = userObject.getInt("id");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putInt("user_id", userId);
                editor.putString("email", email);

                if (rememberCheckBox.isChecked()) {
                    editor.putBoolean("remember_me", true);
                    editor.putString("saved_email", email);
                    editor.putString("saved_password", password);
                } else {
                    editor.putBoolean("remember_me", false);
                    editor.remove("saved_email");
                    editor.remove("saved_password");
                }

                editor.apply();

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                navigateToHome();
            } else {
                Toast.makeText(this, jsonResponse.optString("message", "Login failed!"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Response error. Please try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void handleLoginError(VolleyError error) {
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            switch (statusCode) {
                case 404:
                    Toast.makeText(this, "User not found. Check your email.", Toast.LENGTH_SHORT).show();
                    break;
                case 401:
                    Toast.makeText(this, "Invalid password. Try again.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Network error. Check your connection.", Toast.LENGTH_SHORT).show();
        }
        error.printStackTrace();
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}