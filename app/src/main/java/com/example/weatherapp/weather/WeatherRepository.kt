package com.example.weatherapp.weather

import com.example.weatherapp.weather.api.WeatherApiService
import com.example.weatherapp.weather.model.PlaceData
import com.example.weatherapp.weather.model.WeatherForecast

class WeatherRepository(val apiService: WeatherApiService) {

    suspend fun getHourlyWeather(query: String): WeatherForecast {
        return apiService.getHourlyWeather(q = query)
    }

    suspend fun getWeeklyWeather(query: String): WeatherForecast {
        return apiService.getWeeklyWeather(q = query)
    }

    suspend fun getPlaces(query: String): List<PlaceData> {
        return apiService.getPlaces(q = query)
    }

}