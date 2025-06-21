package com.example.wedwise_java;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class NewSupplierActivity extends AppCompatActivity {

    private EditText editTextName, editTextNote, editTextPhone, editTextAddress, editTextEmail, editTextWebsite;
    private SwitchCompat switchBooked;
    private Button btnSave;
    private ImageButton btnBack;

    private int supplierIndex = -1; // -1 means adding new, otherwise editing existing
    private String categoryName; // Category name for the supplier
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_supplier);

        // Find all views
        editTextName = findViewById(R.id.editTextName);
        editTextNote = findViewById(R.id.editTextNote);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextWebsite = findViewById(R.id.websiteEditText);
        switchBooked = findViewById(R.id.my_switch);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // Get data from intent
        supplierIndex = getIntent().getIntExtra("SUPPLIER_INDEX", -1);
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        isEditMode = supplierIndex != -1;

        if (isEditMode) {
            // This is edit mode, pre-fill the fields
            loadSupplierData();
            // Change button text and title for edit mode
            btnSave.setText("Update Supplier");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Supplier");
            }
        } else {
            // Add mode
            btnSave.setText("Save Supplier");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add New Supplier");
            }
        }

        // Set back button click listener
        btnBack.setOnClickListener(v -> onBackPressed());

        // Save/Update button logic
        btnSave.setOnClickListener(v -> saveOrUpdateSupplier());
    }

    private void loadSupplierData() {
        // Load supplier data from intent extras
        String name = getIntent().getStringExtra("SUPPLIER_NAME");
        String note = getIntent().getStringExtra("SUPPLIER_NOTE");
        String phone = getIntent().getStringExtra("SUPPLIER_PHONE");
        String address = getIntent().getStringExtra("SUPPLIER_ADDRESS");
        String email = getIntent().getStringExtra("SUPPLIER_EMAIL");
        String website = getIntent().getStringExtra("SUPPLIER_WEBSITE");
        boolean booked = getIntent().getBooleanExtra("SUPPLIER_BOOKED", false);

        // Pre-fill the form fields
        if (name != null) editTextName.setText(name);
        if (note != null) editTextNote.setText(note);
        if (phone != null) editTextPhone.setText(phone);
        if (address != null) editTextAddress.setText(address);
        if (email != null) editTextEmail.setText(email);
        if (website != null) editTextWebsite.setText(website);
        switchBooked.setChecked(booked);
    }

    private void saveOrUpdateSupplier() {
        // Get input values
        String supplierName = editTextName.getText().toString().trim();
        String supplierNote = editTextNote.getText().toString().trim();
        String supplierPhone = editTextPhone.getText().toString().trim();
        String supplierAddress = editTextAddress.getText().toString().trim();
        String supplierEmail = editTextEmail.getText().toString().trim();
        String supplierWebsite = editTextWebsite.getText().toString().trim();
        boolean isBooked = switchBooked.isChecked();

        // Validate required fields
        if (supplierName.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }

        if (supplierNote.isEmpty()) {
            editTextNote.setError("Note is required");
            editTextNote.requestFocus();
            return;
        }

        // Create intent with result data
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SUPPLIER_NAME", supplierName);
        resultIntent.putExtra("SUPPLIER_NOTE", supplierNote);
        resultIntent.putExtra("SUPPLIER_PHONE", supplierPhone);
        resultIntent.putExtra("SUPPLIER_ADDRESS", supplierAddress);
        resultIntent.putExtra("SUPPLIER_EMAIL", supplierEmail);
        resultIntent.putExtra("SUPPLIER_WEBSITE", supplierWebsite);
        resultIntent.putExtra("SUPPLIER_BOOKED", isBooked);
        resultIntent.putExtra("SUPPLIER_INDEX", supplierIndex);
        resultIntent.putExtra("CATEGORY_NAME", categoryName);
        resultIntent.putExtra("IS_EDIT_MODE", isEditMode);

        // Show success message
        String message = isEditMode ? "Supplier updated successfully!" : "Supplier added successfully!";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}