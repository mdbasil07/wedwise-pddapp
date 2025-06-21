package com.example.wedwise_java;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetRecommendationActivity extends AppCompatActivity {

    // UI Elements
    EditText budgetInput;
    TextView totalBudgetDisplay, totalSpentDisplay, remainingBudgetDisplay;

    // Budget allocation controls (EditText instead of SeekBar)
    EditText venuePercentageInput, cateringPercentageInput, photoPercentageInput,
            decorPercentageInput, otherPercentageInput;
    TextView venueBudgetAmount, cateringBudgetAmount, photoBudgetAmount,
            decorBudgetAmount, otherBudgetAmount;

    // Vendor lists containers
    LinearLayout venueContainer, cateringContainer, photoContainer, decorContainer, otherContainer;

    androidx.cardview.widget.CardView selectedItemsCard;
    LinearLayout selectedItemsContainer;

    // Budget tracking
    private double totalBudget = 0;
    private double totalSpent = 0;
    private int venuePercent = 35, cateringPercent = 25, photoPercent = 15, decorPercent = 10, otherPercent = 15;

    // Selected vendors tracking
    private List<Vendor> selectedVendors = new ArrayList<>();

    // Vendor class
    static class Vendor {
        String name;
        double price;
        String contact;
        float rating;
        String description;
        String category;
        boolean isSelected;

        Vendor(String name, double price, String contact, float rating, String description, String category) {
            this.name = name;
            this.price = price;
            this.contact = contact;
            this.rating = rating;
            this.description = description;
            this.category = category;
            this.isSelected = false;
        }
    }

    // Static vendor data
    static ArrayList<Vendor> venues = new ArrayList<>();
    static ArrayList<Vendor> caterers = new ArrayList<>();
    static ArrayList<Vendor> photographers = new ArrayList<>();
    static ArrayList<Vendor> decors = new ArrayList<>();
    static ArrayList<Vendor> others = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_recommendation);

        initializeViews();
        loadVendors();
        setupListeners();
        updateBudgetDisplay();
    }

    private void initializeViews() {
        budgetInput = findViewById(R.id.budgetInput);
        totalBudgetDisplay = findViewById(R.id.totalBudgetDisplay);
        totalSpentDisplay = findViewById(R.id.totalSpentDisplay);
        remainingBudgetDisplay = findViewById(R.id.remainingBudgetDisplay);

        // Percentage input fields
        venuePercentageInput = findViewById(R.id.venuePercentageInput);
        cateringPercentageInput = findViewById(R.id.cateringPercentageInput);
        photoPercentageInput = findViewById(R.id.photoPercentageInput);
        decorPercentageInput = findViewById(R.id.decorPercentageInput);
        otherPercentageInput = findViewById(R.id.otherPercentageInput);

        // Budget amount displays
        venueBudgetAmount = findViewById(R.id.venueBudgetAmount);
        cateringBudgetAmount = findViewById(R.id.cateringBudgetAmount);
        photoBudgetAmount = findViewById(R.id.photoBudgetAmount);
        decorBudgetAmount = findViewById(R.id.decorBudgetAmount);
        otherBudgetAmount = findViewById(R.id.otherBudgetAmount);

        // Vendor containers
        venueContainer = findViewById(R.id.venueContainer);
        cateringContainer = findViewById(R.id.cateringContainer);
        photoContainer = findViewById(R.id.photoContainer);
        decorContainer = findViewById(R.id.decorContainer);
        otherContainer = findViewById(R.id.otherContainer);

        // Add these missing initializations:
        selectedItemsCard = findViewById(R.id.selectedItemsCard);
        selectedItemsContainer = findViewById(R.id.selectedItemsContainer);
    }

    private void loadVendors() {
        // Venues
        venues.add(new Vendor("Dream Palace Hall", 150000, "9876543210", 4.5f, "Premium banquet hall with AC, parking for 200 guests", "venue"));
        venues.add(new Vendor("Royal Banquet", 180000, "9876512340", 4.7f, "Luxury venue with garden, capacity 300 guests", "venue"));
        venues.add(new Vendor("Budget Inn", 100000, "9876501234", 4.2f, "Affordable hall with basic amenities, 150 guests", "venue"));
        venues.add(new Vendor("Grand Celebration", 220000, "9876501235", 4.8f, "5-star venue with full service, 400 guests", "venue"));
        venues.add(new Vendor("City Plaza", 120000, "9876501236", 4.3f, "Central location, modern facilities, 200 guests", "venue"));

        // Caterers
        caterers.add(new Vendor("Tasty Treats", 100000, "9876001234", 4.6f, "Multi-cuisine catering, 150 guests", "catering"));
        caterers.add(new Vendor("Foodie Hub", 125000, "9876012345", 4.4f, "North Indian speciality, 200 guests", "catering"));
        caterers.add(new Vendor("Royal Kitchen", 80000, "9876012346", 4.5f, "Traditional recipes, 120 guests", "catering"));
        caterers.add(new Vendor("Spice Route", 150000, "9876012347", 4.7f, "Premium buffet service, 250 guests", "catering"));
        caterers.add(new Vendor("Home Style", 60000, "9876012348", 4.2f, "Home-cooked meals, 100 guests", "catering"));

        // Photographers
        photographers.add(new Vendor("SnapShot Studio", 70000, "9876023456", 4.8f, "Wedding photography + videography package", "photography"));
        photographers.add(new Vendor("PhotoPro", 75000, "9876034567", 4.5f, "Professional wedding shoots with albums", "photography"));
        photographers.add(new Vendor("Candid Moments", 90000, "9876034568", 4.9f, "Candid + traditional photography", "photography"));
        photographers.add(new Vendor("Frame Perfect", 50000, "9876034569", 4.3f, "Basic photography package", "photography"));
        photographers.add(new Vendor("Luxury Lens", 120000, "9876034570", 4.7f, "Premium photography with drone shots", "photography"));

        // Decorators
        decors.add(new Vendor("Floral Magic", 40000, "9876045678", 4.3f, "Fresh flower decorations and mandap", "decor"));
        decors.add(new Vendor("Elegant Events", 60000, "9876056789", 4.6f, "Premium decorations with lighting", "decor"));
        decors.add(new Vendor("Creative Designs", 35000, "9876056790", 4.1f, "Budget-friendly decor solutions", "decor"));
        decors.add(new Vendor("Royal Decorators", 80000, "9876056791", 4.8f, "Luxury theme decorations", "decor"));
        decors.add(new Vendor("Simple Elegance", 25000, "9876056792", 4.0f, "Minimalist wedding decor", "decor"));

        // Others (Music, DJ, etc.)
        others.add(new Vendor("Beat Masters DJ", 30000, "9876067890", 4.5f, "Professional DJ with sound system", "other"));
        others.add(new Vendor("Musical Nights", 25000, "9876078901", 4.3f, "Live band + DJ combo", "other"));
        others.add(new Vendor("Sound & Light Pro", 40000, "9876078902", 4.7f, "Complete audio-visual setup", "other"));
        others.add(new Vendor("Party Vibes", 20000, "9876078903", 4.2f, "Basic DJ and music setup", "other"));
        others.add(new Vendor("Event Entertainment", 50000, "9876078904", 4.8f, "Premium entertainment package", "other"));
    }

    private void setupListeners() {
        // Budget input listener
        budgetInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String budgetText = s.toString().trim();
                if (!budgetText.isEmpty()) {
                    try {
                        totalBudget = Double.parseDouble(budgetText);
                        updateBudgetDisplay();
                        updateSelectedItemsDisplay();
                        updateSelectedItemsDisplay();
                        updateVendorRecommendations();
                    } catch (NumberFormatException e) {
                        totalBudget = 0;
                        updateBudgetDisplay();
                    }
                } else {
                    totalBudget = 0;
                    updateBudgetDisplay();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Percentage input listeners
        setupPercentageListener(venuePercentageInput, "venue");
        setupPercentageListener(cateringPercentageInput, "catering");
        setupPercentageListener(photoPercentageInput, "photo");
        setupPercentageListener(decorPercentageInput, "decor");
        setupPercentageListener(otherPercentageInput, "other");
    }

    private void setupPercentageListener(EditText editText, String category) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String percentText = s.toString().trim();
                if (!percentText.isEmpty()) {
                    try {
                        int percent = Integer.parseInt(percentText);
                        if (percent >= 0 && percent <= 100) {
                            updateCategoryPercentage(category, percent);
                            updateBudgetDisplay();
                            updateVendorRecommendations();
                        }
                    } catch (NumberFormatException e) {
                        // Invalid input, ignore
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateCategoryPercentage(String category, int percent) {
        switch (category) {
            case "venue":
                venuePercent = percent;
                break;
            case "catering":
                cateringPercent = percent;
                break;
            case "photo":
                photoPercent = percent;
                break;
            case "decor":
                decorPercent = percent;
                break;
            case "other":
                otherPercent = percent;
                break;
        }
    }

    private void updateBudgetDisplay() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        totalBudgetDisplay.setText("Total Budget: " + formatter.format(totalBudget));
        totalSpentDisplay.setText("Total Spent: " + formatter.format(totalSpent));
        remainingBudgetDisplay.setText("Remaining: " + formatter.format(totalBudget - totalSpent));

        // Update category budget amounts
        double venueBudget = (totalBudget * venuePercent) / 100;
        double cateringBudget = (totalBudget * cateringPercent) / 100;
        double photoBudget = (totalBudget * photoPercent) / 100;
        double decorBudget = (totalBudget * decorPercent) / 100;
        double otherBudget = (totalBudget * otherPercent) / 100;

        venueBudgetAmount.setText(formatter.format(venueBudget));
        cateringBudgetAmount.setText(formatter.format(cateringBudget));
        photoBudgetAmount.setText(formatter.format(photoBudget));
        decorBudgetAmount.setText(formatter.format(decorBudget));
        otherBudgetAmount.setText(formatter.format(otherBudget));

        // Check if percentages total 100%
        int totalPercent = venuePercent + cateringPercent + photoPercent + decorPercent + otherPercent;
        if (totalPercent != 100) {
            Toast.makeText(this, "Warning: Budget allocation should total 100% (Currently: " + totalPercent + "%)", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateVendorRecommendations() {
        if (totalBudget <= 0) return;

        double venueBudget = (totalBudget * venuePercent) / 100;
        double cateringBudget = (totalBudget * cateringPercent) / 100;
        double photoBudget = (totalBudget * photoPercent) / 100;
        double decorBudget = (totalBudget * decorPercent) / 100;
        double otherBudget = (totalBudget * otherPercent) / 100;

        populateVendorCards(venueContainer, venues, venueBudget);
        populateVendorCards(cateringContainer, caterers, cateringBudget);
        populateVendorCards(photoContainer, photographers, photoBudget);
        populateVendorCards(decorContainer, decors, decorBudget);
        populateVendorCards(otherContainer, others, otherBudget);
    }

    private void populateVendorCards(LinearLayout container, ArrayList<Vendor> vendorList, double budget) {
        container.removeAllViews();

        for (Vendor vendor : vendorList) {
            if (vendor.price <= budget * 1.2) { // Show vendors within 120% of budget
                CardView vendorCard = createVendorCard(vendor, budget);
                container.addView(vendorCard);
            }
        }
    }

    private CardView createVendorCard(Vendor vendor, double budget) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                (int) (300 * getResources().getDisplayMetrics().density),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, (int) (12 * getResources().getDisplayMetrics().density), 0);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(6);
        cardView.setRadius(12);

        // Set card background color based on affordability
        if (vendor.price <= budget) {
            cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        } else {
            cardView.setCardBackgroundColor(0xFFFFEBEE); // Light red for over budget
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // Vendor name
        TextView nameText = new TextView(this);
        nameText.setText(vendor.name);
        nameText.setTextSize(16);
        nameText.setTypeface(nameText.getTypeface(), android.graphics.Typeface.BOLD);
        nameText.setTextColor(0xFF2E7D32);
        layout.addView(nameText);

        // Price
        TextView priceText = new TextView(this);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        priceText.setText(formatter.format(vendor.price));
        priceText.setTextSize(14);
        priceText.setTypeface(priceText.getTypeface(), android.graphics.Typeface.BOLD);
        if (vendor.price <= budget) {
            priceText.setTextColor(0xFF388E3C);
        } else {
            priceText.setTextColor(0xFFD32F2F);
        }
        layout.addView(priceText);

        // Rating
        TextView ratingText = new TextView(this);
        ratingText.setText("⭐ " + vendor.rating + "/5");
        ratingText.setTextSize(12);
        ratingText.setTextColor(0xFFFF9800);
        layout.addView(ratingText);

        // Description
        TextView descText = new TextView(this);
        descText.setText(vendor.description);
        descText.setTextSize(12);
        descText.setTextColor(0xFF666666);
        descText.setMaxLines(2);
        layout.addView(descText);

        // Contact
        TextView contactText = new TextView(this);
        contactText.setText("📞 " + vendor.contact);
        contactText.setTextSize(12);
        contactText.setTextColor(0xFF1976D2);
        layout.addView(contactText);

        // Select button
        Button selectButton = new Button(this);
        selectButton.setText(vendor.isSelected ? "Selected ✓" : "Select");
        selectButton.setTextSize(12);
        selectButton.setPadding(8, 8, 8, 8);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, 8, 0, 0);
        selectButton.setLayoutParams(buttonParams);

        if (vendor.isSelected) {
            selectButton.setBackgroundColor(0xFF4CAF50);
            selectButton.setTextColor(0xFFFFFFFF);
        } else {
            selectButton.setBackgroundColor(0xFF2196F3);
            selectButton.setTextColor(0xFFFFFFFF);
        }

        selectButton.setOnClickListener(v -> {
            if (vendor.isSelected) {
                // Deselect vendor
                vendor.isSelected = false;
                selectedVendors.remove(vendor);
                totalSpent -= vendor.price;
                selectButton.setText("Select");
                selectButton.setBackgroundColor(0xFF2196F3);
            } else {
                // Select vendor
                vendor.isSelected = true;
                selectedVendors.add(vendor);
                totalSpent += vendor.price;
                selectButton.setText("Selected ✓");
                selectButton.setBackgroundColor(0xFF4CAF50);

                Toast.makeText(this, "Vendor selected! Total vendors: " + selectedVendors.size(), Toast.LENGTH_SHORT).show();

            }
            updateBudgetDisplay();
            updateSelectedItemsDisplay();
        });

        layout.addView(selectButton);
        cardView.addView(layout);

        return cardView;
    }

    private void updateSelectedItemsDisplay() {
        if (selectedItemsContainer == null) return;

        selectedItemsContainer.removeAllViews();

        if (selectedVendors.isEmpty()) {
            selectedItemsCard.setVisibility(View.GONE);
            return;
        }

        selectedItemsCard.setVisibility(View.VISIBLE);

        for (Vendor vendor : selectedVendors) {
            View selectedItemView = createSelectedItemView(vendor);
            selectedItemsContainer.addView(selectedItemView);
        }
    }

    private View createSelectedItemView(Vendor vendor) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(12, 8, 12, 8);
        itemLayout.setBackground(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 4, 0, 4);
        itemLayout.setLayoutParams(layoutParams);

        // Vendor info
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        infoLayout.setLayoutParams(infoParams);

        TextView nameText = new TextView(this);
        nameText.setText(vendor.name + " (" + getCategoryEmoji(vendor.category) + ")");
        nameText.setTextSize(14);
        nameText.setTypeface(nameText.getTypeface(), android.graphics.Typeface.BOLD);
        nameText.setTextColor(0xFF2E7D32);

        TextView priceText = new TextView(this);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        priceText.setText(formatter.format(vendor.price));
        priceText.setTextSize(12);
        priceText.setTextColor(0xFF1976D2);

        infoLayout.addView(nameText);
        infoLayout.addView(priceText);

        // Remove button
        Button removeButton = new Button(this);
        removeButton.setText("✕");
        removeButton.setTextSize(12);
        removeButton.setBackgroundColor(0xFFFF5722);
        removeButton.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                80, 80
        );
        removeButton.setLayoutParams(buttonParams);

        removeButton.setOnClickListener(v -> {
            vendor.isSelected = false;
            selectedVendors.remove(vendor);
            totalSpent -= vendor.price;
            updateBudgetDisplay();
            updateSelectedItemsDisplay();
            updateVendorRecommendations(); // Refresh vendor cards to show unselected state
        });

        itemLayout.addView(infoLayout);
        itemLayout.addView(removeButton);

        return itemLayout;
    }

    private String getCategoryEmoji(String category) {
        switch (category) {
            case "venue": return "🏛️";
            case "catering": return "🍽️";
            case "photography": return "📸";
            case "decor": return "🌸";
            case "other": return "🎵";
            default: return "📋";
        }
    }
}