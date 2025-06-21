package com.example.wedwise_java;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetActivity extends AppCompatActivity {

    private TextView tvTotalBudget, tvRemainingBudget, tvSpentBudget;
    private EditText etTotalBudget;
    private LinearLayout categoriesContainer;
    private Button btnNewSection;
    private PieChart budgetPieChart;
    private BottomNavigationView bottomNavigation;
    private final List<View> dynamicCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Bottom Navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.getMenu().findItem(R.id.nav_budget).setChecked(true);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(BudgetActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_tasks) {
                startActivity(new Intent(BudgetActivity.this, TasksActivity.class));
                finish();
                return true;
            }else if (itemId == R.id.nav_venues) {
                startActivity(new Intent(BudgetActivity.this, VenuesActivity.class));
                finish();
                return true;
            }
            return false;
        });

        initializeViews();
        setupInitialCategories();

        // Load saved total budget from SharedPreferences (only totalBudget)
        SharedPreferences prefs = getSharedPreferences("WedWisePrefs", MODE_PRIVATE);
        BudgetData.totalBudget = prefs.getInt("totalBudget", 0);

        // Set the EditText to show the saved total budget
        if (BudgetData.totalBudget != 0) {
            etTotalBudget.setText(String.valueOf(BudgetData.totalBudget));
        }

        // Calculate remaining budget from static variables
        BudgetData.remainingBudget = BudgetData.totalBudget - BudgetData.spentBudget;
        updateBudgetDisplay();
        setupPieChart();

        etTotalBudget.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                handleTotalBudgetChange(s.toString());
            }
        });

        btnNewSection.setOnClickListener(v -> showNewCategoryDialog());
    }


    private void initializeViews() {
        tvTotalBudget = findViewById(R.id.tv_general_total_budget);
        tvRemainingBudget = findViewById(R.id.tv_general_remaining_budget);
        tvSpentBudget = findViewById(R.id.tv_general_spent_budget);
        etTotalBudget = findViewById(R.id.et_general_total_budget);
        categoriesContainer = findViewById(R.id.categoriesContainer);
        btnNewSection = findViewById(R.id.btnNewSection);
        budgetPieChart = findViewById(R.id.budgetPieChart);
    }

    private void handleTotalBudgetChange(String input) {
        try {
            BudgetData.totalBudget = input.isEmpty() ? 0 : Integer.parseInt(input);
        } catch (NumberFormatException e) {
            BudgetData.totalBudget = 0;
        }

        // Recalculate remaining budget
        BudgetData.remainingBudget = BudgetData.totalBudget - BudgetData.spentBudget;
        updateBudgetDisplay();

        // Save only total budget to SharedPreferences
        getSharedPreferences("WedWisePrefs", MODE_PRIVATE)
                .edit()
                .putInt("totalBudget", BudgetData.totalBudget)
                .apply();
    }



    private void setupInitialCategories() {
        if (BudgetData.categories.isEmpty()) {
            // Add default categories only once
            String[] initialCategories = {"Catering", "Rent", "Electricity", "Photography"};
            for (String categoryName : initialCategories) {
                addCategory(categoryName);
            }
        } else {
            // Rebuild UI from static data
            for (BudgetData.BudgetCategory category : BudgetData.categories) {
                View categoryView = createDynamicCategoryView(category.name);
                categoriesContainer.addView(categoryView);
                dynamicCategories.add(categoryView);

                LinearLayout itemsContainer = categoryView.findViewById(R.id.itemsContainer);
                itemsContainer.removeAllViews();
                for (BudgetData.BudgetItem item : category.items) {
                    addItemToCategoryView(itemsContainer, category.name, item.name, item.amount);
                }
            }
        }
    }


    private void addCategory(String categoryName) {
        BudgetData.BudgetCategory newCategory = new BudgetData.BudgetCategory(categoryName);
        newCategory.items.add(new BudgetData.BudgetItem(categoryName + " Item", 0));
        BudgetData.categories.add(newCategory);

        View newCategoryView = createDynamicCategoryView(categoryName);
        categoriesContainer.addView(newCategoryView);
        dynamicCategories.add(newCategoryView);
    }


    private View createDynamicCategoryView(String categoryName) {
        View categoryView = LayoutInflater.from(this)
                .inflate(R.layout.budget_category_item, categoriesContainer, false);

        TextView titleView = categoryView.findViewById(R.id.title_category);
        titleView.setText(categoryName);
        LinearLayout itemsContainer = categoryView.findViewById(R.id.itemsContainer);
        Button btnAddItem = categoryView.findViewById(R.id.btn_add_item);

        // Updated button click listener to properly add new items to both UI and data
        btnAddItem.setOnClickListener(v -> {
            // Find the category in BudgetData
            BudgetData.BudgetCategory category = null;
            for (BudgetData.BudgetCategory cat : BudgetData.categories) {
                if (cat.name.equals(categoryName)) {
                    category = cat;
                    break;
                }
            }

            if (category != null) {
                // Add new item to data structure
                String newItemName = categoryName + " Item " + (category.items.size() + 1);
                BudgetData.BudgetItem newItem = new BudgetData.BudgetItem(newItemName, 0);
                category.items.add(newItem);

                // Add new item to UI
                addItemToCategoryView(itemsContainer, categoryName, newItemName, 0);
            }
        });

        // Add initial item
        addItemToCategoryView(itemsContainer, categoryName, categoryName + " Item", 0);

        return categoryView;
    }

    private void addItemToCategoryView(LinearLayout itemsContainer, String categoryName, String itemName, int amount) {
        // Inflate a single budget item view from XML
        View itemView = LayoutInflater.from(this)
                .inflate(R.layout.budget_item, itemsContainer, false);

        // Reference the EditText for item name and set its text
        EditText etItemName = itemView.findViewById(R.id.et_item_name);
        etItemName.setText(itemName);

        // Reference the EditText for item budget and set its text
        EditText etItemBudget = itemView.findViewById(R.id.et_item_budget);
        etItemBudget.setText(String.valueOf(amount));

        // Add a TextWatcher to handle real-time updates when budget amount changes
        etItemBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Update the budget totals when user changes the amount
                updateCategoryBudgets();
            }
        });

        // Add TextWatcher to item name to update data structure
        etItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Update item name in data structure
                updateItemNames();
            }
        });

        // Finally, add the inflated item view to the container
        itemsContainer.addView(itemView);
    }

    private void updateItemNames() {
        // Update item names in the data structure based on current UI state
        for (int i = 0; i < BudgetData.categories.size() && i < dynamicCategories.size(); i++) {
            BudgetData.BudgetCategory category = BudgetData.categories.get(i);
            View categoryView = dynamicCategories.get(i);
            LinearLayout itemsContainer = categoryView.findViewById(R.id.itemsContainer);

            for (int j = 0; j < itemsContainer.getChildCount() && j < category.items.size(); j++) {
                EditText etItemName = itemsContainer.getChildAt(j).findViewById(R.id.et_item_name);
                category.items.get(j).name = etItemName.getText().toString();
            }
        }
    }

    private void updateCategoryBudgets() {
        BudgetData.spentBudget = 0;
        for (int i = 0; i < BudgetData.categories.size() && i < dynamicCategories.size(); i++) {
            BudgetData.BudgetCategory category = BudgetData.categories.get(i);
            View categoryView = dynamicCategories.get(i);
            LinearLayout itemsContainer = categoryView.findViewById(R.id.itemsContainer);

            for (int j = 0; j < itemsContainer.getChildCount() && j < category.items.size(); j++) {
                EditText etItemBudget = itemsContainer.getChildAt(j).findViewById(R.id.et_item_budget);
                try {
                    int amount = Integer.parseInt(etItemBudget.getText().toString());
                    BudgetData.spentBudget += amount;
                    category.items.get(j).amount = amount;
                } catch (NumberFormatException e) {
                    // If parsing fails, set amount to 0
                    category.items.get(j).amount = 0;
                }
            }
        }

        // Update remaining budget using static variables
        BudgetData.remainingBudget = BudgetData.totalBudget - BudgetData.spentBudget;
        updateBudgetDisplay();
    }

    private void updateBudgetDisplay() {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        tvTotalBudget.setText(currencyFormatter.format(BudgetData.totalBudget));
        tvRemainingBudget.setText(currencyFormatter.format(BudgetData.remainingBudget));
        tvSpentBudget.setText(currencyFormatter.format(BudgetData.spentBudget));
        setupPieChart();
    }

    private void showNewCategoryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_new_category);

        EditText etCategoryName = dialog.findViewById(R.id.etCategoryName);
        dialog.findViewById(R.id.btnAddCategory).setOnClickListener(v -> {
            String categoryName = etCategoryName.getText().toString().trim();
            if (categoryName.isEmpty()) {
                etCategoryName.setError("Category name is required");
                return;
            }
            addCategory(categoryName);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupPieChart() {
        budgetPieChart.clear();

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int[] colorPalette = {
                ContextCompat.getColor(this, android.R.color.holo_blue_bright),
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                ContextCompat.getColor(this, android.R.color.holo_orange_light),
                ContextCompat.getColor(this, android.R.color.holo_purple),
                ContextCompat.getColor(this, android.R.color.holo_red_light),
                ContextCompat.getColor(this, android.R.color.holo_blue_dark),
                ContextCompat.getColor(this, android.R.color.holo_green_dark)
        };

        for (View categoryView : dynamicCategories) {
            TextView titleView = categoryView.findViewById(R.id.title_category);
            String categoryName = titleView.getText().toString();
            LinearLayout itemsContainer = categoryView.findViewById(R.id.itemsContainer);
            int categoryTotal = 0;

            for (int j = 0; j < itemsContainer.getChildCount(); j++) {
                EditText etItemBudget = itemsContainer.getChildAt(j).findViewById(R.id.et_item_budget);
                try {
                    categoryTotal += Integer.parseInt(etItemBudget.getText().toString());
                } catch (NumberFormatException ignored) {}
            }

            if (categoryTotal > 0) {
                entries.add(new PieEntry(categoryTotal, categoryName));
                colors.add(colorPalette[entries.size() % colorPalette.length]);
            }
        }

        if (entries.isEmpty() && BudgetData.totalBudget > 0) {
            entries.add(new PieEntry(BudgetData.totalBudget, "Total Budget"));
            colors.add(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(budgetPieChart));
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);

        budgetPieChart.setData(pieData);
        budgetPieChart.setHoleRadius(50f);
        budgetPieChart.setTransparentCircleRadius(60f);
        budgetPieChart.getDescription().setEnabled(false);
        budgetPieChart.setDrawEntryLabels(true);
        budgetPieChart.setEntryLabelColor(Color.BLACK);
        budgetPieChart.setEntryLabelTextSize(10f);

        Legend legend = budgetPieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        budgetPieChart.invalidate();
    }
}