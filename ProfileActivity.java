package com.example.wedwise_java;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImageView;
    private EditText nameEditText, genderEditText, emailEditText;
    private Button editProfileButton;
    private boolean isEditing = false;
    private Uri imageUri;
    private SharedPreferences sharedPreferences;
    private String sourceActivity; // To track where user came from

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Get source information from intent
        sourceActivity = getIntent().getStringExtra("source");

        // Initialize Views
        profileImageView = findViewById(R.id.profileImageView);
        nameEditText = findViewById(R.id.nameEditText);
        genderEditText = findViewById(R.id.genderEditText);
        emailEditText = findViewById(R.id.emailEditText);
        editProfileButton = findViewById(R.id.editProfileButton);

        // Load user data
        loadUserData();

        // Clicking Profile Picture to Upload Image
        profileImageView.setOnClickListener(v -> openGallery());

        // Toggle Editing Mode
        editProfileButton.setOnClickListener(v -> toggleEditing());

        // Gender selection click listener
        genderEditText.setOnClickListener(v -> {
            if (isEditing) {
                showGenderSelectionDialog();
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate based on source
            if ("settings".equals(sourceActivity)) {
                // If came from settings, go back to settings
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else {
                // Default behavior - go to home (for cases coming from home or unknown source)
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            finish();
        });
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            // Create a unique filename
            String filename = "profile_image_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), filename);

            // Copy the image to internal storage
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadUserData() {
        // Load email from SharedPreferences (auto-populated from login)
        String email = sharedPreferences.getString("email", "");
        emailEditText.setText(email);

        // Load saved profile data from SharedPreferences
        String savedName = sharedPreferences.getString("profile_name", "");
        String savedGender = sharedPreferences.getString("profile_gender", "");
        String savedImagePath = sharedPreferences.getString("profile_image_path", "");

        nameEditText.setText(savedName);
        genderEditText.setText(savedGender);

        // Load saved profile image from internal storage
        if (!savedImagePath.isEmpty()) {
            try {
                File imageFile = new File(savedImagePath);
                if (imageFile.exists()) {
                    Uri imageUri = Uri.fromFile(imageFile);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    profileImageView.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // If loading fails, keep default placeholder
            }
        }
    }

    private void saveUserData() {
        // Save profile data to SharedPreferences
        String name = nameEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("profile_name", name);
        editor.putString("profile_gender", gender);
        editor.apply();

        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void toggleEditing() {
        if (!isEditing) {
            // Enable editing mode
            nameEditText.setEnabled(true);
            nameEditText.setFocusableInTouchMode(true);
            genderEditText.setEnabled(true);
            genderEditText.setFocusableInTouchMode(true);
            genderEditText.setClickable(true); // Make gender field clickable

            // Email remains disabled (auto-populated from login)
            emailEditText.setEnabled(false);

            editProfileButton.setText("Save");
            isEditing = true;
        } else {
            // Disable editing and save changes
            nameEditText.setEnabled(false);
            genderEditText.setEnabled(false);
            genderEditText.setClickable(false); // Disable gender field clicking
            emailEditText.setEnabled(false);

            // Save the data
            saveUserData();

            editProfileButton.setText("Edit Profile");
            isEditing = false;
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();

            if (imageUri != null) {
                try {
                    // Save image to internal storage
                    String savedImagePath = saveImageToInternalStorage(imageUri);

                    if (savedImagePath != null) {
                        // Load and display the image
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        profileImageView.setImageBitmap(bitmap);

                        // Save image path to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("profile_image_path", savedImagePath);
                        editor.remove("profile_image_uri"); // Remove old URI key
                        editor.apply();

                        Toast.makeText(this, "Profile photo updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this activity
        loadUserData();
    }

    private void showGenderSelectionDialog() {
        String[] genderOptions = {"Male", "Female", "Prefer not to say"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender")
                .setItems(genderOptions, (dialog, which) -> {
                    // Update the gender field with selected option
                    genderEditText.setText(genderOptions[which]);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}