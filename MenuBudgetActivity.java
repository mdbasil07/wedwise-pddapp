package com.example.wedwise_java;

import android.os.Bundle;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MenuBudgetActivity extends AppCompatActivity {

    // Static variables for temporary data storage
    private static int staticTotalBudget = 0;
    private static int staticRemainingBudget = 0;
    private static List<BudgetItem> staticBudgetItems = new ArrayList<>();

    // Inner class to represent budget items
    public static class BudgetItem {
        public String itemName;
        public int itemCost;
        public boolean isSelected;

        public BudgetItem(String name, int cost, boolean selected) {
            this.itemName = name;
            this.itemCost = cost;
            this.isSelected = selected;
        }
    }

    private EditText etTotalBudget;
    private EditText etAddItem;
    private EditText etAddCost;
    private TextView tvTotalBudget, tvRemainingBudget;
    private LinearLayout itemsContainer;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabDelete;

    private int totalBudget = 0;
    private int remainingBudget = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_budget);

        // Initialize UI elements
        etTotalBudget = findViewById(R.id.et_total_budget);
        etAddItem = findViewById(R.id.et_add_item_new);
        etAddCost = findViewById(R.id.et_add_cost);
        tvTotalBudget = findViewById(R.id.tv_total_budget);
        tvRemainingBudget = findViewById(R.id.tv_remaining_budget);
        itemsContainer = findViewById(R.id.items_container);
        fabAdd = findViewById(R.id.fabAdd);
        fabDelete = findViewById(R.id.fabDelete);

        // Load saved data if available
        loadStaticData();

        // Listen for total budget input changes
        etTotalBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.toString().isEmpty()) {
                        totalBudget = Integer.parseInt(s.toString());
                        remainingBudget = totalBudget;
                        tvTotalBudget.setText("₹ " + totalBudget);
                        tvRemainingBudget.setText("₹ " + remainingBudget);

                        // Save to static variables
                        saveTotalBudget(totalBudget);
                    } else {
                        totalBudget = 0;
                        remainingBudget = 0;
                        tvTotalBudget.setText("₹ 0");
                        tvRemainingBudget.setText("₹ 0");

                        // Save to static variables
                        saveTotalBudget(0);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MenuBudgetActivity.this, "Invalid budget input!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Floating Action Button to add new items
        fabAdd.setOnClickListener(v -> addNewItem());

        // Floating Action Button to delete checked items
        fabDelete.setOnClickListener(v -> deleteCheckedItems());

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuBudgetActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // optional: closes the current activity
            }
        });
    }

    // Static methods for data management
    public static void saveTotalBudget(int budget) {
        staticTotalBudget = budget;
        staticRemainingBudget = budget;
        // Reset items when budget changes
        staticBudgetItems.clear();
    }

    public static int getTotalBudget() {
        return staticTotalBudget;
    }

    public static int getRemainingBudget() {
        return staticRemainingBudget;
    }

    public static void setRemainingBudget(int budget) {
        staticRemainingBudget = budget;
    }

    public static List<BudgetItem> getBudgetItems() {
        return new ArrayList<>(staticBudgetItems);
    }

    public static void addBudgetItem(String name, int cost, boolean selected) {
        staticBudgetItems.add(new BudgetItem(name, cost, selected));
    }

    public static void removeBudgetItem(String name) {
        staticBudgetItems.removeIf(item -> item.itemName.equals(name));
    }

    public static void updateItemSelection(String name, boolean selected) {
        for (BudgetItem item : staticBudgetItems) {
            if (item.itemName.equals(name)) {
                item.isSelected = selected;
                break;
            }
        }
    }

    public static void clearAllData() {
        staticTotalBudget = 0;
        staticRemainingBudget = 0;
        staticBudgetItems.clear();
    }

    public static int getTotalSelectedCost() {
        int total = 0;
        for (BudgetItem item : staticBudgetItems) {
            if (item.isSelected) {
                total += item.itemCost;
            }
        }
        return total;
    }

    // Load static data into UI
    private void loadStaticData() {
        if (staticTotalBudget > 0) {
            totalBudget = staticTotalBudget;
            remainingBudget = staticRemainingBudget;

            etTotalBudget.setText(String.valueOf(totalBudget));
            tvTotalBudget.setText("₹ " + totalBudget);
            tvRemainingBudget.setText("₹ " + remainingBudget);

            // Restore budget items
            for (BudgetItem item : staticBudgetItems) {
                recreateItemView(item);
            }
        } else {
            // Set initial values
            tvTotalBudget.setText("₹ 0");
            tvRemainingBudget.setText("₹ 0");
        }
    }

    // Recreate item view from saved data
    private void recreateItemView(BudgetItem item) {
        View newItemView = getLayoutInflater().inflate(R.layout.item_budget, null);

        CheckBox checkBox = newItemView.findViewById(R.id.checkbox_item);
        TextView itemNameTV = newItemView.findViewById(R.id.tv_item_name);
        TextView costTV = newItemView.findViewById(R.id.tv_item_cost);

        itemNameTV.setText(item.itemName);
        costTV.setText("₹" + item.itemCost);
        checkBox.setChecked(item.isSelected);

        itemsContainer.addView(newItemView);

        // Handle checkbox selection
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (remainingBudget - item.itemCost < 0) {
                    checkBox.setChecked(false);
                    Toast.makeText(this, "Insufficient budget", Toast.LENGTH_SHORT).show();
                } else {
                    remainingBudget -= item.itemCost;
                    tvRemainingBudget.setText("₹ " + remainingBudget);
                    updateItemSelection(item.itemName, true);
                    setRemainingBudget(remainingBudget);
                }
            } else {
                remainingBudget += item.itemCost;
                tvRemainingBudget.setText("₹ " + remainingBudget);
                updateItemSelection(item.itemName, false);
                setRemainingBudget(remainingBudget);
            }
        });
    }

    private void addNewItem() {
        if (totalBudget == 0) {
            Toast.makeText(this, "Please set total budget first", Toast.LENGTH_SHORT).show();
            return;
        }

        String itemName = etAddItem.getText().toString().trim();
        String costStr = etAddCost.getText().toString().trim();

        if (itemName.isEmpty() || costStr.isEmpty()) {
            Toast.makeText(this, "Please enter item name and cost", Toast.LENGTH_SHORT).show();
            return;
        }

        int itemCost;
        try {
            itemCost = Integer.parseInt(costStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid cost input", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for duplicate item names
        for (BudgetItem existingItem : staticBudgetItems) {
            if (existingItem.itemName.equalsIgnoreCase(itemName)) {
                Toast.makeText(this, "Item already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Add to static storage
        addBudgetItem(itemName, itemCost, false);

        // Inflate the predefined layout
        View newItemView = getLayoutInflater().inflate(R.layout.item_budget, null);

        // Get references to the views inside the layout
        CheckBox checkBox = newItemView.findViewById(R.id.checkbox_item);
        TextView itemNameTV = newItemView.findViewById(R.id.tv_item_name);
        TextView costTV = newItemView.findViewById(R.id.tv_item_cost);

        // Set values
        itemNameTV.setText(itemName);
        costTV.setText("₹" + itemCost);

        // Add to container
        itemsContainer.addView(newItemView);

        // Handle checkbox selection
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (remainingBudget - itemCost < 0) {
                    checkBox.setChecked(false);
                    Toast.makeText(this, "Insufficient budget", Toast.LENGTH_SHORT).show();
                } else {
                    remainingBudget -= itemCost;
                    tvRemainingBudget.setText("₹ " + remainingBudget);
                    updateItemSelection(itemName, true);
                    setRemainingBudget(remainingBudget);
                }
            } else {
                remainingBudget += itemCost;
                tvRemainingBudget.setText("₹ " + remainingBudget);
                updateItemSelection(itemName, false);
                setRemainingBudget(remainingBudget);
            }
        });

        // Clear input fields
        etAddItem.setText("");
        etAddCost.setText("");
    }

    private void deleteCheckedItems() {
        boolean isAnyItemDeleted = false;
        List<String> itemsToRemove = new ArrayList<>();

        // **Handle Dynamic Items Safely**
        for (int i = itemsContainer.getChildCount() - 1; i >= 0; i--) {
            View view = itemsContainer.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout itemLayout = (LinearLayout) view;
                CheckBox checkBox = (CheckBox) itemLayout.getChildAt(0);

                if (checkBox != null && checkBox.isChecked()) {
                    TextView itemNameTV = (TextView) itemLayout.getChildAt(1);
                    TextView costTV = (TextView) itemLayout.getChildAt(2);

                    if (costTV != null && itemNameTV != null) {
                        String itemName = itemNameTV.getText().toString();
                        String costStr = costTV.getText().toString().replace("₹", "").trim();

                        try {
                            int itemCost = Integer.parseInt(costStr);
                            remainingBudget += itemCost;
                            itemsToRemove.add(itemName);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    // **Remove the item safely**
                    itemsContainer.removeView(itemLayout);
                    isAnyItemDeleted = true;
                }
            }
        }

        // Remove items from static storage
        for (String itemName : itemsToRemove) {
            removeBudgetItem(itemName);
        }

        // **Update Remaining Budget**
        tvRemainingBudget.setText("₹ " + remainingBudget);
        setRemainingBudget(remainingBudget);

        // **If No Items Left, Reset Budget**
        if (itemsContainer.getChildCount() == 0) {
            remainingBudget = totalBudget;
            tvRemainingBudget.setText("₹ " + remainingBudget);
            setRemainingBudget(remainingBudget);
            Toast.makeText(this, "All items deleted. List is empty!", Toast.LENGTH_SHORT).show();
        }

        // **Provide User Feedback**
        if (isAnyItemDeleted) {
            Toast.makeText(this, "Selected items deleted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No items selected for deletion", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Data is automatically saved in static variables
        // No need for additional saving here since we update static variables in real-time
    }
}