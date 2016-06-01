package com.example.android.sunshine.network;


import com.example.android.sunshine.model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapService {

    @GET("daily?mode=json&units=metric")
    Call<ResponseModel> getDailyForecast(
            @Query("q") String city,
            @Query("cnt") int count,
            @Query("appid") String apiKey
    );
}
