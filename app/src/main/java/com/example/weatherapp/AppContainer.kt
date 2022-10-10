package com.example.weatherapp

import android.content.Context
import com.example.weatherapp.utils.NetworkHelper
import com.example.weatherapp.weather.WeatherRepository
import com.example.weatherapp.weather.WeatherViewModel
import com.example.weatherapp.weather.api.WeatherApiClient
import com.example.weatherapp.weather.api.WeatherApiService

class AppContainer(private val context: Context) {

    private val weatherApiService: WeatherApiService = WeatherApiClient.WeatherApiService
    private val networkHelper = NetworkHelper(context)

    private val weatherRepository = WeatherRepository(weatherApiService)
    val weatherViewModel = WeatherViewModel(weatherRepository, networkHelper)
}