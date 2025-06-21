package com.example.wedwise_java;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SupplierDetailsActivity extends AppCompatActivity implements SupplierAdapter.OnDeleteClickListener {

    private ListView listViewSuppliers;
    private TextView emptyStateTextView;
    private ArrayList<Supplier> supplierList;
    private SupplierAdapter supplierAdapter;
    private FloatingActionButton fabAddSupplier;
    private String categoryName;
    private TextView toolbarTitle;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_details);

        // Get the category name from intent
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        // Find views
        listViewSuppliers = findViewById(R.id.listViewSuppliers);
        fabAddSupplier = findViewById(R.id.fabAddSupplier);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        btnBack = findViewById(R.id.btnBack);
        emptyStateTextView = findViewById(R.id.emptyStateText);

        // Set the toolbar title
        if (categoryName != null) {
            toolbarTitle.setText(categoryName);
        }

        // Set back button click listener
        btnBack.setOnClickListener(v -> onBackPressed());

        // Initialize supplier list from static storage
        supplierList = SupplierStorage.getSuppliers(categoryName);

        // Setup adapter
        supplierAdapter = new SupplierAdapter(this, supplierList, this);
        listViewSuppliers.setAdapter(supplierAdapter);

        // Update empty state visibility
        updateEmptyState();

        // Floating button to add new supplier
        fabAddSupplier.setOnClickListener(v -> {
            Intent intent = new Intent(SupplierDetailsActivity.this, NewSupplierActivity.class);
            intent.putExtra("CATEGORY_NAME", categoryName);
            startActivityForResult(intent, 1);
        });

        // List item click to open details
        listViewSuppliers.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Supplier selectedSupplier = supplierList.get(position);
            Intent intent = new Intent(SupplierDetailsActivity.this, ViewDetailsActivity.class);

            intent.putExtra("SUPPLIER_NAME", selectedSupplier.getName());
            intent.putExtra("SUPPLIER_NOTE", selectedSupplier.getNote());
            intent.putExtra("SUPPLIER_PHONE", selectedSupplier.getPhone());
            intent.putExtra("SUPPLIER_ADDRESS", selectedSupplier.getAddress());
            intent.putExtra("SUPPLIER_EMAIL", selectedSupplier.getEmail());
            intent.putExtra("SUPPLIER_BOOKED", selectedSupplier.isBooked());
            intent.putExtra("SUPPLIER_WEBSITE", selectedSupplier.getWebsite());

            startActivity(intent);
        });
    }

    private void updateEmptyState() {
        if (emptyStateTextView != null) {
            if (supplierList.isEmpty()) {
                emptyStateTextView.setVisibility(View.VISIBLE);
                listViewSuppliers.setVisibility(View.GONE);
            } else {
                emptyStateTextView.setVisibility(View.GONE);
                listViewSuppliers.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Get supplier details from intent
            String name = data.getStringExtra("SUPPLIER_NAME");
            String note = data.getStringExtra("SUPPLIER_NOTE");
            String phone = data.getStringExtra("SUPPLIER_PHONE");
            String address = data.getStringExtra("SUPPLIER_ADDRESS");
            String email = data.getStringExtra("SUPPLIER_EMAIL");
            boolean booked = data.getBooleanExtra("SUPPLIER_BOOKED", false);
            String website = data.getStringExtra("SUPPLIER_WEBSITE");

            // Debugging Log
            Log.d("SUPPLIER_ADDED", "Name: " + name + ", Phone: " + phone + ", Address: " + address);

            // Create new supplier and add to storage
            Supplier newSupplier = new Supplier(name, note, phone, address, email, booked, null, website);
            SupplierStorage.addSupplier(categoryName, newSupplier);

            // Notify adapter of change
            supplierAdapter.notifyDataSetChanged();

            // Update empty state
            updateEmptyState();
        }
    }

    @Override
    public void onDeleteClick(int position) {
        showDeleteConfirmationDialog(position);
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Supplier");
        builder.setMessage("Are you sure you want to delete this supplier?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            SupplierStorage.removeSupplier(categoryName, position);
            supplierAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
