package com.example.wedwise_java;

// AndroidAppChatbot.java - Chatbot for your Android app
import java.util.*;
import java.text.SimpleDateFormat;

public class AndroidAppChatbot {

    // Current user email
    private String currentUserEmail;

    // References to your static data (you'll connect these to your actual variables)
    private List<BudgetItem> userBudgets;
    private List<TaskItem> userTasks;
    private List<SupplierItem> userSuppliers;

    // Constructor
    public AndroidAppChatbot() {
        this.currentUserEmail = "";
        this.userBudgets = new ArrayList<>();
        this.userTasks = new ArrayList<>();
        this.userSuppliers = new ArrayList<>();
    }

    // Set current user and connect your static data
    public void setUserData(String userEmail, List<BudgetItem> budgets,
                            List<TaskItem> tasks, List<SupplierItem> suppliers) {
        this.currentUserEmail = userEmail;
        this.userBudgets = budgets != null ? budgets : new ArrayList<>();
        this.userTasks = tasks != null ? tasks : new ArrayList<>();
        this.userSuppliers = suppliers != null ? suppliers : new ArrayList<>();
    }

    // Main function to get chatbot response
    public String getResponse(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "I didn't catch that. How can I help you?";
        }

        String input = userInput.toLowerCase().trim();
        String intent = detectIntent(input);

        switch (intent) {
            case "greeting":
                return getGreetingResponse();
            case "contact_support":
                return getSupportResponse();
            case "app_features":
                return getFeaturesResponse();
            case "analyze_budget":
                return analyzeBudget();
            case "analyze_tasks":
                return analyzeTasks();
            case "analyze_suppliers":
                return analyzeSuppliers();
            default:
                return getDefaultResponse();
        }
    }

    // Detect what user wants
    private String detectIntent(String input) {
        // Greeting keywords
        if (containsAny(input, Arrays.asList("hello", "hi", "hey", "start", "good morning"))) {
            return "greeting";
        }

        // Support contact keywords
        if (containsAny(input, Arrays.asList("contact", "support", "help", "email", "customer service"))) {
            return "contact_support";
        }

        // App features keywords
        if (containsAny(input, Arrays.asList("features", "what can", "how to use", "guide"))) {
            return "app_features";
        }

        // Budget analysis keywords
        if (containsAny(input, Arrays.asList("budget", "money", "expense", "spending")) &&
                containsAny(input, Arrays.asList("check", "analyze", "show", "status", "problem", "remaining"))) {
            return "analyze_budget";
        }

        // Task analysis keywords
        if (containsAny(input, Arrays.asList("task", "todo", "work", "deadline")) &&
                containsAny(input, Arrays.asList("check", "analyze", "show", "status", "how many"))) {
            return "analyze_tasks";
        }

        // Supplier analysis keywords
        if (containsAny(input, Arrays.asList("supplier", "vendor", "company", "booked")) &&
                containsAny(input, Arrays.asList("check", "analyze", "show", "status", "how many"))) {
            return "analyze_suppliers";
        }

        return "default";
    }

    // Check if input contains any of the keywords
    private boolean containsAny(String input, List<String> keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    // Response functions
    private String getGreetingResponse() {
        List<String> responses = Arrays.asList(
                "Hello! I'm your app assistant. How can I help you today?",
                "Hi there! I can help with your budget, tasks, suppliers, or answer general questions!",
                "Hey! What would you like to know about your app or data?"
        );
        return getRandomResponse(responses);
    }

    private String getSupportResponse() {
        List<String> responses = Arrays.asList(
                "You can contact our support team at: support@yourapp.com",
                "Need help? Email us at support@yourapp.com - we respond within 24 hours!",
                "For support, reach us at support@yourapp.com or call +1-800-YOURAPP"
        );
        return getRandomResponse(responses);
    }

    private String getFeaturesResponse() {
        List<String> responses = Arrays.asList(
                "Our app helps you manage budgets, track tasks, and organize suppliers!",
                "You can create budgets with categories, add tasks with deadlines, and manage supplier details.",
                "The app includes budget tracking, task management, and supplier organization. What interests you?"
        );
        return getRandomResponse(responses);
    }

    private String getDefaultResponse() {
        List<String> responses = Arrays.asList(
                "I can help you check your budget, tasks, suppliers, or answer app questions. What do you need?",
                "Try asking me about your budget status, task progress, or how to contact support!",
                "I'm here to help! Ask me about budgets, tasks, suppliers, or general app questions.",
                "Not sure about that, but I can analyze your data or answer app-related questions!"
        );
        return getRandomResponse(responses);
    }

    // Budget analysis
    private String analyzeBudget() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            return "Please log in first to check your budget!";
        }

        if (userBudgets == null || userBudgets.isEmpty()) {
            return "❌ You haven't created any budgets yet. Would you like to create one?";
        }

        double totalBudget = 0;
        double remainingBudget = 0;
        Set<String> categories = new HashSet<>();
        int itemCount = 0;

        for (BudgetItem budget : userBudgets) {
            totalBudget += budget.getTotalBudget();
            remainingBudget += budget.getRemainingBudget();
            if (budget.getCategory() != null && !budget.getCategory().isEmpty()) {
                categories.add(budget.getCategory());
            }
            if (budget.getItem() != null && !budget.getItem().isEmpty()) {
                itemCount++;
            }
        }

        double spentAmount = totalBudget - remainingBudget;
        double percentageUsed = totalBudget > 0 ? (spentAmount / totalBudget) * 100 : 0;

        StringBuilder response = new StringBuilder();

        if (percentageUsed > 90) {
            response.append("🚨 BUDGET ALERT! You've spent ").append(String.format("%.1f", percentageUsed)).append("% of your budget!\n");
        } else if (percentageUsed > 70) {
            response.append("⚠️ Budget Warning: You've used ").append(String.format("%.1f", percentageUsed)).append("% of your budget.\n");
        } else {
            response.append("✅ Budget looks healthy!\n");
        }

        response.append("💰 Total Budget: $").append(String.format("%.2f", totalBudget)).append("\n");
        response.append("💸 Spent: $").append(String.format("%.2f", spentAmount)).append("\n");
        response.append("💳 Remaining: $").append(String.format("%.2f", remainingBudget)).append("\n");
        response.append("📂 Categories: ").append(categories.size()).append("\n");
        response.append("📝 Items: ").append(itemCount);

        return response.toString();
    }

    // Task analysis
    private String analyzeTasks() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            return "Please log in first to check your tasks!";
        }

        if (userTasks == null || userTasks.isEmpty()) {
            return "📝 You don't have any tasks yet. Ready to create some?";
        }

        int totalTasks = userTasks.size();
        int tasksWithDeadlines = 0;
        int tasksWithNotes = 0;

        for (TaskItem task : userTasks) {
            if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
                tasksWithDeadlines++;
            }
            if (task.getNotes() != null && !task.getNotes().isEmpty()) {
                tasksWithNotes++;
            }
        }

        StringBuilder response = new StringBuilder();
        response.append("📋 Task Summary:\n");
        response.append("📊 Total Tasks: ").append(totalTasks).append("\n");
        response.append("⏰ Tasks with Deadlines: ").append(tasksWithDeadlines).append("\n");
        response.append("📝 Tasks with Notes: ").append(tasksWithNotes).append("\n");

        if (tasksWithDeadlines == 0) {
            response.append("💡 Tip: Add deadlines to stay organized!");
        } else {
            response.append("🎯 Great job tracking deadlines!");
        }

        return response.toString();
    }

    // Supplier analysis
    private String analyzeSuppliers() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            return "Please log in first to check your suppliers!";
        }

        if (userSuppliers == null || userSuppliers.isEmpty()) {
            return "🏢 You haven't added any suppliers yet. Want to add some?";
        }

        int totalSuppliers = userSuppliers.size();
        int bookedSuppliers = 0;
        int suppliersWithEmail = 0;
        int suppliersWithUrl = 0;

        for (SupplierItem supplier : userSuppliers) {
            if (supplier.isBooked()) {
                bookedSuppliers++;
            }
            if (supplier.getMail() != null && !supplier.getMail().isEmpty()) {
                suppliersWithEmail++;
            }
            if (supplier.getUrl() != null && !supplier.getUrl().isEmpty()) {
                suppliersWithUrl++;
            }
        }

        StringBuilder response = new StringBuilder();
        response.append("🏢 Supplier Summary:\n");
        response.append("📊 Total Suppliers: ").append(totalSuppliers).append("\n");
        response.append("✅ Booked Suppliers: ").append(bookedSuppliers).append("\n");
        response.append("📧 Suppliers with Email: ").append(suppliersWithEmail).append("\n");
        response.append("🌐 Suppliers with Website: ").append(suppliersWithUrl).append("\n");

        if (bookedSuppliers == 0) {
            response.append("💡 No suppliers booked yet!");
        } else {
            response.append("🎉 You have active supplier bookings!");
        }

        return response.toString();
    }

    // Get random response from list
    private String getRandomResponse(List<String> responses) {
        Random random = new Random();
        return responses.get(random.nextInt(responses.size()));
    }

    // Inner classes to match your data structure
    public static class BudgetItem {
        private double totalBudget;
        private double remainingBudget;
        private String category;
        private String item;

        public BudgetItem(double totalBudget, double remainingBudget, String category, String item) {
            this.totalBudget = totalBudget;
            this.remainingBudget = remainingBudget;
            this.category = category;
            this.item = item;
        }

        // Getters
        public double getTotalBudget() { return totalBudget; }
        public double getRemainingBudget() { return remainingBudget; }
        public String getCategory() { return category; }
        public String getItem() { return item; }

        // Setters
        public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }
        public void setRemainingBudget(double remainingBudget) { this.remainingBudget = remainingBudget; }
        public void setCategory(String category) { this.category = category; }
        public void setItem(String item) { this.item = item; }
    }

    public static class TaskItem {
        private String task;
        private String deadline;
        private String notes;

        public TaskItem(String task, String deadline, String notes) {
            this.task = task;
            this.deadline = deadline;
            this.notes = notes;
        }

        // Getters
        public String getTask() { return task; }
        public String getDeadline() { return deadline; }
        public String getNotes() { return notes; }

        // Setters
        public void setTask(String task) { this.task = task; }
        public void setDeadline(String deadline) { this.deadline = deadline; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class SupplierItem {
        private String name;
        private String note;
        private String number;
        private String address;
        private boolean booked;
        private String mail;
        private String url;

        public SupplierItem(String name, String note, String number, String address,
                            boolean booked, String mail, String url) {
            this.name = name;
            this.note = note;
            this.number = number;
            this.address = address;
            this.booked = booked;
            this.mail = mail;
            this.url = url;
        }

        // Getters
        public String getName() { return name; }
        public String getNote() { return note; }
        public String getNumber() { return number; }
        public String getAddress() { return address; }
        public boolean isBooked() { return booked; }
        public String getMail() { return mail; }
        public String getUrl() { return url; }

        // Setters
        public void setName(String name) { this.name = name; }
        public void setNote(String note) { this.note = note; }
        public void setNumber(String number) { this.number = number; }
        public void setAddress(String address) { this.address = address; }
        public void setBooked(boolean booked) { this.booked = booked; }
        public void setMail(String mail) { this.mail = mail; }
        public void setUrl(String url) { this.url = url; }
    }
}