package com.example.product_sale_app.repository;

import android.util.Base64;

import com.example.product_sale_app.model.chat.ChatMessageDto;
import com.example.product_sale_app.model.chat.SendChatMessageRequestDTO;
import com.example.product_sale_app.model.response.BaseResponseModel;
import com.example.product_sale_app.model.response.SingleResponseModel;
import com.example.product_sale_app.network.service.ChatApiService;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    public interface CallbackFn<T> {
        void onSuccess(T result);
        void onError(Throwable t);
    }

    private final ChatApiService api;
    private final String jwtToken;

    public ChatRepository(ChatApiService api, String jwtToken) {
        this.api = api;
        this.jwtToken = jwtToken;
    }

    /**
     * Get the raw JWT token used for API calls (including "Bearer ")
     */
    public String getJwtToken() {
        return jwtToken;
    }

    /**
     * Extracts the 'role' claim from the JWT payload.
     * @return the role string, or null if parsing fails
     */
    public static String extractRoleFromJwt(String jwt) {
        try {
            // remove "Bearer " prefix if present
            String token = jwt.replaceFirst("^Bearer ", "");
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            byte[] decoded = Base64.decode(parts[1], Base64.URL_SAFE);
            String payload = new String(decoded, StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(payload);
            // adjust claim key if your API uses a different URI
            return obj.optString("http://schemas.microsoft.com/ws/2008/06/identity/claims/role", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static int extractUserIdFromJwt(String rawJwt) {
        try {
            // if you stored "Bearer XXX", strip off the prefix
            String token = rawJwt.startsWith("Bearer ")
                    ? rawJwt.substring(7)
                    : rawJwt;
            String[] parts = token.split("\\.");
            byte[] decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE);
            String payload = new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(payload);
            return obj.getInt("sub");       // or getInt("http://…/nameidentifier")
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void loadAllMessages(Integer userId, CallbackFn<List<ChatMessageDto>> cb) {
        api.getMessages(1, 100, null, userId)
                .enqueue(new Callback<BaseResponseModel<ChatMessageDto>>() {
                    @Override
                    public void onResponse(Call<BaseResponseModel<ChatMessageDto>> call,
                                           Response<BaseResponseModel<ChatMessageDto>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            cb.onSuccess(resp.body().data.items);
                        } else {
                            cb.onError(new Exception("API error: " + resp.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponseModel<ChatMessageDto>> call, Throwable t) {
                        cb.onError(t);
                    }
                });
    }

    public void loadBoxMessages(int boxId, CallbackFn<List<ChatMessageDto>> cb) {
        api.getBoxMessages(boxId, 1, 9999)
                .enqueue(new Callback<BaseResponseModel<ChatMessageDto>>() {
                    @Override
                    public void onResponse(Call<BaseResponseModel<ChatMessageDto>> call,
                                           Response<BaseResponseModel<ChatMessageDto>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            cb.onSuccess(resp.body().data.items);
                        } else {
                            cb.onError(new Exception("API error: " + resp.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponseModel<ChatMessageDto>> call, Throwable t) {
                        cb.onError(t);
                    }
                });
    }

    public void sendMessage(int boxId, String text, CallbackFn<ChatMessageDto> cb) {
        SendChatMessageRequestDTO dto = new SendChatMessageRequestDTO(boxId, text);
        api.sendMessage(dto)
                .enqueue(new Callback<SingleResponseModel<ChatMessageDto>>() {
                    @Override
                    public void onResponse(Call<SingleResponseModel<ChatMessageDto>> call,
                                           Response<SingleResponseModel<ChatMessageDto>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            cb.onSuccess(resp.body().data);
                        } else {
                            cb.onError(new Exception("API error: " + resp.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleResponseModel<ChatMessageDto>> call, Throwable t) {
                        cb.onError(t);
                    }
                });
    }
}
