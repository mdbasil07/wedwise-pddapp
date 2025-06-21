package com.example.wedwise_java;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

public class ChatbotActivity extends AppCompatActivity implements GroqApiService.ResponseCallback {

    private RecyclerView recyclerChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageButton btnBack;
    private TextView tvChatDate;
    private GroqApiService groqApiService;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private boolean isWaitingForResponse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        // Initialize views
        initializeViews();

        // Set up RecyclerView
        setupRecyclerView();

        // Set up listeners
        setupListeners();

        // Add welcome message
        addWelcomeMessage();

        // Set current date
        setCurrentDate();
    }

    private void initializeViews() {
        recyclerChat = findViewById(R.id.recycler_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        tvChatDate = findViewById(R.id.tv_chat_date);

        // Initialize Groq API service with context - THIS IS THE KEY CHANGE
        groqApiService = new GroqApiService(this);
        chatMessages = new ArrayList<>();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(chatMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendMessage());

        btnBack.setOnClickListener(v -> onBackPressed());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
        tvChatDate.setText(sdf.format(new Date()));
    }

    private void addWelcomeMessage() {
        String welcomeMessage = "Hello! I'm Nina, your WedWise AI assistant. I can help you with wedding planning, budgets, tasks, and suppliers. How can I assist you today?";
        addBotMessage(welcomeMessage);
    }

    private void sendMessage() {
        if (isWaitingForResponse) {
            Toast.makeText(this, "Please wait for the previous response", Toast.LENGTH_SHORT).show();
            return;
        }

        String userMessage = etMessage.getText().toString().trim();

        if (!userMessage.isEmpty()) {
            // Add user message to chat
            addUserMessage(userMessage);

            // Clear input field
            etMessage.setText("");

            // Show loading state
            setLoadingState(true);

            // Send message to Groq API
            groqApiService.sendMessage(userMessage, this);

            // Scroll to bottom
            scrollToBottom();
        }
    }

    // GroqApiService.ResponseCallback implementation
    @Override
    public void onResponse(String response) {
        setLoadingState(false);
        addBotMessage(response);
        scrollToBottom();
    }

    @Override
    public void onError(String error) {
        setLoadingState(false);
        addBotMessage("I apologize, but I'm having trouble responding right now. " + error);
        scrollToBottom();
    }

    private void setLoadingState(boolean isLoading) {
        isWaitingForResponse = isLoading;
        btnSend.setEnabled(!isLoading);

        if (isLoading) {
            // Optionally show a typing indicator
            addTypingIndicator();
        } else {
            // Remove typing indicator if you added one
            removeTypingIndicator();
        }
    }

    private void addTypingIndicator() {
        // Add a temporary "typing..." message
        ChatMessage typingMessage = new ChatMessage("Nina is typing...", false, System.currentTimeMillis());
        chatMessages.add(typingMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    private void removeTypingIndicator() {
        // Remove the last message if it's a typing indicator
        if (!chatMessages.isEmpty()) {
            ChatMessage lastMessage = chatMessages.get(chatMessages.size() - 1);
            if (lastMessage.getMessage().equals("Nina is typing...")) {
                chatMessages.remove(chatMessages.size() - 1);
                chatAdapter.notifyItemRemoved(chatMessages.size());
            }
        }
    }

    private void addUserMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, System.currentTimeMillis());
        chatMessages.add(chatMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    private void addBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, System.currentTimeMillis());
        chatMessages.add(chatMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    private void scrollToBottom() {
        if (chatMessages.size() > 0) {
            recyclerChat.smoothScrollToPosition(chatMessages.size() - 1);
        }
    }
}