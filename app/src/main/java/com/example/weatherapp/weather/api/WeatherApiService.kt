package com.example.weatherapp.weather.api

import com.example.weatherapp.weather.model.PlaceData
import com.example.weatherapp.weather.model.WeatherForecast
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("forecast.json")
    suspend fun getHourlyWeather(
        @Query("key") key: String = "023bd6928bb44994a32133307220810",
        @Query("q") q: String = ""
    ): WeatherForecast

    @GET("forecast.json")
    suspend fun getWeeklyWeather(
        @Query("key") key: String = "023bd6928bb44994a32133307220810",
        @Query("q") q: String = "",
        @Query("days") days: Int = 7,
    ): WeatherForecast

    @GET("search.json")
    suspend fun getPlaces(
        @Query("key") key: String = "023bd6928bb44994a32133307220810",
        @Query("q") q: String
    ): List<PlaceData>

}