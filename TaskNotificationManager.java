package com.example.wedwise_java;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskNotificationManager {

    private static final String CHANNEL_ID_SOUND = "TASK_REMINDERS_SOUND";
    private static final String CHANNEL_ID_VIBRATE = "TASK_REMINDERS_VIBRATE";
    private static final String CHANNEL_NAME_SOUND = "Task Reminders (Sound)";
    private static final String CHANNEL_NAME_VIBRATE = "Task Reminders (Vibrate Only)";
    private static final String CHANNEL_DESCRIPTION = "Notifications for task deadlines";

    private Context context;
    private NotificationManager notificationManager;

    public TaskNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel for notifications with sound
            NotificationChannel soundChannel = new NotificationChannel(
                    CHANNEL_ID_SOUND,
                    CHANNEL_NAME_SOUND,
                    NotificationManager.IMPORTANCE_HIGH
            );
            soundChannel.setDescription(CHANNEL_DESCRIPTION);
            soundChannel.enableVibration(true);
            soundChannel.setShowBadge(true);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            soundChannel.setSound(defaultSoundUri, null);
            notificationManager.createNotificationChannel(soundChannel);

            // Channel for vibrate-only notifications
            NotificationChannel vibrateChannel = new NotificationChannel(
                    CHANNEL_ID_VIBRATE,
                    CHANNEL_NAME_VIBRATE,
                    NotificationManager.IMPORTANCE_HIGH
            );
            vibrateChannel.setDescription(CHANNEL_DESCRIPTION + " (Vibrate Only)");
            vibrateChannel.enableVibration(true);
            vibrateChannel.setShowBadge(true);
            vibrateChannel.setSound(null, null); // No sound for this channel
            notificationManager.createNotificationChannel(vibrateChannel);
        }
    }

    public void scheduleTaskNotification(Task task, int taskId) {
        if (task.isCompleted()) {
            return; // Don't schedule notifications for completed tasks
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d/M/yyyy, HH:mm", Locale.getDefault());
        try {
            Date dueDate = sdf.parse(task.getDateTime());
            long dueTimeMillis = dueDate.getTime();
            long currentTimeMillis = System.currentTimeMillis();

            // Schedule notification based on the task's reminder time
            long reminderTimeMillis = task.getReminderMinutes() * 60 * 1000; // Convert minutes to milliseconds
            long notificationTime = dueTimeMillis - reminderTimeMillis;

            if (notificationTime > currentTimeMillis) {
                String reminderText = "Task Reminder - " + task.getReminderText();
                scheduleNotification(task, taskId, notificationTime, reminderText);
            }

            // Schedule notification at deadline (always at due time)
            if (dueTimeMillis > currentTimeMillis) {
                scheduleNotification(task, taskId + 1000, dueTimeMillis, "Task Deadline Reached!");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void scheduleNotification(Task task, int notificationId, long triggerTime, String title) {
        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.putExtra("task_title", task.getTitle());
        intent.putExtra("task_datetime", task.getDateTime());
        intent.putExtra("notification_title", title);
        intent.putExtra("notification_id", notificationId);
        intent.putExtra("task_muted", task.isMuted()); // Pass mute state
        intent.putExtra("reminder_text", task.getReminderText()); // Pass reminder text

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }

    public void cancelTaskNotification(int taskId) {
        // Cancel both notifications (reminder and at deadline)
        cancelNotification(taskId);
        cancelNotification(taskId + 1000);
    }

    private void cancelNotification(int notificationId) {
        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void showImmediateNotification(Task task) {
        Intent intent = new Intent(context, TasksActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Choose channel based on mute state
        String channelId = task.isMuted() ? CHANNEL_ID_VIBRATE : CHANNEL_ID_SOUND;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Task Overdue!")
                .setContentText(task.getTitle() + " - " + task.getDateTime())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500});

        // For older Android versions, explicitly set sound
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (!task.isMuted()) {
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(defaultSoundUri);
            }
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(task.getTitle().hashCode(), builder.build());
    }

    // BroadcastReceiver class for handling scheduled notifications
    public static class TaskNotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String taskTitle = intent.getStringExtra("task_title");
            String taskDateTime = intent.getStringExtra("task_datetime");
            String notificationTitle = intent.getStringExtra("notification_title");
            String reminderText = intent.getStringExtra("reminder_text");
            int notificationId = intent.getIntExtra("notification_id", 0);
            boolean taskMuted = intent.getBooleanExtra("task_muted", false);

            Intent activityIntent = new Intent(context, TasksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Choose channel based on mute state
            String channelId = taskMuted ? CHANNEL_ID_VIBRATE : CHANNEL_ID_SOUND;

            // Create notification content with reminder info
            String contentText = taskTitle + " - " + taskDateTime;
            if (reminderText != null && !reminderText.isEmpty()) {
                contentText += "\n(" + reminderText + ")";
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(notificationTitle)
                    .setContentText(contentText)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 500, 200, 500});

            // For older Android versions, explicitly set sound
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                if (!taskMuted) {
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    builder.setSound(defaultSoundUri);
                }
            }

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(notificationId, builder.build());
        }
    }
}