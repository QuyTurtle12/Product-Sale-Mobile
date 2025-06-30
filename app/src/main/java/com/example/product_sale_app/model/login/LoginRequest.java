package com.example.product_sale_app.model.login;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("usernameOrEmail")
    private String usernameOrEmail;

    @SerializedName("password")
    private String password;

    // Constructor
    public LoginRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    // Getters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
