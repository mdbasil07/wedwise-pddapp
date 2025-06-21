package com.example.wedwise_java;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SuppliersActivity extends AppCompatActivity {

    private FloatingActionButton fabAddCategory;
    private LinearLayout dynamicSuppliersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);

        // Initialize Bottom Navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.getMenu().findItem(R.id.nav_suppliers).setChecked(true);

        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(SuppliersActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_budget) {
                    startActivity(new Intent(SuppliersActivity.this, BudgetActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_tasks) {
                    startActivity(new Intent(SuppliersActivity.this, TasksActivity.class));
                    finish();
                    return true;
                }else if (itemId == R.id.nav_venues) {
                    startActivity(new Intent(SuppliersActivity.this, VenuesActivity.class));
                    finish();
                    return true;
                }
                else if (itemId == R.id.nav_suppliers) {
                    return true;
                }
                return false;
            }
        });

        fabAddCategory = findViewById(R.id.fabAdd);
        dynamicSuppliersContainer = findViewById(R.id.dynamicSuppliersContainer);

        setupPredefinedCategories();         // Setup predefined cards
        loadSuppliersFromStaticList();       // Load previously added dynamic supplier cards

        fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void loadSuppliersFromStaticList() {
        for (Supplier supplier : Supplier.supplierList) {
            addNewSupplierCard(supplier.getName());
        }
    }

    private void setupPredefinedCategories() {
        // Venues
        CardView venuesCard = findViewById(R.id.venuesCard);
        ImageButton btnDeleteVenue = venuesCard.findViewById(R.id.btnDeleteVenue);
        btnDeleteVenue.setOnClickListener(v -> {
            ((LinearLayout) venuesCard.getParent()).removeView(venuesCard);
        });
        venuesCard.setOnClickListener(v -> {
            Intent intent = new Intent(SuppliersActivity.this, SupplierDetailsActivity.class);
            intent.putExtra("CATEGORY_NAME", "Venues");
            startActivity(intent);
        });

        // Photographers
        CardView photographersCard = findViewById(R.id.photographersCard);
        ImageButton btnDeletePhotographers = photographersCard.findViewById(R.id.btnDeletePhotographers);
        btnDeletePhotographers.setOnClickListener(v -> {
            ((LinearLayout) photographersCard.getParent()).removeView(photographersCard);
        });
        photographersCard.setOnClickListener(v -> {
            Intent intent = new Intent(SuppliersActivity.this, SupplierDetailsActivity.class);
            intent.putExtra("CATEGORY_NAME", "Photographers");
            startActivity(intent);
        });

        // Caterers
        CardView caterersCard = findViewById(R.id.caterersCard);
        ImageButton btnDeleteCaterers = caterersCard.findViewById(R.id.btnDeleteSupplier);
        btnDeleteCaterers.setOnClickListener(v -> {
            ((LinearLayout) caterersCard.getParent()).removeView(caterersCard);
        });
        caterersCard.setOnClickListener(v -> {
            Intent intent = new Intent(SuppliersActivity.this, SupplierDetailsActivity.class);
            intent.putExtra("CATEGORY_NAME", "Caterers");
            startActivity(intent);
        });
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New Supplier Category");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newCategoryName = input.getText().toString().trim();
            if (!newCategoryName.isEmpty()) {
                addNewSupplierCard(newCategoryName);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addNewSupplierCard(String categoryName) {
        Supplier newSupplier = new Supplier(categoryName, "", "", "", "", false, null, "");
        Supplier.supplierList.add(newSupplier);

        View supplierCardView = LayoutInflater.from(this).inflate(
                R.layout.item_supplier_category, dynamicSuppliersContainer, false);

        TextView tvCategoryName = supplierCardView.findViewById(R.id.tvCategoryName);
        TextView tvCategoryStats = supplierCardView.findViewById(R.id.tvCategoryStats);
        ImageButton btnDelete = supplierCardView.findViewById(R.id.btnDeleteSupplier);

        tvCategoryName.setText(categoryName);
        tvCategoryStats.setText(categoryName + ": 0 | booked: 0");

        supplierCardView.setOnClickListener(v -> {
            Intent intent = new Intent(SuppliersActivity.this, SupplierDetailsActivity.class);
            intent.putExtra("CATEGORY_NAME", categoryName);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            dynamicSuppliersContainer.removeView(supplierCardView);
            for (int i = 0; i < Supplier.supplierList.size(); i++) {
                if (Supplier.supplierList.get(i).getName().equals(categoryName)) {
                    Supplier.supplierList.remove(i);
                    break;
                }
            }
        });

        dynamicSuppliersContainer.addView(supplierCardView);
    }
}