package com.example.wedwise_java;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VenuesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVenues;
    private VenuesAdapter venuesAdapter;
    private SearchView searchViewVenues;
    private ChipGroup chipGroupVenueTypes;
    private RangeSlider priceRangeSlider;
    private Spinner spinnerCapacity, spinnerSort, spinnerLocation;
    private Button buttonApplyFilters, buttonClearFilters;
    private TextView textPriceRange, textResultsCount;
    private LinearLayout emptyStateLayout;

    private List<Venue> allVenues;
    private List<Venue> filteredVenues;

    // Custom color for chips and buttons
    private static final String CUSTOM_COLOR = "#FB9440";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues);

        initializeViews();
        setupRecyclerView();
        setupFilters();
        loadComprehensiveVenues();
        applyFilters();
        updateEmptyState();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        // Set the current item as selected (assuming nav_venues exists in your menu)
        bottomNavigation.setSelectedItemId(R.id.nav_venues);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                navigateTo(HomeActivity.class);
                return true;
            } else if (itemId == R.id.nav_budget) {
                navigateTo(BudgetActivity.class);
                return true;
            } else if (itemId == R.id.nav_tasks) {
                navigateTo(TasksActivity.class);
                return true;
            } else if (itemId == R.id.nav_venues) {
                // Current activity - no navigation needed
                return true;
            } else if (itemId == R.id.nav_suppliers) {
                navigateTo(SuppliersActivity.class);
                return true;
            }
            return false;
        });
    }

    private void initializeViews() {
        recyclerViewVenues = findViewById(R.id.recyclerViewVenues);
        searchViewVenues = findViewById(R.id.searchViewVenues);
        chipGroupVenueTypes = findViewById(R.id.chipGroupVenueTypes);
        priceRangeSlider = findViewById(R.id.priceRangeSlider);
        spinnerCapacity = findViewById(R.id.spinnerCapacity);
        spinnerSort = findViewById(R.id.spinnerSort);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        buttonApplyFilters = findViewById(R.id.buttonApplyFilters);
        buttonClearFilters = findViewById(R.id.buttonClearFilters);
        textPriceRange = findViewById(R.id.textPriceRange);
        textResultsCount = findViewById(R.id.textResultsCount);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        allVenues = new ArrayList<>();
        filteredVenues = new ArrayList<>();

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Wedding Venues");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Apply custom colors to buttons
        setupButtonColors();
    }

    private void setupButtonColors() {
        int customColor = Color.parseColor(CUSTOM_COLOR);

        // Set custom color for Apply Filters button
        buttonApplyFilters.setBackgroundTintList(ColorStateList.valueOf(customColor));
        buttonApplyFilters.setTextColor(Color.WHITE);

        // Set custom color for Clear Filters button
        buttonClearFilters.setBackgroundTintList(ColorStateList.valueOf(customColor));
        buttonClearFilters.setTextColor(Color.WHITE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void navigateTo(Class<?> activityClass) {
        if (!this.getClass().equals(activityClass)) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void setupRecyclerView() {
        venuesAdapter = new VenuesAdapter(filteredVenues, new VenuesAdapter.OnVenueClickListener() {
            @Override
            public void onVenueClick(Venue venue) {
                onVenueSelected(venue);
            }
        });

        recyclerViewVenues.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVenues.setAdapter(venuesAdapter);
    }


    private void onVenueSelected(Venue venue) {
        try {
            Intent intent = new Intent(this, VenueDetailsActivity.class);

            // Generate venue ID based on position in the list
            int venueIndex = allVenues.indexOf(venue);
            String venueId = String.valueOf(venueIndex + 1); // +1 because VenueDetailsActivity expects IDs starting from 1

            intent.putExtra("VENUE_ID", venueId);

            // Add additional venue data as fallback
            intent.putExtra("VENUE_NAME", venue.getName());
            intent.putExtra("VENUE_LOCATION", venue.getLocation());
            intent.putExtra("VENUE_PRICE", venue.getPrice());

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to open venue details. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFilters() {
        setupVenueTypeChips();
        setupSpinners();
        setupPriceRangeSlider();
        setupSearchView();
        setupFilterButtons();
    }

    private void setupVenueTypeChips() {
        String[] venueTypes = {"Marriage Hall", "Banquet Hall", "Resort", "Hotel"};
        int customColor = Color.parseColor(CUSTOM_COLOR);

        for (String type : venueTypes) {
            Chip chip = new Chip(this);
            chip.setText(type);
            chip.setCheckable(true);

            // Create ColorStateList for chip colors
            ColorStateList chipColorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked}, // checked state
                            new int[]{} // default state
                    },
                    new int[]{
                            customColor, // color when checked
                            Color.parseColor("#E0E0E0") // color when unchecked (light gray)
                    }
            );

            // Create ColorStateList for text colors
            ColorStateList textColorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked}, // checked state
                            new int[]{} // default state
                    },
                    new int[]{
                            Color.WHITE, // text color when checked
                            Color.parseColor("#424242") // text color when unchecked (dark gray)
                    }
            );

            chip.setChipBackgroundColor(chipColorStateList);
            chip.setTextColor(textColorStateList);

            // Set stroke color for better visibility
            ColorStateList strokeColorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    },
                    new int[]{
                            customColor,
                            Color.parseColor("#BDBDBD")
                    }
            );
            chip.setChipStrokeColor(strokeColorStateList);
            chip.setChipStrokeWidth(2f);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());
            chipGroupVenueTypes.addView(chip);
        }
    }

    private void setupSpinners() {
        // Capacity Spinner
        String[] capacityOptions = {"Any Capacity", "50-100", "100-200", "200-500", "500-1000", "1000-2000", "2000+"};
        ArrayAdapter<String> capacityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, capacityOptions);
        capacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCapacity.setAdapter(capacityAdapter);
        spinnerCapacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Sort Spinner
        String[] sortOptions = {"Recommended", "Price: Low to High", "Price: High to Low",
                "Rating: High to Low", "Capacity: Low to High", "Newest First", "Most Popular"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySorting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Location Spinner - Tamil Nadu cities
        String[] locationOptions = {"All Locations", "Chennai", "Coimbatore", "Madurai", "Trichy",
                "Salem"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locationOptions);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupPriceRangeSlider() {
        priceRangeSlider.setValueFrom(25000f);
        priceRangeSlider.setValueTo(1000000f);
        priceRangeSlider.setValues(Arrays.asList(25000f, 1000000f));

        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            updatePriceRangeText();
            if (fromUser) {
                applyFilters();
            }
        });

        updatePriceRangeText();
    }

    private void updatePriceRangeText() {
        List<Float> values = priceRangeSlider.getValues();
        String priceText = "₹" + String.format("%.0f", values.get(0)) + " - ₹" + String.format("%.0f", values.get(1));
        textPriceRange.setText(priceText);
    }

    private void setupSearchView() {
        searchViewVenues.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    applyFilters();
                }
                return true;
            }
        });
    }

    private void setupFilterButtons() {
        buttonApplyFilters.setOnClickListener(v -> applyFilters());
        buttonClearFilters.setOnClickListener(v -> clearAllFilters());
    }

    private void applyFilters() {
        filteredVenues.clear();

        String searchQuery = searchViewVenues.getQuery().toString().toLowerCase().trim();
        List<String> selectedTypes = getSelectedVenueTypes();
        String selectedCapacity = spinnerCapacity.getSelectedItem().toString();
        String selectedLocation = spinnerLocation.getSelectedItem().toString();
        List<Float> priceRange = priceRangeSlider.getValues();

        for (Venue venue : allVenues) {
            boolean matchesSearch = searchQuery.isEmpty() ||
                    venue.getName().toLowerCase().contains(searchQuery) ||
                    venue.getLocation().toLowerCase().contains(searchQuery) ||
                    venue.getType().toLowerCase().contains(searchQuery) ||
                    venue.getDescription().toLowerCase().contains(searchQuery);

            boolean matchesType = selectedTypes.isEmpty() || selectedTypes.contains(venue.getType());

            boolean matchesCapacity = selectedCapacity.equals("Any Capacity") ||
                    checkCapacityMatch(venue.getCapacity(), selectedCapacity);

            boolean matchesLocation = selectedLocation.equals("All Locations") ||
                    venue.getLocation().equals(selectedLocation);

            boolean matchesPrice = venue.getPrice() >= priceRange.get(0) &&
                    venue.getPrice() <= priceRange.get(1);

            if (matchesSearch && matchesType && matchesCapacity && matchesLocation && matchesPrice) {
                filteredVenues.add(venue);
            }
        }

        applySorting();
        updateResultsCount();
        updateEmptyState();
    }

    private List<String> getSelectedVenueTypes() {
        List<String> selectedTypes = new ArrayList<>();
        for (int i = 0; i < chipGroupVenueTypes.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupVenueTypes.getChildAt(i);
            if (chip.isChecked()) {
                selectedTypes.add(chip.getText().toString());
            }
        }
        return selectedTypes;
    }

    private boolean checkCapacityMatch(int venueCapacity, String selectedCapacity) {
        switch (selectedCapacity) {
            case "50-100":
                return venueCapacity >= 50 && venueCapacity <= 100;
            case "100-200":
                return venueCapacity >= 100 && venueCapacity <= 200;
            case "200-500":
                return venueCapacity >= 200 && venueCapacity <= 500;
            case "500-1000":
                return venueCapacity >= 500 && venueCapacity <= 1000;
            case "1000-2000":
                return venueCapacity >= 1000 && venueCapacity <= 2000;
            case "2000+":
                return venueCapacity >= 2000;
            default:
                return true;
        }
    }

    private void applySorting() {
        String selectedSort = spinnerSort.getSelectedItem().toString();

        switch (selectedSort) {
            case "Price: Low to High":
                Collections.sort(filteredVenues, (v1, v2) -> Float.compare(v1.getPrice(), v2.getPrice()));
                break;
            case "Price: High to Low":
                Collections.sort(filteredVenues, (v1, v2) -> Float.compare(v2.getPrice(), v1.getPrice()));
                break;
            case "Rating: High to Low":
                Collections.sort(filteredVenues, (v1, v2) -> Float.compare(v2.getRating(), v1.getRating()));
                break;
            case "Capacity: Low to High":
                Collections.sort(filteredVenues, (v1, v2) -> Integer.compare(v1.getCapacity(), v2.getCapacity()));
                break;
            case "Most Popular":
                Collections.sort(filteredVenues, (v1, v2) -> Integer.compare(v2.getTotalBookings(), v1.getTotalBookings()));
                break;
            case "Newest First":
                Collections.sort(filteredVenues, (v1, v2) -> v2.getEstablishedYear().compareTo(v1.getEstablishedYear()));
                break;
            default: // Recommended - keep original order
                break;
        }

        venuesAdapter.notifyDataSetChanged();
    }

    private void clearAllFilters() {
        searchViewVenues.setQuery("", false);

        // Clear all chips
        for (int i = 0; i < chipGroupVenueTypes.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupVenueTypes.getChildAt(i);
            chip.setChecked(false);
        }

        // Reset spinners
        spinnerCapacity.setSelection(0);
        spinnerSort.setSelection(0);
        spinnerLocation.setSelection(0);

        // Reset price range
        priceRangeSlider.setValues(Arrays.asList(25000f, 1000000f));

        applyFilters();
    }

    private void updateResultsCount() {
        if (textResultsCount != null) {
            String countText = filteredVenues.size() + " venues found";
            textResultsCount.setText(countText);
        }
    }

    private void updateEmptyState() {
        if (filteredVenues.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerViewVenues.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerViewVenues.setVisibility(View.VISIBLE);
        }
    }

    // Helper method to create venues
    private void createVenue(String name, String type, String location, String address,
                             int minCapacity, int maxCapacity, float price, float rating,
                             String description, List<String> amenities, String contact,
                             String email, int experienceYears, int totalBookings) {

        Venue venue = new Venue();
        venue.setName(name);
        venue.setType(type);
        venue.setLocation(location);
        venue.setAddress(address);
        venue.setCapacity(maxCapacity); // Using maxCapacity as the main capacity
        venue.setPrice(price);
        venue.setRating(rating);
        venue.setDescription(description);
        venue.setContactNumber(contact);
        venue.setEmail(email);
        venue.setExperienceYears(experienceYears);
        venue.setTotalBookings(totalBookings);
        venue.setAvailable(true);
        venue.setEstablishedYear(String.valueOf(2024 - experienceYears));

        // Set the ID based on the position in the list + 1
        venue.setId(String.valueOf(allVenues.size() + 1));

        allVenues.add(venue);
    }

    private void loadComprehensiveVenues() {
        // Chennai Venues
        createVenue("Grand Chettinad Palace", "Marriage Hall", "Chennai", "T. Nagar, Chennai",
                150, 500, 250000, 4.8f, "Authentic Chettinad architecture with royal ambiance perfect for traditional Tamil weddings",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography"),
                "+91 9876543210", "grandchettinad@gmail.com", 15, 450);

        createVenue("The Leela Palace Chennai", "Hotel", "Chennai", "Adyar, Chennai",
                200, 800, 450000, 4.9f, "Luxury 5-star hotel with world-class facilities and premium wedding services",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography", "Music", "Lighting"),
                "+91 9876543211", "leela@chennai.com", 25, 1200);

        createVenue("Kalaivaani Cultural Centre", "Auditorium", "Chennai", "Mylapore, Chennai",
                300, 1200, 180000, 4.6f, "Traditional auditorium perfect for cultural weddings with excellent acoustics",
                Arrays.asList("AC", "Parking", "Music", "Lighting"),
                "+91 9876543212", "kalaivaani@gmail.com", 30, 800);

        createVenue("ITC Grand Chola", "Hotel", "Chennai", "Guindy, Chennai",
                250, 1000, 550000, 4.9f, "Ultra-luxury hotel with multiple banquet halls and premium wedding services",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography", "Music", "Lighting"),
                "+91 9876543213", "grandchola@itc.com", 12, 680);

        createVenue("Music Academy", "Auditorium", "Chennai", "T.T.K. Road, Chennai",
                400, 1500, 200000, 4.7f, "Iconic venue for classical music and cultural wedding ceremonies",
                Arrays.asList("AC", "Parking", "Music", "Lighting"),
                "+91 9876543214", "musicacademy@chennai.com", 85, 1500);

        // Coimbatore Venues
        createVenue("Codissia Trade Fair", "Convention Center", "Coimbatore", "Avinashi Road, Coimbatore",
                500, 2000, 350000, 4.7f, "Massive convention center for grand celebrations with modern facilities",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography", "Music", "Lighting"),
                "+91 9876543215", "codissia@coimbatore.com", 20, 950);

        createVenue("Vivanta Coimbatore", "Hotel", "Coimbatore", "Race Course Road, Coimbatore",
                100, 400, 280000, 4.5f, "Modern hotel with elegant banquet facilities and professional wedding planning",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography"),
                "+91 9876543216", "vivanta@coimbatore.com", 12, 320);

        createVenue("Sri Annapoorna Gardens", "Garden", "Coimbatore", "Peelamedu, Coimbatore",
                200, 800, 150000, 4.4f, "Beautiful garden venue with natural ambiance and outdoor wedding setup",
                Arrays.asList("Parking", "Catering", "Decoration", "Photography"),
                "+91 9876543217", "annapoorna@gardens.com", 18, 650);

        createVenue("Brookefields Mall Convention", "Convention Center", "Coimbatore", "Brookefields, Coimbatore",
                300, 1200, 320000, 4.6f, "Modern convention center with retail complex and ample parking",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Music", "Lighting"),
                "+91 9876543218", "brookefields@convention.com", 8, 420);

        // Madurai Venues
        createVenue("Heritage Madurai", "Heritage", "Madurai", "TPK Road, Madurai",
                150, 600, 200000, 4.6f, "Heritage property with traditional Tamil architecture and cultural ambiance",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography"),
                "+91 9876543219", "heritage@madurai.com", 35, 750);

        createVenue("Meenakshi Mission Hospital Convention", "Convention Center", "Madurai", "Lake Area, Madurai",
                300, 1000, 220000, 4.3f, "Modern convention facility with medical backup and professional services",
                Arrays.asList("AC", "Parking", "Catering", "Music", "Lighting"),
                "+91 9876543220", "meenakshi@convention.com", 8, 280);

        createVenue("Fortune Pandian Hotel", "Hotel", "Madurai", "Race Course Road, Madurai",
                120, 450, 190000, 4.4f, "Premium hotel with traditional South Indian wedding services",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography"),
                "+91 9876543221", "fortune@madurai.com", 15, 380);

        // Trichy Venues
        createVenue("Sangam Hotel", "Hotel", "Trichy", "Collector Office Road, Trichy",
                100, 300, 160000, 4.2f, "Established hotel with multiple banquet halls and traditional wedding services",
                Arrays.asList("AC", "Parking", "Catering", "Decoration"),
                "+91 9876543222", "sangam@trichy.com", 40, 900);

        createVenue("Rock Fort Marriage Hall", "Marriage Hall", "Trichy", "Rock Fort Area, Trichy",
                200, 750, 140000, 4.3f, "Spacious marriage hall with traditional architecture near historic Rock Fort",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Music"),
                "+91 9876543223", "rockfort@hall.com", 22, 680);

        createVenue("Grand Gardenia", "Banquet Hall", "Trichy", "Cantonment, Trichy",
                150, 500, 175000, 4.5f, "Elegant banquet hall with modern amenities and professional wedding planning",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography", "Music"),
                "+91 9876543224", "gardenia@trichy.com", 12, 340);

        // Salem Venues
        createVenue("GRT Grand", "Hotel", "Salem", "Junction Main Road, Salem",
                180, 600, 210000, 4.5f, "Grand hotel with multiple wedding halls and comprehensive wedding services",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography", "Music"),
                "+91 9876543225", "grt@salem.com", 18, 520);

        createVenue("Hotel Selvam", "Hotel", "Salem", "Four Roads, Salem",
                100, 350, 145000, 4.2f, "Well-established hotel with traditional South Indian wedding arrangements",
                Arrays.asList("AC", "Parking", "Catering", "Decoration"),
                "+91 9876543226", "selvam@salem.com", 25, 680);

        // Add more venues from other cities...
        createVenue("Hotel Parisutham", "Hotel", "Thanjavur", "Grand Anicut Canal Road, Thanjavur",
                120, 400, 165000, 4.3f, "Traditional hotel near Brihadeeswarar Temple with cultural wedding services",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography"),
                "+91 9876543231", "parisutham@thanjavur.com", 28, 590);

        createVenue("Kanchipuram Temple Wedding Hall", "Temple", "Kanchipuram", "Varadaraja Perumal Street, Kanchipuram",
                200, 800, 120000, 4.7f, "Sacred temple venue for traditional Tamil Hindu weddings with religious ceremonies",
                Arrays.asList("Parking", "Catering", "Decoration", "Music"),
                "+91 9876543232", "temple@kanchi.com", 50, 1200);

        createVenue("Vellore Fort Convention", "Heritage", "Vellore", "Fort Museum Road, Vellore",
                250, 900, 185000, 4.4f, "Historic fort venue with royal ambiance and cultural significance",
                Arrays.asList("AC", "Parking", "Catering", "Decoration", "Photography", "Music"),
                "+91 9876543233", "fort@vellore.com", 35, 420);
    }
}