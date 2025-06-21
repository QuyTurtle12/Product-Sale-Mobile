package com.example.product_sale_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.product_sale_app.model.ApiErrorResponse;
import com.example.product_sale_app.model.login.LoginData;
import com.example.product_sale_app.model.login.LoginRequest;
import com.example.product_sale_app.model.login.LoginResponse;
import com.example.product_sale_app.network.AuthApiService;
import com.example.product_sale_app.network.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsernameOrEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private static final String TAG = "LoginActivity";
    public static final String PREFS_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsernameOrEmail = findViewById(R.id.username_or_email_field);
        editTextPassword = findViewById(R.id.password_field);
        buttonLogin = findViewById(R.id.login_button);
        buttonRegister = findViewById(R.id.sign_up_button);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performLogin() {
        String usernameOrEmail = editTextUsernameOrEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (usernameOrEmail.isEmpty()) {
            editTextUsernameOrEmail.setError("Username or Email is required");
            editTextUsernameOrEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        buttonLogin.setEnabled(false);

        LoginRequest loginRequest = new LoginRequest(usernameOrEmail, password);
        AuthApiService authApiService = RetrofitClient.createService(LoginActivity.this, AuthApiService.class);
        Call<LoginResponse> call = authApiService.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                buttonLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if ("SUCCESS".equals(loginResponse.getCode()) && loginResponse.getData() != null) {
                        LoginData loginData = loginResponse.getData();
                        String token = loginData.getToken();
                        int userId = loginData.getUserId();
                        String role = loginData.getRole();

                        Log.i(TAG, "Login successful! Token: " + token + ", UserID: " + userId + ", Role: " + role );
                        Toast.makeText(LoginActivity.this, "Login Successful: " + loginData.getUsername(), Toast.LENGTH_LONG).show();

                        // Save to SharedPreferences
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("token", token);
                        editor.putInt("userId", userId);
                        editor.putString("role", loginData.getRole());
                        editor.putString("username", loginData.getUsername());
                        editor.apply();

                        // Navigate to HomeActivity
                         Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                         startActivity(intent);
                         finish();

                    } else {
                        String apiErrorMessage = loginResponse.getMessage() != null ? loginResponse.getMessage() : "Login failed. Please try again.";
                        Log.e(TAG, "API Logic Error: " + apiErrorMessage);
                        Toast.makeText(LoginActivity.this, apiErrorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // HTTP error (e.g., 401, 400, 500)
                    String displayErrorMessage = "Login failed. Please try again."; // Default message
                    if (response.errorBody() != null) {
                        try {
                            ApiErrorResponse errorResponse = null;
                            if (RetrofitClient.createService(LoginActivity.this, AuthApiService.class) != null) {
                                Gson gson = new Gson();
                                errorResponse = gson.fromJson(response.errorBody().charStream(), ApiErrorResponse.class);
                            }

                            if (errorResponse != null && errorResponse.getErrorMessage() != null) {
                                displayErrorMessage = errorResponse.getErrorMessage();
                                Log.e(TAG, "API Error Response: Code - " + errorResponse.getErrorCode() + ", Message - " + errorResponse.getErrorMessage());
                            } else {
                                // Fallback if parsing fails or structure doesn't match
                                Log.e(TAG, "HTTP Error Body (unparseable or wrong format): " + response.errorBody().string()); // consume it if not parsed
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    } else {
                        Log.e(TAG, "HTTP Error: No error body. Code: " + response.code() + " " + response.message());
                        displayErrorMessage = "Login failed. Error: " + response.code();
                    }
                    Toast.makeText(LoginActivity.this, displayErrorMessage, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                buttonLogin.setEnabled(true);

                // Handle network failure (e.g., no internet, server down, DNS issues)
                Log.e(TAG, "Network Failure: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
            }
        });
    }
}