package com.example.wedwise_java;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskDetailsActivity extends AppCompatActivity {

    EditText titleEdit, dateEdit, notesEdit;
    Spinner statusSpinner;
    Button editBtn, saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Initialize fields
        titleEdit = findViewById(R.id.editTitle);
        dateEdit = findViewById(R.id.editDate);
        notesEdit = findViewById(R.id.editNotes);
        statusSpinner = findViewById(R.id.editStatus);
        editBtn = findViewById(R.id.btnEdit);
        saveBtn = findViewById(R.id.btnSave);

        // Populate Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Pending", "Completed"}
        );
        statusSpinner.setAdapter(statusAdapter);

        // Set data from TaskData
        titleEdit.setText(TaskData.title);
        dateEdit.setText(TaskData.date);
        notesEdit.setText(TaskData.notes);

        // Set Spinner to current status
        int statusPosition = statusAdapter.getPosition(TaskData.status);
        if (statusPosition != -1) {
            statusSpinner.setSelection(statusPosition);
        }

        enableEditing(false);

        // Set date picker
        dateEdit.setOnClickListener(v -> showDateTimePicker());

        // Edit button
        editBtn.setOnClickListener(v -> enableEditing(true));

        // Save button
        saveBtn.setOnClickListener(v -> {
            // Get all updated values
            TaskData.title = titleEdit.getText().toString();
            TaskData.date = dateEdit.getText().toString();
            TaskData.notes = notesEdit.getText().toString();
            TaskData.status = statusSpinner.getSelectedItem().toString();

            // Return them to TasksActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", TaskData.title);
            resultIntent.putExtra("date", TaskData.date);
            resultIntent.putExtra("status", TaskData.status);
            resultIntent.putExtra("notes", TaskData.notes);
            resultIntent.putExtra("position", TaskData.position);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void enableEditing(boolean enable) {
        titleEdit.setEnabled(enable);
        dateEdit.setEnabled(enable);
        notesEdit.setEnabled(enable);
        statusSpinner.setEnabled(enable);
        saveBtn.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                TaskDetailsActivity.this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            TaskDetailsActivity.this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d/M/yyyy, HH:mm", Locale.getDefault());
                                dateEdit.setText(sdf.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }
}
