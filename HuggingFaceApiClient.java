package com.example.wedwise_java;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HuggingFaceApiClient {
    private static final String TAG = "HuggingFaceApiClient";
    private static final String API_URL = "https://api-inference.huggingface.co/models/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final String apiToken;
    private final String modelId;
    private final OkHttpClient client;

    public HuggingFaceApiClient(String apiToken, String modelId) {
        this.apiToken = apiToken;
        this.modelId = modelId;

        // Configure OkHttpClient with longer timeouts for model inference
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public interface CompletionCallback {
        void onComplete(String response);
        void onError(String error);
    }

    public void generateResponse(String userMessage, CompletionCallback callback) {
        new Thread(() -> {
            try {
                String fullUrl = API_URL + modelId;

                // Create request body
                JSONObject requestBody = new JSONObject();
                try {
                    // Using the correct format for text generation models
                    requestBody.put("inputs", userMessage);
                    // Optional parameters for controlling generation
                    JSONObject parameters = new JSONObject();
                    parameters.put("max_length", 100);
                    parameters.put("temperature", 0.7);
                    requestBody.put("parameters", parameters);
                } catch (JSONException e) {
                    callback.onError("Error creating request: " + e.getMessage());
                    return;
                }

                // Build request
                Request request = new Request.Builder()
                        .url(fullUrl)
                        .addHeader("Authorization", "Bearer " + apiToken)
                        .post(RequestBody.create(requestBody.toString(), JSON))
                        .build();

                // Execute request
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Unexpected response: " + response);
                        return;
                    }

                    String responseBody = response.body() != null ? response.body().string() : "";
                    String generatedText = parseResponse(responseBody);
                    callback.onComplete(generatedText);
                }

            } catch (IOException e) {
                Log.e(TAG, "Network error", e);
                callback.onError("Network error: " + e.getMessage());
            }
        }).start();
    }

    private String parseResponse(String responseBody) {
        try {
            // Parse response according to Hugging Face API format
            // This might need adjustment based on the specific model you're using
            if (responseBody.startsWith("[")) {
                // For some models like GPT-2, response comes as an array
                JSONObject responseJson = new JSONObject(responseBody.substring(1, responseBody.length() - 1));
                return responseJson.getString("generated_text");
            } else {
                JSONObject responseJson = new JSONObject(responseBody);
                return responseJson.getString("generated_text");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing response", e);
            return "I'm having trouble understanding right now. Could you rephrase?";
        }
    }
}