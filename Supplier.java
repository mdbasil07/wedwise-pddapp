package com.example.wedwise_java;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class Supplier {
    private String name;
    private String note;
    private String phone;
    private String address;
    private String email;
    private boolean booked;
    private Uri imageUri;
    private String website;

    // Static list to store suppliers temporarily
    public static List<Supplier> supplierList = new ArrayList<>();

    public Supplier(String name, String note, String phone, String address, String email, boolean booked, Uri imageUri, String website) {
        this.name = name;
        this.note = note;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.booked = booked;
        this.imageUri = imageUri;
        this.website = website;
    }

    // Getter methods
    public String getName() { return name; }
    public String getNote() { return note; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public boolean isBooked() { return booked; }
    public Uri getImageUri() { return imageUri; }
    public String getWebsite() { return website; }

    public String getFirstLetter() {
        return (name != null && !name.isEmpty()) ? String.valueOf(name.charAt(0)).toUpperCase() : "?";
    }
}