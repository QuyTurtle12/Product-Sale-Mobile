// src/main/java/com/example/product_sale_app/data/ChatRepository.java
package com.example.product_sale_app.repository;

import com.example.product_sale_app.network.service.ChatApiService;
import com.example.product_sale_app.model.chat.SendChatMessageRequestDTO;
import com.example.product_sale_app.model.response.SingleResponseModel;
import com.example.product_sale_app.model.chat.ChatMessageDto;
import com.example.product_sale_app.model.response.BaseResponseModel;

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

    public ChatRepository(ChatApiService api) {
        this.api = api;
    }

    public void loadAllMessages(int userId, CallbackFn<List<ChatMessageDto>> cb) {
        api.getMessages(1, 100, null, userId)
                .enqueue(new Callback<BaseResponseModel<ChatMessageDto>>() {
                    @Override public void onResponse(Call<BaseResponseModel<ChatMessageDto>> call,
                                                     Response<BaseResponseModel<ChatMessageDto>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            cb.onSuccess(resp.body().data.items);
                        } else {
                            cb.onError(new Exception("API error: " + resp.code()));
                        }
                    }
                    @Override public void onFailure(Call<BaseResponseModel<ChatMessageDto>> call, Throwable t) {
                        cb.onError(t);
                    }
                });
    }

    public void loadBoxMessages(int boxId, CallbackFn<List<ChatMessageDto>> cb) {
        api.getBoxMessages(boxId, 1, 20)
                .enqueue(new Callback<BaseResponseModel<ChatMessageDto>>() {
                    @Override public void onResponse(Call<BaseResponseModel<ChatMessageDto>> call,
                                                     Response<BaseResponseModel<ChatMessageDto>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            cb.onSuccess(resp.body().data.items);
                        } else {
                            cb.onError(new Exception("API error: " + resp.code()));
                        }
                    }
                    @Override public void onFailure(Call<BaseResponseModel<ChatMessageDto>> call, Throwable t) {
                        cb.onError(t);
                    }
                });
    }

    public void sendMessage(int boxId, String text, CallbackFn<ChatMessageDto> cb) {
        SendChatMessageRequestDTO dto = new SendChatMessageRequestDTO(boxId, text);
        api.sendMessage(dto)
                .enqueue(new Callback<SingleResponseModel<ChatMessageDto>>() {
                    @Override public void onResponse(Call<SingleResponseModel<ChatMessageDto>> call,
                                                     Response<SingleResponseModel<ChatMessageDto>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            // RESP.data is the single ChatMessageDto
                            cb.onSuccess(resp.body().data);
                        } else {
                            cb.onError(new Exception("API error: " + resp.code()));
                        }
                    }
                    @Override public void onFailure(Call<SingleResponseModel<ChatMessageDto>> call, Throwable t) {
                        cb.onError(t);
                    }
                });
    }
}
