// VenueDetailsActivity.java
package com.example.wedwise_java;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.*;

public class VenueDetailsActivity extends AppCompatActivity {

    // Static temporary storage for prototype
    public static List<Venue> staticVenuesList = new ArrayList<>();
    public static Set<String> favoriteVenueIds = new HashSet<>();
    public static Map<String, List<String>> venueImages = new HashMap<>();
    public static Map<String, VenueDetails> venueDetailsMap = new HashMap<>();

    // Extended venue details class for additional info
    public static class VenueDetails {
        public String vegPrice;
        public String nonVegPrice;
        public String decorationPrice;
        public String photographyPrice;
        public String workingHours;
        public String experience;
        public String totalBookings;
        public List<String> amenities;
        public List<String> services;
        public List<String> imageUrls;

        public VenueDetails(String vegPrice, String nonVegPrice, String decorationPrice,
                            String photographyPrice, String workingHours, String experience,
                            String totalBookings, List<String> amenities, List<String> services,
                            List<String> imageUrls) {
            this.vegPrice = vegPrice;
            this.nonVegPrice = nonVegPrice;
            this.decorationPrice = decorationPrice;
            this.photographyPrice = photographyPrice;
            this.workingHours = workingHours;
            this.experience = experience;
            this.totalBookings = totalBookings;
            this.amenities = amenities;
            this.services = services;
            this.imageUrls = imageUrls;
        }
    }

    // UI Components
    private ImageView imageVenue;
    private TextView textVenueName, textVenueType, textVenueLocation, textVenueAddress;
    private TextView textVenueCapacity, textVenuePrice, textVenueRating, textVenueDescription;
    private TextView textExperience, textBookings, textWorkingHours;
    private TextView textVegPrice, textNonVegPrice, textDecorationPrice, textPhotographyPrice;
    private TextView textContactNumber, textEmail;
    private ChipGroup chipGroupAmenities, chipGroupServices;
    private Button buttonCall, buttonEmail, buttonBookNow, buttonGetDirections;
    private FloatingActionButton fabFavorite;
    private LinearLayout layoutPricing, layoutContact;
    private RecyclerView recyclerViewImages;
    private ScrollView scrollView;

    // Data
    private Venue currentVenue;
    private VenueDetails currentVenueDetails;
    private String venueId;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);

        // Initialize static data if empty (first time)
        if (staticVenuesList.isEmpty()) {
            initializeStaticData();
        }

        initializeViews();
        getVenueData();
        setupClickListeners();
        setupActionBar();
    }

    // Initialize static temporary data for prototype
    private void initializeStaticData() {
        // Create sample venues with extended details
        createStaticVenue("1", "Grand Chettinad Palace", "Marriage Hall", "Chennai",
                "T. Nagar, Chennai - 600017", 500, 250000f, 4.8f,
                "Authentic Chettinad architecture with royal ambiance perfect for traditional Tamil weddings.",
                "+91 9876543210", "grandchettinad@gmail.com");

        createStaticVenue("2", "The Leela Palace Chennai", "Hotel", "Chennai",
                "Adyar, Chennai - 600020", 800, 450000f, 4.9f,
                "Luxury 5-star hotel with world-class facilities and premium wedding services.",
                "+91 9876543211", "leela@chennai.com");

        createStaticVenue("3", "Codissia Trade Fair", "Convention Center", "Coimbatore",
                "Avinashi Road, Coimbatore - 641014", 2000, 350000f, 4.7f,
                "Massive convention center for grand celebrations with modern facilities.",
                "+91 9876543215", "codissia@coimbatore.com");

        createStaticVenue("4", "Heritage Madurai", "Heritage", "Madurai",
                "TPK Road, Madurai - 625003", 600, 200000f, 4.6f,
                "Heritage property with traditional Tamil architecture and cultural ambiance.",
                "+91 9876543219", "heritage@madurai.com");

        createStaticVenue("5", "Savoy Hotel", "Heritage", "Ooty",
                "Sylks Road, Ooty - 643001", 250, 320000f, 4.7f,
                "Historic colonial hotel perfect for intimate hill station weddings.",
                "+91 9876543227", "savoy@ooty.com");
    }

    private void createStaticVenue(String id, String name, String type, String location,
                                   String address, int capacity, float price, float rating,
                                   String description, String contact, String email) {

        // Create venue object
        Venue venue = new Venue(name, type, location, capacity, price, rating, description, "");
        venue.setAddress(address);
        venue.setContactNumber(contact);
        venue.setEmail(email);
        venue.setAvailable(true);

        // Add to static list
        staticVenuesList.add(venue);

        // Create extended details
        List<String> amenities = Arrays.asList(
                "Air Conditioning", "Parking Available", "Generator Backup",
                "Sound System", "Stage Setup", "Bridal Room",
                "Guest Rooms", "Kitchen Facilities", "Security"
        );

        List<String> services = Arrays.asList(
                "Catering Services", "Decoration", "Photography", "Videography",
                "DJ & Music", "Lighting", "Flower Arrangements", "Wedding Planning"
        );

        List<String> images = Arrays.asList(
                "venue_" + id + "_1.jpg", "venue_" + id + "_2.jpg",
                "venue_" + id + "_3.jpg", "venue_" + id + "_4.jpg"
        );

        VenueDetails details = new VenueDetails(
                "₹800 per plate", "₹1200 per plate", "₹50,000 - ₹2,00,000",
                "₹25,000 - ₹75,000", "6:00 AM - 11:59 PM", "15+ years experience",
                "450+ successful events", amenities, services, images
        );

        venueDetailsMap.put(id, details);
        venueImages.put(id, images);
    }

    private void initializeViews() {
        // Image and basic info
        imageVenue = findViewById(R.id.imageVenue);
        textVenueName = findViewById(R.id.textVenueName);
        textVenueType = findViewById(R.id.textVenueType);
        textVenueLocation = findViewById(R.id.textVenueLocation);
        textVenueAddress = findViewById(R.id.textVenueAddress);

        // Capacity and pricing
        textVenueCapacity = findViewById(R.id.textVenueCapacity);
        textVenuePrice = findViewById(R.id.textVenuePrice);
        textVenueRating = findViewById(R.id.textVenueRating);
        textVenueDescription = findViewById(R.id.textVenueDescription);

        // Experience and stats
        textExperience = findViewById(R.id.textExperience);
        textBookings = findViewById(R.id.textBookings);
        textWorkingHours = findViewById(R.id.textWorkingHours);

        // Pricing details
        textVegPrice = findViewById(R.id.textVegPrice);
        textNonVegPrice = findViewById(R.id.textNonVegPrice);
        textDecorationPrice = findViewById(R.id.textDecorationPrice);
        textPhotographyPrice = findViewById(R.id.textPhotographyPrice);

        // Contact info
        textContactNumber = findViewById(R.id.textContactNumber);
        textEmail = findViewById(R.id.textEmail);

        // Chip groups
        chipGroupAmenities = findViewById(R.id.chipGroupAmenities);
        chipGroupServices = findViewById(R.id.chipGroupServices);

        // Buttons
        buttonCall = findViewById(R.id.buttonCall);
        buttonEmail = findViewById(R.id.buttonEmail);
        buttonBookNow = findViewById(R.id.buttonBookNow);
        buttonGetDirections = findViewById(R.id.buttonGetDirections);

        // Layouts
        layoutPricing = findViewById(R.id.layoutPricing);
        layoutContact = findViewById(R.id.layoutContact);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        scrollView = findViewById(R.id.scrollView);
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Venue Details");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getVenueData() {
        // Get venue ID from intent
        venueId = getIntent().getStringExtra("VENUE_ID");

        // Also try to get venue name as fallback
        String venueName = getIntent().getStringExtra("VENUE_NAME");

        if (venueId != null) {
            // Find venue from static list using ID
            currentVenue = findVenueById(venueId);
            currentVenueDetails = venueDetailsMap.get(venueId);
        } else if (venueName != null) {
            // Fallback: find venue by name and set venueId
            currentVenue = findVenueByName(venueName);
            if (currentVenue != null) {
                // Set venueId based on the position in list + 1
                venueId = String.valueOf(staticVenuesList.indexOf(currentVenue) + 1);
                currentVenueDetails = venueDetailsMap.get(venueId);
            }
        } else {
            Toast.makeText(this, "Invalid venue ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (currentVenue != null) {
            populateVenueDetails();
            checkFavoriteStatus();
        } else {
            Toast.makeText(this, "Venue not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private Venue findVenueByName(String name) {
        for (Venue venue : staticVenuesList) {
            if (venue.getName().equals(name)) {
                return venue;
            }
        }
        return null;
    }

    private Venue findVenueById(String id) {
        // In a real app, you'd use the actual ID from database
        // For prototype, using index as ID
        try {
            int index = Integer.parseInt(id) - 1;
            if (index >= 0 && index < staticVenuesList.size()) {
                return staticVenuesList.get(index);
            }
        } catch (NumberFormatException e) {
            // If ID is not numeric, search by name or other criteria
            return findVenueByName(id);
        }
        return null;
    }

    private void checkFavoriteStatus() {
        isFavorite = favoriteVenueIds.contains(venueId);
        updateFavoriteIcon();
    }

    private void populateVenueDetails() {
        if (currentVenue == null) return;

        // Basic Information
        textVenueName.setText(currentVenue.getName());
        textVenueType.setText(currentVenue.getType());
        textVenueLocation.setText(currentVenue.getLocation());
        textVenueAddress.setText(currentVenue.getAddress());

        // Capacity and Pricing
        textVenueCapacity.setText(currentVenue.getCapacityText());
        textVenuePrice.setText(currentVenue.getFormattedPrice());
        textVenueRating.setText(currentVenue.getRatingText());
        textVenueDescription.setText(currentVenue.getDescription());

        // Contact Information
        textContactNumber.setText(currentVenue.getContactNumber());
        textEmail.setText(currentVenue.getEmail());

        // Extended details from static data
        if (currentVenueDetails != null) {
            textExperience.setText(currentVenueDetails.experience);
            textBookings.setText(currentVenueDetails.totalBookings);
            textWorkingHours.setText(currentVenueDetails.workingHours);

            // Pricing Details
            textVegPrice.setText(currentVenueDetails.vegPrice);
            textNonVegPrice.setText(currentVenueDetails.nonVegPrice);
            textDecorationPrice.setText(currentVenueDetails.decorationPrice);
            textPhotographyPrice.setText(currentVenueDetails.photographyPrice);

            // Populate amenities and services
            populateAmenities(currentVenueDetails.amenities);
            populateServices(currentVenueDetails.services);
        }

        // Load main image (using placeholder for prototype)
        imageVenue.setImageResource(R.drawable.ic_venues);

        // Setup image gallery
        setupImageGallery();
    }

    private void populateAmenities(List<String> amenities) {
        chipGroupAmenities.removeAllViews();

        for (String amenity : amenities) {
            Chip chip = new Chip(this);
            chip.setText(amenity);
            chip.setCheckable(false);
            chip.setChipBackgroundColorResource(android.R.color.holo_blue_light);
            chip.setTextColor(getResources().getColor(android.R.color.white));
            chipGroupAmenities.addView(chip);
        }
    }

    private void populateServices(List<String> services) {
        chipGroupServices.removeAllViews();

        for (String service : services) {
            Chip chip = new Chip(this);
            chip.setText(service);
            chip.setCheckable(false);
            chip.setChipBackgroundColorResource(android.R.color.holo_green_light);
            chip.setTextColor(getResources().getColor(android.R.color.white));
            chipGroupServices.addView(chip);
        }
    }

    private void setupImageGallery() {
        // For prototype, just showing placeholder message
        if (recyclerViewImages != null && currentVenueDetails != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewImages.setLayoutManager(layoutManager);

            // In a real app, you'd create an adapter for the image URLs
            // For prototype, showing image count
            Toast.makeText(this, currentVenueDetails.imageUrls.size() + " images available", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        // Call button
        buttonCall.setOnClickListener(v -> {
            if (currentVenue != null && currentVenue.getContactNumber() != null) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + currentVenue.getContactNumber()));
                startActivity(callIntent);
            }
        });

        // Email button
        buttonEmail.setOnClickListener(v -> {
            if (currentVenue != null && currentVenue.getEmail() != null) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + currentVenue.getEmail()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Wedding Venue Inquiry - " + currentVenue.getName());
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi,\n\nI am interested in booking your venue for a wedding. Please provide more details.\n\nThanks!");

                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                } else {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Book Now button
        buttonBookNow.setOnClickListener(v -> {
            // For prototype, just show a message
            Toast.makeText(this, "Booked Hall!", Toast.LENGTH_LONG).show();

            // In a real app, you'd navigate to booking activity:
            // Intent bookingIntent = new Intent(this, BookingActivity.class);
            // bookingIntent.putExtra("VENUE_ID", venueId);
            // startActivity(bookingIntent);
        });

        // Get Directions button
        buttonGetDirections.setOnClickListener(v -> {
            if (currentVenue != null) {
                String location = currentVenue.getAddress() != null ?
                        currentVenue.getAddress() : currentVenue.getLocation();

                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Fallback to web browser
                    String url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(location);
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(webIntent);
                }
            }
        });

        // Favorite button
        fabFavorite.setOnClickListener(v -> toggleFavorite());

        // Main image click for full-screen view
        imageVenue.setOnClickListener(v -> {
            Toast.makeText(this, "Full-screen image view - Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;

        if (isFavorite) {
            favoriteVenueIds.add(venueId); // Add to static storage
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        } else {
            favoriteVenueIds.remove(venueId); // Remove from static storage
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        }

        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    // Static methods to access temporary data from other activities
    public static List<Venue> getAllVenues() {
        return staticVenuesList;
    }

    public static Set<String> getFavoriteVenueIds() {
        return favoriteVenueIds;
    }

    public static void addToFavorites(String venueId) {
        favoriteVenueIds.add(venueId);
    }

    public static void removeFromFavorites(String venueId) {
        favoriteVenueIds.remove(venueId);
    }

    public static boolean isFavoriteVenue(String venueId) {
        return favoriteVenueIds.contains(venueId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh favorite status in case it was changed in another activity
        checkFavoriteStatus();
    }
}