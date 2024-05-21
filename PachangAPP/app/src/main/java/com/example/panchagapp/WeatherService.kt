package com.example.panchagapp

import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") apiKey: String
    ): MainActivity.WeatherData
}
