package com.example.wedwise_java;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText taskInput;
    private EditText deadlineInput;
    private Button reminderButton;
    private Button notifyButton;
    private EditText notesInput;
    private Button addButton;
    private ImageButton backButton;

    private Calendar selectedDateTime = Calendar.getInstance();
    private boolean isNotificationEnabled = false;
    private int reminderMinutes = 60; // Default to 1 hour
    private final String[] reminderOptions = {"30 minutes before", "1 hour before", "2 hours before", "1 day before", "Remind at exact time", "Custom"};
    private final int[] reminderValues = {30, 60, 120, 1440, 0, -1}; // 0 for exact time, -1 for custom

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Initialize UI components
        taskInput = findViewById(R.id.taskInput);
        deadlineInput = findViewById(R.id.deadlineInput);
        reminderButton = findViewById(R.id.reminderButton);
        notifyButton = findViewById(R.id.notifyButton);
        notesInput = findViewById(R.id.notesInput);
        addButton = findViewById(R.id.addButton);
        backButton = findViewById(R.id.backButton);

        // Disable direct input and show date/time picker on click
        deadlineInput.setFocusable(false);
        deadlineInput.setClickable(true);
        deadlineInput.setOnClickListener(v -> showDateTimePicker());

        // Initialize reminder button text and click logic
        updateReminderButtonText();
        reminderButton.setOnClickListener(v -> showReminderDialog());

        // Initialize notify button text and toggle logic
        updateNotifyButtonText();
        notifyButton.setOnClickListener(v -> {
            isNotificationEnabled = !isNotificationEnabled;
            updateNotifyButtonText();
        });

        // Add button: validate input and send result back
        addButton.setOnClickListener(v -> saveTask());

        // Back button finishes activity
        backButton.setOnClickListener(v -> finish());
    }

    private void updateReminderButtonText() {
        String reminderText = getReminderText(reminderMinutes);
        reminderButton.setText("Reminder: " + reminderText);
    }

    private String getReminderText(int minutes) {
        if (minutes == 0) {
            return "at exact time";
        } else if (minutes < 60) {
            return minutes + " min before";
        } else if (minutes < 1440) {
            int hours = minutes / 60;
            return hours + (hours == 1 ? " hour before" : " hours before");
        } else {
            int days = minutes / 1440;
            return days + (days == 1 ? " day before" : " days before");
        }
    }

    private void showReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Reminder Time");
        builder.setItems(reminderOptions, (dialog, which) -> {
            if (which == 5) { // Custom option (last index)
                showCustomReminderDialog();
            } else {
                reminderMinutes = reminderValues[which];
                updateReminderButtonText();
            }
        });
        builder.show();
    }

    private void showCustomReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Custom Reminder");

        final EditText input = new EditText(this);
        input.setHint("Enter minutes (e.g., 90 for 1.5 hours before, 0 for exact time)");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                try {
                    int customMinutes = Integer.parseInt(value);
                    if (customMinutes >= 0) { // Allow 0 for exact time
                        reminderMinutes = customMinutes;
                        updateReminderButtonText();
                    } else {
                        // Invalid input, keep current value
                        android.widget.Toast.makeText(this, "Please enter a non-negative number", android.widget.Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    // Invalid input, keep current value
                    android.widget.Toast.makeText(this, "Please enter a valid number", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateNotifyButtonText() {
        notifyButton.setText(isNotificationEnabled ? "Notify: ON" : "Notify: OFF");
    }

    private void showDateTimePicker() {
        // Date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    showTimePicker();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        // Time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    selectedDateTime.set(Calendar.SECOND, 0);
                    selectedDateTime.set(Calendar.MILLISECOND, 0);
                    updateDeadlineText();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDeadlineText() {
        // Changed to match the format expected by TaskAdapter
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, d/M/yyyy, HH:mm", Locale.getDefault());
        String formattedDateTime = displayFormat.format(selectedDateTime.getTime());
        deadlineInput.setText(formattedDateTime);
    }

    private void saveTask() {
        String taskTitle = taskInput.getText().toString().trim();

        // Changed to match the format expected by TaskAdapter
        SimpleDateFormat appDateTimeFormat = new SimpleDateFormat("EEE, d/M/yyyy, HH:mm", Locale.getDefault());
        String deadline = appDateTimeFormat.format(selectedDateTime.getTime());

        String notes = notesInput.getText().toString().trim();

        // Validate
        if (taskTitle.isEmpty()) {
            taskInput.setError("Task title is required");
            return;
        }

        // Make sure user picked date/time (or you can allow optional)
        if (deadline.isEmpty()) {
            deadlineInput.setError("Deadline is required");
            return;
        }

        // Send data back
        Intent resultIntent = new Intent();
        resultIntent.putExtra("taskTitle", taskTitle);
        resultIntent.putExtra("deadline", deadline);
        resultIntent.putExtra("notes", notes);
        resultIntent.putExtra("reminderMinutes", reminderMinutes);
        resultIntent.putExtra("notificationEnabled", isNotificationEnabled);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}