package com.example.weatherapp.weather.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherApiClient {

    const val BASE_URL = "https://api.weatherapi.com/v1/"

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val WeatherApiService = getRetrofit().create(WeatherApiService::class.java)
}