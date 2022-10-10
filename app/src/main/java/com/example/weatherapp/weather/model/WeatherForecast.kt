package com.example.weatherapp.weather.model

import com.example.weatherapp.weather.model.weatherdata.CurrentWeatherData
import com.example.weatherapp.weather.model.weatherdata.ForecastWeatherData
import com.example.weatherapp.weather.model.weatherdata.LocationData

data class WeatherForecast(
    val location: LocationData,
    val current: CurrentWeatherData,
    val forecast: ForecastWeatherData,
)
