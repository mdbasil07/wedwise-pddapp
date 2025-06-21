package com.example.wedwise_java;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    // Static List to hold tasks temporarily
    private static List<Task> taskList = new ArrayList<>();

    public static List<Task> getTaskList() {
        return taskList;
    }

    public static void addTask(Task task) {
        taskList.add(task);
    }

    public static void removeTask(Task task) {
        taskList.remove(task);
    }

    public static void clearTasks() {
        taskList.clear();
    }
}