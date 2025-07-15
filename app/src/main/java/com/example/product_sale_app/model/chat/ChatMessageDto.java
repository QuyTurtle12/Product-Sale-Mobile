package com.example.product_sale_app.model.chat;

import com.google.gson.annotations.SerializedName;

/**
 * Data Transfer Object for chat messages.
 * Supports both field access for adapters and getters for logic.
 */
public class ChatMessageDto {
    @SerializedName("chatMessageId")
    public int id;

    @SerializedName("userId")
    public Integer userId;

    @SerializedName("username")
    public String username;

    @SerializedName("message")
    public String text;

    @SerializedName("sentAt")
    public String sentAt;

    @SerializedName("chatBoxId")
    public int boxId;

    // Getters for logic and readability

    public int getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    /**
     * @return the message text
     */
    public String getText() {
        return text;
    }

    /**
     * ISO timestamp when the message was sent (e.g. "2025-07-15T13:45:30Z").
     */
    public String getSentAt() {
        return sentAt;
    }

    /**
     * @return the chat box ID this message belongs to
     */
    public int getChatBoxId() {
        return boxId;
    }

    @Override
    public String toString() {
        return "ChatMessageDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", text='" + text + '\'' +
                ", sentAt='" + sentAt + '\'' +
                ", boxId=" + boxId +
                '}';
    }
}
