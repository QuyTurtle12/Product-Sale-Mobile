package com.example.product_sale_app.data;

import com.google.gson.annotations.SerializedName;

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
}
