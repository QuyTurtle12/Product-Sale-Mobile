package com.example.product_sale_app.model.chat;

public class SendChatMessageRequestDTO {
    public int chatBoxId;
    public String message;

    public SendChatMessageRequestDTO(int chatBoxId, String message) {
        this.chatBoxId = chatBoxId;
        this.message = message;
    }
}
