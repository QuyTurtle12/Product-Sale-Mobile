package com.example.product_sale_app.network.service;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import com.example.product_sale_app.model.BaseResponseModel;
import com.example.product_sale_app.model.StoreLocationDto;
public interface StoreApiService {
    @GET("api/StoreLocation")
    Call<BaseResponseModel<List<StoreLocationDto>>> getAllLocations();
}
