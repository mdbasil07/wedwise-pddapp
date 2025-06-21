// Enhanced GroqApiService.java - Now includes app data context + static variables
package com.example.wedwise_java;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

public class GroqApiService {
    private static final String TAG = "GroqApiService";
    private static final String API_KEY = "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";
    private static final String BASE_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final OkHttpClient client;
    private final Handler mainHandler;
    private final Context context;

    public interface ResponseCallback {
        void onResponse(String response);
        void onError(String error);
    }

    public GroqApiService(Context context) {
        this.context = context;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    private String getUserDataContext() {
        // Read from both SharedPreferences files you use
        SharedPreferences userSession = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences wedWisePrefs = context.getSharedPreferences("WedWisePrefs", Context.MODE_PRIVATE);

        StringBuilder contextBuilder = new StringBuilder();

        // Debug: Log all stored preferences
        Log.d(TAG, "Reading SharedPreferences from UserSession and WedWisePrefs");

        // Get user profile information from UserSession
        String userName = userSession.getString("profile_name", "");
        String userGender = userSession.getString("profile_gender", "");
        String userEmail = userSession.getString("email", "");

        if (!userName.isEmpty()) {
            contextBuilder.append("User Name: ").append(userName).append(". ");
        }
        if (!userGender.isEmpty()) {
            contextBuilder.append("Gender: ").append(userGender).append(". ");
        }

        // Get budget information - PRIORITIZE STATIC VARIABLES over SharedPreferences
        int totalBudget, spentBudget, remainingBudget;

        // First try to get from static variables (real-time data)
        if (BudgetData.totalBudget > 0) {
            totalBudget = BudgetData.totalBudget;
            spentBudget = BudgetData.spentBudget;
            remainingBudget = BudgetData.remainingBudget;
            Log.d(TAG, "Using static budget data - Total: " + totalBudget + ", Spent: " + spentBudget + ", Remaining: " + remainingBudget);
        } else {
            // Fallback to SharedPreferences if static data is not available
            totalBudget = wedWisePrefs.getInt("totalBudget", 0);
            spentBudget = wedWisePrefs.getInt("spentBudget", 0);
            remainingBudget = totalBudget - spentBudget;
            Log.d(TAG, "Using SharedPreferences budget data - Total: " + totalBudget + ", Spent: " + spentBudget);
        }

        if (totalBudget > 0) {
            contextBuilder.append(String.format("User's Budget: Total: ₹%,d, Spent: ₹%,d, Remaining: ₹%,d. ",
                    totalBudget, spentBudget, remainingBudget));
        }

        // Add detailed budget breakdown from static variables
        String budgetBreakdown = getBudgetCategoriesFromStatic();
        if (!budgetBreakdown.isEmpty()) {
            contextBuilder.append("Budget Categories: ").append(budgetBreakdown).append(". ");
        }

        // Get wedding details from WedWisePrefs (add these keys based on what you actually store)
        String weddingDate = wedWisePrefs.getString("weddingDate", "");
        if (weddingDate.isEmpty()) {
            weddingDate = wedWisePrefs.getString("wedding_date", "");
        }

        if (!weddingDate.isEmpty()) {
            contextBuilder.append("Wedding Date: ").append(weddingDate).append(". ");
        }

        String venue = wedWisePrefs.getString("venue", "");
        if (venue.isEmpty()) {
            venue = wedWisePrefs.getString("wedding_venue", "");
        }

        if (!venue.isEmpty()) {
            contextBuilder.append("Venue: ").append(venue).append(". ");
        }

        int guestCount = wedWisePrefs.getInt("guestCount", 0);
        if (guestCount == 0) {
            guestCount = wedWisePrefs.getInt("guest_count", 0);
        }

        if (guestCount > 0) {
            contextBuilder.append("Guest Count: ").append(guestCount).append(". ");
        }

        // Get bride and groom names
        String brideName = wedWisePrefs.getString("brideName", "");
        String groomName = wedWisePrefs.getString("groomName", "");

        if (!brideName.isEmpty() && !groomName.isEmpty()) {
            contextBuilder.append("Bride: ").append(brideName).append(", Groom: ").append(groomName).append(". ");
        }

        // Add tasks - PRIORITIZE STATIC VARIABLES over SharedPreferences
        String tasks = getTasksFromStatic();
        if (tasks.isEmpty()) {
            // Fallback to SharedPreferences if static data is not available
            tasks = getStoredTasks(wedWisePrefs);
        }
        if (!tasks.isEmpty()) {
            contextBuilder.append("Current Tasks: ").append(tasks).append(". ");
        }

        String suppliers = getSuppliersFromStatic();
        if (suppliers.isEmpty()) {
            // Fallback to SharedPreferences if static data is not available
            suppliers = getStoredSuppliers(wedWisePrefs);
        }
        if (!suppliers.isEmpty()) {
            contextBuilder.append("Current Suppliers: ").append(suppliers).append(". ");
        }

        String result = contextBuilder.toString();
        Log.d(TAG, "User context: " + result);

        return result;
    }

    private String getBudgetCategoriesFromStatic() {
        if (BudgetData.categories == null || BudgetData.categories.isEmpty()) {
            return "";
        }

        StringBuilder categoriesBuilder = new StringBuilder();

        for (int i = 0; i < BudgetData.categories.size(); i++) {
            BudgetData.BudgetCategory category = BudgetData.categories.get(i);

            // Calculate total spent in this category
            int categoryTotal = 0;
            StringBuilder itemsBuilder = new StringBuilder();

            if (category.items != null && !category.items.isEmpty()) {
                for (int j = 0; j < category.items.size(); j++) {
                    BudgetData.BudgetItem item = category.items.get(j);
                    categoryTotal += item.amount;
                    itemsBuilder.append(item.name).append(": ₹").append(String.format("%,d", item.amount));
                    if (j < category.items.size() - 1) {
                        itemsBuilder.append(", ");
                    }
                }
            }

            categoriesBuilder.append(category.name).append(" (₹").append(String.format("%,d", categoryTotal)).append(")");

            if (!itemsBuilder.toString().isEmpty()) {
                categoriesBuilder.append(" [").append(itemsBuilder.toString()).append("]");
            }

            if (i < BudgetData.categories.size() - 1) {
                categoriesBuilder.append(", ");
            }
        }

        Log.d(TAG, "Budget categories from static: " + categoriesBuilder.toString());
        return categoriesBuilder.toString();
    }

    private String getTasksFromStatic() {
        if (TaskManager.getTaskList() == null || TaskManager.getTaskList().isEmpty()) {
            return "";
        }

        StringBuilder tasksBuilder = new StringBuilder();
        List<Task> taskList = TaskManager.getTaskList();

        int completedCount = 0;
        int totalCount = taskList.size();

        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            tasksBuilder.append(task.getTitle());

            if (task.isCompleted()) {
                tasksBuilder.append(" (Completed)");
                completedCount++;
            } else {
                tasksBuilder.append(" (Pending)");
            }

            // Add due date if available
            if (task.getDateTime() != null && !task.getDateTime().isEmpty()) {
                tasksBuilder.append(" - Due: ").append(task.getDateTime());
            }

            // Add status if available and different from completed status
            if (task.getStatus() != null && !task.getStatus().isEmpty()) {
                tasksBuilder.append(" [").append(task.getStatus()).append("]");
            }

            if (i < taskList.size() - 1) {
                tasksBuilder.append(", ");
            }
        }

        // Add summary at the beginning
        String summary = String.format("(%d/%d completed) ", completedCount, totalCount);

        Log.d(TAG, "Tasks from static: " + summary + tasksBuilder.toString());
        return summary + tasksBuilder.toString();
    }

    private String getStoredTasks(SharedPreferences prefs) {
        // Implement this based on how you store tasks
        // This is just an example - adjust based on your actual storage method
        StringBuilder tasks = new StringBuilder();

        // If you store tasks as JSON string
        String tasksJson = prefs.getString("tasks", "");
        if (!tasksJson.isEmpty()) {
            try {
                JSONArray tasksArray = new JSONArray(tasksJson);
                for (int i = 0; i < tasksArray.length(); i++) {
                    JSONObject task = tasksArray.getJSONObject(i);
                    String taskName = task.getString("name");
                    boolean completed = task.getBoolean("completed");
                    tasks.append(taskName).append(completed ? " (Completed)" : " (Pending)");
                    if (i < tasksArray.length() - 1) tasks.append(", ");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing tasks JSON", e);
            }
        }

        return tasks.toString();
    }

    private String getSuppliersFromStatic() {
        if (Supplier.supplierList == null || Supplier.supplierList.isEmpty()) {
            return "";
        }

        StringBuilder suppliersBuilder = new StringBuilder();
        List<Supplier> supplierList = Supplier.supplierList;

        int bookedCount = 0;
        int totalCount = supplierList.size();

        for (int i = 0; i < supplierList.size(); i++) {
            Supplier supplier = supplierList.get(i);
            suppliersBuilder.append(supplier.getName());

            // Add supplier type/note if available
            if (supplier.getNote() != null && !supplier.getNote().isEmpty()) {
                suppliersBuilder.append(" (").append(supplier.getNote()).append(")");
            }

            // Add booking status
            if (supplier.isBooked()) {
                suppliersBuilder.append(" [BOOKED]");
                bookedCount++;
            } else {
                suppliersBuilder.append(" [Available]");
            }

            // Add contact info if available
            if (supplier.getPhone() != null && !supplier.getPhone().isEmpty()) {
                suppliersBuilder.append(" - ").append(supplier.getPhone());
            }

            if (i < supplierList.size() - 1) {
                suppliersBuilder.append(", ");
            }
        }

        // Add summary at the beginning
        String summary = String.format("(%d/%d booked) ", bookedCount, totalCount);

        Log.d(TAG, "Suppliers from static: " + summary + suppliersBuilder.toString());
        return summary + suppliersBuilder.toString();
    }

    private String getStoredSuppliers(SharedPreferences prefs) {
        // Implement this based on how you store suppliers
        StringBuilder suppliers = new StringBuilder();

        String suppliersJson = prefs.getString("suppliers", "");
        if (!suppliersJson.isEmpty()) {
            try {
                JSONArray suppliersArray = new JSONArray(suppliersJson);
                for (int i = 0; i < suppliersArray.length(); i++) {
                    JSONObject supplier = suppliersArray.getJSONObject(i);
                    String name = supplier.getString("name");
                    String type = supplier.getString("type");
                    suppliers.append(name).append(" (").append(type).append(")");
                    if (i < suppliersArray.length() - 1) suppliers.append(", ");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing suppliers JSON", e);
            }
        }

        return suppliers.toString();
    }

    public void sendMessage(String userMessage, ResponseCallback callback) {
        Log.d(TAG, "Sending message: " + userMessage);

        try {
            // Create request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "llama-3.1-8b-instant");

            JSONArray messages = new JSONArray();

            // Get user's current data context
            String userDataContext = getUserDataContext();

            // Enhanced system message with user data
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            String systemContent = "You are Nina, an AI assistant for WedWise, a wedding planning app in India. " +
                    "You help users with wedding budget management, task organization, and supplier coordination. " +
                    "Always be helpful, friendly, and focused on wedding planning topics. " +
                    "Keep responses concise and actionable. " +
                    "IMPORTANT: Always use Indian Rupees (₹) for currency, never dollars ($). " +
                    "All budget amounts are in Indian Rupees.\n\n" +
                    "Current user data: " + userDataContext + "\n" +
                    "Use this information to provide personalized responses about their wedding planning progress. " +
                    "When discussing money, always use ₹ symbol and format for Indian currency. " +
                    "The budget data provided includes real-time spending information with detailed category breakdowns.";
            systemMessage.put("content", systemContent);
            messages.put(systemMessage);

            // User message
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.put(userMsg);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1024);
            requestBody.put("top_p", 1);

            Log.d(TAG, "Request body: " + requestBody.toString());

            // Create request
            RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            // Execute request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Request failed: " + e.getMessage());
                    String errorMessage;
                    if (e.getMessage() != null && e.getMessage().contains("timeout")) {
                        errorMessage = "Request timed out. Please check your internet connection and try again.";
                    } else {
                        errorMessage = "Connection error: " + e.getMessage();
                    }

                    mainHandler.post(() -> callback.onError(errorMessage));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Response status: " + response.code());
                    Log.d(TAG, "Response body: " + responseBody);

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            JSONArray choices = jsonResponse.getJSONArray("choices");

                            if (choices.length() > 0) {
                                JSONObject firstChoice = choices.getJSONObject(0);
                                JSONObject message = firstChoice.getJSONObject("message");
                                String content = message.getString("content");

                                mainHandler.post(() -> callback.onResponse(content));
                            } else {
                                mainHandler.post(() -> callback.onError("I received an unexpected response format. Please try again."));
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            mainHandler.post(() -> callback.onError("Error processing response. Please try again."));
                        }
                    } else {
                        String errorMessage;
                        switch (response.code()) {
                            case 401:
                                errorMessage = "Authentication failed. Please check the API key.";
                                break;
                            case 429:
                                errorMessage = "Rate limit exceeded. Please wait a moment before trying again.";
                                break;
                            default:
                                errorMessage = "Server error (" + response.code() + "): " + responseBody;
                                break;
                        }
                        mainHandler.post(() -> callback.onError(errorMessage));
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "JSON creation error: " + e.getMessage());
            callback.onError("Error creating request. Please try again.");
        }
    }
}