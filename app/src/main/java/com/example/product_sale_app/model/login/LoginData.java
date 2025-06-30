package com.example.product_sale_app.model.login;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("token")
    private String token;

    @SerializedName("expiresAt")
    private String expiresAt; // Keep as String for now, can be parsed to Date/DateTime later if needed

    @SerializedName("userId")
    private int userId; // Assuming userId is an integer based on the example

    @SerializedName("username")
    private String username;

    @SerializedName("role")
    private String role;

    // Default constructor (for Gson)
    public LoginData() {
    }

    // Getters
    public String getToken() {
        return token;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
