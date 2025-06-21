package com.example.wedwise_java;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = TaskManager.getTaskList();
    private static final int ADD_TASK_REQUEST = 1;
    private static final int NOTIFICATION_PERMISSION_REQUEST = 100;
    private TaskNotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Initialize notification manager
        notificationManager = new TaskNotificationManager(this);

        // Request notification permission for Android 13+
        requestNotificationPermission();

        // Bottom Navigation Setup
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.getMenu().findItem(R.id.nav_tasks).setChecked(true);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(TasksActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_budget) {
                startActivity(new Intent(TasksActivity.this, BudgetActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_tasks) {
                return true;
            }else if (itemId == R.id.nav_venues) {
                startActivity(new Intent(TasksActivity.this, VenuesActivity.class));
                finish();
                return true;
            }
            else if (itemId == R.id.nav_suppliers) {
                startActivity(new Intent(TasksActivity.this, SuppliersActivity.class));
                finish();
                return true;
            }
            return false;
        });

        // RecyclerView Setup
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initTaskList();
        taskAdapter = new TaskAdapter(taskList);
        taskAdapter.setNotificationManager(notificationManager); // Pass notification manager to adapter
        tasksRecyclerView.setAdapter(taskAdapter);

        // Add Task Button
        FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(TasksActivity.this, AddTaskActivity.class);
            startActivityForResult(intent, ADD_TASK_REQUEST);
        });

        // Delete Completed Tasks Button
        FloatingActionButton deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> removeCompletedTasks());

        // Check for overdue tasks and show immediate notifications
        checkForOverdueTasks();

        // Schedule notifications for all existing tasks
        scheduleAllNotifications();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                scheduleAllNotifications();
            } else {
                Toast.makeText(this, "Notification permission denied. You won't receive task reminders.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initTaskList() {
        if (TaskManager.getTaskList().isEmpty()) {
            TaskManager.addTask(new Task("Invite Guests", "Sun, 16/2/2025, 09:40", "Time expired", false, true, 120));
            TaskManager.addTask(new Task("Marriage Hall Payment", "Fri, 14/2/2025, 09:40", "Time expired", false, true, 60));
            TaskManager.addTask(new Task("Invitation Cards", "Thu, 20/2/2025, 09:40", "2 days", false, true, 1440));
            TaskManager.addTask(new Task("Food Payment", "Sat, 22/2/2025, 09:40", "4 days", false, true, 30));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("taskTitle");
            String deadline = data.getStringExtra("deadline");
            String status = "Pending";
            boolean notification = data.getBooleanExtra("notificationEnabled", false);
            int reminderMinutes = data.getIntExtra("reminderMinutes", 60);

            Task newTask = new Task(title, deadline, status, false, notification, reminderMinutes);

            TaskManager.addTask(newTask);
            taskAdapter.notifyItemInserted(TaskManager.getTaskList().size() - 1);
            String notes = data.getStringExtra("notes");
            newTask.setNotes(notes);

        }


        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String updatedTitle = data.getStringExtra("title");
            String updatedDate = data.getStringExtra("date");
            String updatedStatus = data.getStringExtra("status");
            String updatedNotes = data.getStringExtra("notes");
            int pos = data.getIntExtra("position", -1);

            if (pos >= 0 && pos < TaskManager.getTaskList().size()) {
                Task task = TaskManager.getTaskList().get(pos);
                task.setTitle(updatedTitle);
                task.setDateTime(updatedDate);
                task.setStatus(updatedStatus);
                task.setNotes(updatedNotes);
                taskAdapter.notifyItemChanged(pos);
            }
        }
    }




    private void removeCompletedTasks() {
        List<Task> tasksToRemove = new ArrayList<>();
        for (Task task : taskList) {
            if (task.isCompleted()) {
                tasksToRemove.add(task);
                // Cancel notifications for completed tasks
                notificationManager.cancelTaskNotification(task.getId());
            }
        }
        taskList.removeAll(tasksToRemove);
        taskAdapter.notifyDataSetChanged();

        if (!tasksToRemove.isEmpty()) {
            Toast.makeText(this, "Completed tasks removed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkForOverdueTasks() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d/M/yyyy, HH:mm", Locale.getDefault());
        Date now = new Date();

        for (Task task : taskList) {
            if (!task.isCompleted()) {
                try {
                    Date dueDate = sdf.parse(task.getDateTime());
                    if (dueDate != null && dueDate.before(now)) {
                        // Task is overdue, show immediate notification
                        notificationManager.showImmediateNotification(task);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void scheduleAllNotifications() {
        for (Task task : taskList) {
            if (!task.isCompleted()) {
                notificationManager.scheduleTaskNotification(task, task.getId());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check for overdue tasks when app is resumed
        checkForOverdueTasks();
    }
}