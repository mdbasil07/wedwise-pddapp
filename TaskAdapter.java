package com.example.wedwise_java;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.content.Intent;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private TaskNotificationManager notificationManager;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    // Add method to set notification manager
    public void setNotificationManager(TaskNotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Task task = taskList.get(position);

        holder.taskTitle.setText(task.getTitle());
        holder.taskDate.setText(task.getDateTime());

        // Calculate time left and update status text
        String statusText = getTimeLeftStatus(task.getDateTime());
        holder.taskStatus.setText(statusText);

        // Change status color for expired tasks
        if ("Expired".equalsIgnoreCase(statusText)) {
            holder.taskStatus.setTextColor(Color.RED);
        } else {
            holder.taskStatus.setTextColor(Color.BLACK);
        }

        // Set notification icon based on muted state
        holder.notificationIcon.setImageResource(
                task.isMuted() ? R.drawable.ic_muted : R.drawable.ic_notification
        );

        holder.itemView.setOnClickListener(v -> {
            TaskData.title = task.getTitle();
            TaskData.date = task.getDateTime();
            TaskData.status = task.getStatus();
            TaskData.notes = task.getNotes(); // 🆕
            TaskData.position = position;

            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, TaskDetailsActivity.class);
            ((Activity) context).startActivityForResult(intent, 101);
        });




        // Set click listener for notification icon toggle
        holder.notificationIcon.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Task clickedTask = taskList.get(adapterPosition);
                clickedTask.setMuted(!clickedTask.isMuted());
                // Update icon after toggle
                holder.notificationIcon.setImageResource(
                        clickedTask.isMuted() ? R.drawable.ic_muted : R.drawable.ic_notification
                );

                // Reschedule notifications with new mute state
                if (notificationManager != null && !clickedTask.isCompleted()) {
                    notificationManager.cancelTaskNotification(clickedTask.getId());
                    notificationManager.scheduleTaskNotification(clickedTask, clickedTask.getId());
                }
            }
        });

        // Prevent checkbox listener firing on recycle by clearing it first
        holder.taskCheckbox.setOnCheckedChangeListener(null);
        holder.taskCheckbox.setChecked(task.isCompleted());

        // Add listener to update task completion
        holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);

            // Cancel notifications for completed tasks
            if (isChecked && notificationManager != null) {
                notificationManager.cancelTaskNotification(task.getId());
            }
        });
    }



    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCheckbox;
        TextView taskTitle, taskDate, taskStatus;
        ImageView notificationIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheckbox = itemView.findViewById(R.id.taskCheckbox);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDate = itemView.findViewById(R.id.taskDate);
            taskStatus = itemView.findViewById(R.id.taskStatus);
            notificationIcon = itemView.findViewById(R.id.notificationIcon);
        }
    }

    private String getTimeLeftStatus(String dateTimeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d/M/yyyy, HH:mm", Locale.getDefault());

        try {
            Date dueDate = sdf.parse(dateTimeStr);
            Date now = new Date();
            long diffMillis = dueDate.getTime() - now.getTime();

            if (diffMillis <= 0) {
                return "Expired";
            }

            long diffSeconds = diffMillis / 1000;
            long minutes = diffSeconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                return days + (days == 1 ? " day left" : " days left");
            } else if (hours > 0) {
                return hours + (hours == 1 ? " hour left" : " hours left");
            } else if (minutes > 0) {
                return minutes + (minutes == 1 ? " minute left" : " minutes left");
            } else {
                return "Less than a minute left";
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }

}