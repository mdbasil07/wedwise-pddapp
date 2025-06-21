package com.example.wedwise_java;

public class Venue {
    private String id;
    private String name;
    private String type;
    private String location;
    private int capacity;
    private float price;
    private float rating;
    private String description;
    private String imageUrl;
    private boolean isAvailable;
    private String contactNumber;
    private String address;
    private String email;
    private String establishedYear;
    private int totalBookings;
    private int experienceYears;

    // Default constructor
    public Venue() {
    }

    // Constructor
    public Venue(String name, String type, String location, int capacity, float price, float rating, String description, String imageUrl) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.capacity = capacity;
        this.price = price;
        this.rating = rating;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isAvailable = true;
    }

    // Full Constructor
    public Venue(String name, String type, String location, int capacity, float price, float rating,
                 String description, String imageUrl, boolean isAvailable, String contactNumber, String address) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.capacity = capacity;
        this.price = price;
        this.rating = rating;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    // Getters and Setters for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstablishedYear() {
        return establishedYear;
    }

    public void setEstablishedYear(String establishedYear) {
        this.establishedYear = establishedYear;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    // Utility methods
    public String getFormattedPrice() {
        return "₹" + String.format("%.0f", price);
    }

    public String getCapacityText() {
        return capacity + " guests";
    }

    public String getRatingText() {
        return String.format("%.1f", rating) + " ★";
    }

    @Override
    public String toString() {
        return "Venue{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", price=" + price +
                ", rating=" + rating +
                '}';
    }
}