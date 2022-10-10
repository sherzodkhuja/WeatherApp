package com.example.weatherapp.weather.model.weatherdata

data class CurrentWeatherData(
    val temp_c: Double?,
    val is_day: Int?,
    val condition: WeatherUI,
    val wind_kph: Double,
    val humidity: Int,
)

data class WeatherUI(
    val text: String?,
    val icon: String?
)