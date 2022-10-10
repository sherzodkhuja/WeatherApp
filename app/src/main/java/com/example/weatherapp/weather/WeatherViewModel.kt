package com.example.weatherapp.weather

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.utils.NetworkHelper
import com.example.weatherapp.utils.Resource
import com.example.weatherapp.weather.model.PlaceData
import com.example.weatherapp.weather.model.WeatherForecast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val networkHelper: NetworkHelper
) :
    ViewModel() {

    val hourlyWeather = MutableLiveData<Resource<WeatherForecast>>()
    val weeklyWeather = MutableLiveData<Resource<WeatherForecast>>()
    val places = MutableLiveData<Resource<List<PlaceData>>>()

    fun getHourlyWeather(query: String) {
        if (networkHelper.isNetworkConnected()) {

            viewModelScope.launch {
                hourlyWeather.postValue(Resource.loading(null))

                try {
                    val hourlyWeatherResponse = async { repository.getHourlyWeather(query) }.await()
                    hourlyWeather.postValue(Resource.success(hourlyWeatherResponse))

                } catch (e: Exception) {
                    hourlyWeather.postValue(Resource.error(e.message ?: "Error", null))

                }
            }
        } else {
            hourlyWeather.postValue(Resource.lostInternet(null))
        }
    }

    fun getWeeklyWeather(query: String) {
        if (networkHelper.isNetworkConnected()) {

            viewModelScope.launch {
                weeklyWeather.postValue(Resource.loading(null))

                try {
                    val weeklyWeatherResponse = async { repository.getWeeklyWeather(query) }.await()
                    weeklyWeather.postValue(Resource.success(weeklyWeatherResponse))

                } catch (e: Exception) {
                    weeklyWeather.postValue(Resource.error(e.message ?: "Error", null))

                }
            }
        } else {
            weeklyWeather.postValue(Resource.lostInternet(null))
        }
    }

    fun getPlaces(query: String) {
        if (networkHelper.isNetworkConnected()) {

            viewModelScope.launch {
                places.postValue(Resource.loading(null))

                try {
                    val placesResponse = async { repository.getPlaces(query) }.await()
                    val removeDuplicatePlaces = placesResponse.distinctBy { it.lat }
                    places.postValue(Resource.success(removeDuplicatePlaces) as Resource<List<PlaceData>>?)

                } catch (e: Exception) {
                    places.postValue(Resource.error(e.message ?: "Error", null))

                }
            }
        } else {
            places.postValue(Resource.lostInternet(null))
        }
    }


}