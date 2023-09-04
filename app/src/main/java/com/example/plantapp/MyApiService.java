package com.example.plantapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MyApiService {
    @GET("v1/entities:search")
    Call<MyResponse> getPlantInfo(@Query("query") String plantName, @Query("key") String apiKey);
}
