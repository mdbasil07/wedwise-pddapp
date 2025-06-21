package com.example.wedwise_java;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ViewDetailsActivity extends AppCompatActivity {

    // UI Elements
    private ImageButton btnBack;
    private TextView toolbarTitle;
    private TextView supplierLetter;
    private TextView supplierName;
    private TextView bookedBadge;
    private EditText noteTextView;
    private EditText addressTextView;
    private EditText phoneTextView;
    private EditText emailTextView;
    private EditText websiteTextView;
    private CardView noteCardView;
    private CardView addressCardView;
    private CardView phoneCardView;
    private CardView emailCardView;
    private CardView websiteCardView;

    private Button editButton;
    private Button saveButton;

    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        // Initialize UI elements
        initializeViews();

        // Set up back button
        setupBackButton();

        // Load and populate data
        loadSupplierDetails();

        // Edit/Save logic
        editButton.setOnClickListener(v -> {
            enableEditing(true);
            Toast.makeText(this, "Edit mode enabled", Toast.LENGTH_SHORT).show();
        });

        saveButton.setOnClickListener(v -> {
            saveSupplierDetails();
            enableEditing(false);
            Toast.makeText(this, "Details saved successfully", Toast.LENGTH_SHORT).show();
        });

        // Initially disable editing
        enableEditing(false);
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        supplierLetter = findViewById(R.id.venueLetter);
        supplierName = findViewById(R.id.venueName);
        bookedBadge = findViewById(R.id.bookedBadge);

        // EditText fields
        noteTextView = findViewById(R.id.noteTextView);
        addressTextView = findViewById(R.id.addressTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        emailTextView = findViewById(R.id.emailTextView);
        websiteTextView = findViewById(R.id.websiteTextView);

        // CardViews
        noteCardView = findViewById(R.id.noteCardView);
        addressCardView = findViewById(R.id.addressCardView);
        phoneCardView = findViewById(R.id.phoneCardView);
        emailCardView = findViewById(R.id.emailCardView);
        websiteCardView = findViewById(R.id.websiteCardView);

        // Buttons
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void loadSupplierDetails() {
        Intent intent = getIntent();

        // Fill SupplierDataHolder if empty
        if (SupplierDataHolder.name == null || SupplierDataHolder.name.isEmpty()) {
            SupplierDataHolder.name = intent.getStringExtra("SUPPLIER_NAME");
            SupplierDataHolder.note = intent.getStringExtra("SUPPLIER_NOTE");
            SupplierDataHolder.address = intent.getStringExtra("SUPPLIER_ADDRESS");
            SupplierDataHolder.phone = intent.getStringExtra("SUPPLIER_PHONE");
            SupplierDataHolder.email = intent.getStringExtra("SUPPLIER_EMAIL");
            SupplierDataHolder.website = intent.getStringExtra("SUPPLIER_WEBSITE");
            SupplierDataHolder.isBooked = intent.getBooleanExtra("SUPPLIER_BOOKED", false);
        }

        // Set default values if null
        if (SupplierDataHolder.name == null) SupplierDataHolder.name = "";
        if (SupplierDataHolder.note == null) SupplierDataHolder.note = "";
        if (SupplierDataHolder.address == null) SupplierDataHolder.address = "";
        if (SupplierDataHolder.phone == null) SupplierDataHolder.phone = "";
        if (SupplierDataHolder.email == null) SupplierDataHolder.email = "";
        if (SupplierDataHolder.website == null) SupplierDataHolder.website = "";

        toolbarTitle.setText(SupplierDataHolder.name);
        supplierLetter.setText(SupplierDataHolder.name != null && !SupplierDataHolder.name.isEmpty() ?
                String.valueOf(SupplierDataHolder.name.charAt(0)).toUpperCase() : "");
        supplierName.setText(SupplierDataHolder.name);
        bookedBadge.setVisibility(SupplierDataHolder.isBooked ? View.VISIBLE : View.GONE);

        noteTextView.setText(SupplierDataHolder.note);
        addressTextView.setText(SupplierDataHolder.address);
        phoneTextView.setText(SupplierDataHolder.phone);
        emailTextView.setText(SupplierDataHolder.email);
        websiteTextView.setText(SupplierDataHolder.website);

        setupContactListeners();
    }

    private void enableEditing(boolean enabled) {
        isEditMode = enabled;

        // Enable/disable EditText fields
        noteTextView.setEnabled(enabled);
        noteTextView.setFocusable(enabled);
        noteTextView.setFocusableInTouchMode(enabled);
        noteTextView.setCursorVisible(enabled);

        addressTextView.setEnabled(enabled);
        addressTextView.setFocusable(enabled);
        addressTextView.setFocusableInTouchMode(enabled);
        addressTextView.setCursorVisible(enabled);

        phoneTextView.setEnabled(enabled);
        phoneTextView.setFocusable(enabled);
        phoneTextView.setFocusableInTouchMode(enabled);
        phoneTextView.setCursorVisible(enabled);

        emailTextView.setEnabled(enabled);
        emailTextView.setFocusable(enabled);
        emailTextView.setFocusableInTouchMode(enabled);
        emailTextView.setCursorVisible(enabled);

        websiteTextView.setEnabled(enabled);
        websiteTextView.setFocusable(enabled);
        websiteTextView.setFocusableInTouchMode(enabled);
        websiteTextView.setCursorVisible(enabled);

        // Show/hide buttons
        editButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
        saveButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(enabled);

        // Change background color to indicate edit mode
        int backgroundColor = enabled ? 0xFFFFFFFF : 0xFFE0E0E0; // White when editing, gray when not
        noteCardView.setCardBackgroundColor(backgroundColor);
        addressCardView.setCardBackgroundColor(backgroundColor);
        phoneCardView.setCardBackgroundColor(backgroundColor);
        emailCardView.setCardBackgroundColor(backgroundColor);
        websiteCardView.setCardBackgroundColor(backgroundColor);

        // Remove click listeners when in edit mode, add them back when not
        if (enabled) {
            phoneTextView.setOnClickListener(null);
            emailTextView.setOnClickListener(null);
            websiteTextView.setOnClickListener(null);
        } else {
            setupContactListeners();
        }
    }

    private void saveSupplierDetails() {
        // Get text from EditText fields and save to SupplierDataHolder
        SupplierDataHolder.note = noteTextView.getText().toString().trim();
        SupplierDataHolder.address = addressTextView.getText().toString().trim();
        SupplierDataHolder.phone = phoneTextView.getText().toString().trim();
        SupplierDataHolder.email = emailTextView.getText().toString().trim();
        SupplierDataHolder.website = websiteTextView.getText().toString().trim();

        // Here you could also save to database or shared preferences if needed
        // For example:
        // saveToDatabase();
        // or
        // saveToSharedPreferences();
    }

    private void setupContactListeners() {
        if (!isEditMode) {
            phoneTextView.setOnClickListener(v -> {
                String phone = SupplierDataHolder.phone;
                if (phone != null && !phone.trim().isEmpty()) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse("tel:" + phone.trim()));
                    startActivity(phoneIntent);
                }
            });

            emailTextView.setOnClickListener(v -> {
                String email = SupplierDataHolder.email;
                if (email != null && !email.trim().isEmpty()) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + email.trim()));
                    startActivity(emailIntent);
                }
            });

            websiteTextView.setOnClickListener(v -> {
                String website = SupplierDataHolder.website;
                if (website != null && !website.trim().isEmpty()) {
                    String url = website.startsWith("http://") || website.startsWith("https://")
                            ? website
                            : "http://" + website;

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
        }
    }
}