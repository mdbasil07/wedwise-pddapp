package com.example.wedwise_java;

import android.content.Context;
import android.util.Log;

public class ChatbotManager {
    private static final String TAG = "ChatbotManager";

    // Models
    public static final String MODEL_GPT2_SMALL = "gpt2";
    public static final String MODEL_DISTILGPT2 = "distilgpt2";
    public static final String MODEL_BLOOM_560M = "bigscience/bloom-560m";
    public static final String MODEL_OPT_350M = "facebook/opt-350m";
    public static final String MODEL_T5_SMALL = "t5-small";

    private final HuggingFaceApiClient apiClient;
    private final Context context;

    public interface ResponseCallback {
        void onResponse(String response);
        void onError(String error);
    }

    public ChatbotManager(Context context, String apiToken, String modelId) {
        this.context = context;
        this.apiClient = new HuggingFaceApiClient(apiToken, modelId);
    }

    public void processUserMessage(String userMessage, ResponseCallback callback) {
        // Pre-defined short replies (Solution 1)
        String customReply = getCustomShortReply(userMessage.trim().toLowerCase());
        if (customReply != null) {
            callback.onResponse(customReply);
            return;
        }

        // Prompt tuning for short, clear replies (Solution 2)
        String systemPrompt = "Respond in a short, clear, and polite manner. Avoid long or emotional replies.";
        String fullPrompt = systemPrompt + "\nUser: " + userMessage + "\nBot:";

        apiClient.generateResponse(fullPrompt, new HuggingFaceApiClient.CompletionCallback() {
            @Override
            public void onComplete(String response) {
                String cleanedResponse = cleanResponse(response);
                callback.onResponse(cleanedResponse);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error getting response: " + error);
                callback.onError(error);
            }
        });
    }

    // Manual short replies for known inputs
    private String getCustomShortReply(String message) {
        switch (message) {
            case "hi":
            case "hello":
                return "Hello! How can I help you?";
            case "how are you":
            case "how are you?":
                return "I'm fine, what about you?";
            case "bye":
            case "goodbye":
                return "Goodbye! Have a great day.";
            case "thank you":
            case "thanks":
                return "You're welcome!";
            default:
                return null; // Let the model handle other messages
        }
    }

    private String cleanResponse(String response) {
        // Remove extra formatting
        response = response.trim();
        if (response.startsWith("Bot:")) {
            response = response.substring("Bot:".length()).trim();
        }

        // Limit length to keep it brief
        if (response.length() > 120) {
            response = response.substring(0, 117) + "...";
        }

        return response;
    }
}
