package com.example.wedwise_java;

public class Task {
    private static int nextId = 1;
    private int id;
    private String title;
    private String dateTime;
    private String status;
    private boolean completed;
    private boolean notification;
    private boolean isMuted;
    private int reminderMinutes; // New field for reminder time in minutes
    private String notes;


    public Task(String title, String dateTime, String status, boolean completed, boolean notification, int reminderMinutes) {
        this.id = nextId++;
        this.title = title;
        this.dateTime = dateTime;
        this.status = status;
        this.completed = completed;
        this.notification = notification;
        this.isMuted = false;
        this.reminderMinutes = reminderMinutes;
        this.notes = ""; // default empty
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Constructor for backward compatibility
    public Task(String title, String dateTime, String status, boolean completed, boolean notification) {
        this(title, dateTime, status, completed, notification, 60); // Default to 1 hour
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean hasNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public int getReminderMinutes() {
        return reminderMinutes;
    }

    public void setReminderMinutes(int reminderMinutes) {
        this.reminderMinutes = reminderMinutes;
    }

    // Helper method to get reminder text for display
    public String getReminderText() {
        if (reminderMinutes < 60) {
            return reminderMinutes + " minutes before";
        } else if (reminderMinutes < 1440) { // Less than 24 hours
            int hours = reminderMinutes / 60;
            return hours + (hours == 1 ? " hour before" : " hours before");
        } else {
            int days = reminderMinutes / 1440;
            return days + (days == 1 ? " day before" : " days before");
        }
    }
}