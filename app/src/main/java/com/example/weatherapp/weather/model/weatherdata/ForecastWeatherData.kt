package com.example.weatherapp.weather.model.weatherdata

data class ForecastWeatherData(
    val forecastday: List<ForecastDetails>
)

data class ForecastDetails(
    val date: String?,
    val day: Day?,
    val astro : SunsetSunrise,
    val hour: List<HourlyForecast>
)

data class Day(
    val avgtemp_c: Double,
    val mintemp_c: Double,
    val condition: WeatherUI,
)

data class SunsetSunrise(
    val sunrise: String,
    val sunset: String
)

data class HourlyForecast(
    val time: String,
    val temp_c: Double,
    val condition: WeatherUI,
    val wind_kph: Double,
    val humidity: Int,
)
