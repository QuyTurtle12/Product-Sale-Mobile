package com.example.product_sale_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.product_sale_app.model.ApiErrorResponse;
import com.example.product_sale_app.model.DetailErrorResponse;
import com.example.product_sale_app.model.register.RegisterData;
import com.example.product_sale_app.model.register.RegisterRequest;
import com.example.product_sale_app.model.register.RegisterResponse;
import com.example.product_sale_app.network.AuthApiService;
import com.example.product_sale_app.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextPhoneNumber;
    private EditText editTextAddress;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Your layout file

        editTextUsername = findViewById(R.id.username_register_field);
        editTextEmail = findViewById(R.id.email_register_field);
        editTextPassword = findViewById(R.id.password_register_field);
        editTextConfirmPassword = findViewById(R.id.confirm_password_field);
        editTextPhoneNumber = findViewById(R.id.phone_register_field);
        editTextAddress = findViewById(R.id.address_register_field);
        buttonRegister = findViewById(R.id.register_button);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });
    }

    private void performRegistration() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        // Input Validation
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Username is required");
            editTextUsername.requestFocus();
            return;
        }
        if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email or leave empty");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 8) {
            editTextPassword.setError("Password must be at least 8 characters");
            editTextPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError("Confirm password is required");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
            return;
        }
        buttonRegister.setEnabled(false);

        RegisterRequest registerRequest = new RegisterRequest(username, password, confirmPassword);

        // Send optional fields if inputted
        if (!TextUtils.isEmpty(email)) {
            registerRequest.setEmail(email);
        }
        if (!TextUtils.isEmpty(phoneNumber)) {
            registerRequest.setPhoneNumber(phoneNumber);
        }
        if (!TextUtils.isEmpty(address)) {
            registerRequest.setAddress(address);
        }

            AuthApiService authApiService = RetrofitClient.getApiService();
            Call<RegisterResponse> call = authApiService.registerUser(registerRequest);

            call.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    buttonRegister.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        // Navigate back to LoginActivity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // HTTP error (e.g., 400 Bad Request for validation)
                        String displayErrorMessage = "An unexpected error occurred during registration."; // Default

                        if (response.errorBody() != null) {
                            try {
                                Gson gson = new Gson(); // Standard Gson instance
                                DetailErrorResponse errorDetails = gson.fromJson(
                                        response.errorBody().charStream(), // Use charStream for efficiency
                                        DetailErrorResponse.class);

                                if (errorDetails != null) {
                                    StringBuilder errorsForToast = new StringBuilder();
                                    boolean focused = false; // To focus only the first field with an error

                                    if (errorDetails.getErrors() != null && !errorDetails.getErrors().isEmpty()) {
                                        for (Map.Entry<String, java.util.List<String>> entry : errorDetails.getErrors().entrySet()) {
                                            String fieldName = entry.getKey();
                                            for (String message : entry.getValue()) {
                                                errorsForToast.append(message).append("\n"); // Append all messages for a comprehensive toast

                                                if ("Password".equalsIgnoreCase(fieldName) && editTextPassword != null) {
                                                    editTextPassword.setError(message);
                                                    if (!focused) { editTextPassword.requestFocus(); focused = true; }
                                                } else if ("Username".equalsIgnoreCase(fieldName) && editTextUsername != null) {
                                                    editTextUsername.setError(message);
                                                    if (!focused) { editTextUsername.requestFocus(); focused = true; }
                                                } else if ("Email".equalsIgnoreCase(fieldName) && editTextEmail != null) {
                                                    editTextEmail.setError(message);
                                                    if (!focused) { editTextEmail.requestFocus(); focused = true; }
                                                }
                                                // Add more `else if` blocks for other specific fields if needed
                                            }
                                        }
                                        if (errorsForToast.length() > 0) {
                                            displayErrorMessage = errorsForToast.substring(0, errorsForToast.length() -1); // Remove last newline for Toast
                                        } else if (errorDetails.getTitle() != null) {
                                            displayErrorMessage = errorDetails.getTitle();
                                        }
                                    } else if (errorDetails.getTitle() != null) {
                                        displayErrorMessage = errorDetails.getTitle();
                                    }
                                    Log.e(TAG, "API Validation Error: Status - " + errorDetails.getStatus() +
                                            ", Title - " + errorDetails.getTitle() +
                                            ", Errors - " + (errorDetails.getErrors() != null ? errorDetails.getErrors().toString() : "null"));
                                } else {
                                    Log.e(TAG, "HTTP Error Body (unparseable to ProblemDetails): " + response.code());
                                    displayErrorMessage = "Registration failed (Error " + response.code() + ")";
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing API error response: " + e.getMessage(), e);

                                // Set a user-friendly fallback error message.
                                displayErrorMessage = "Registration failed (Error " + response.code() + ", unable to parse error details)";
                            }
                        } else { // errorBody is null
                            Log.e(TAG, "HTTP Error: No error body. Code: " + response.code() + " " + response.message());
                            displayErrorMessage = "Registration failed (Error " + response.code() + ")";
                        }
                        // This Toast will show the most specific error message derived, or the fallbacks
                        Toast.makeText(RegisterActivity.this, displayErrorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    buttonRegister.setEnabled(true);
                    Log.e(TAG, "Network Failure: " + t.getMessage(), t);
                    Toast.makeText(RegisterActivity.this, "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
                }
            });
    }
}