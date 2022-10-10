package com.example.weatherapp.weather.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemDailyWeatherBinding
import com.example.weatherapp.utils.Resource
import com.example.weatherapp.weather.model.weatherdata.HourlyForecast
import com.squareup.picasso.Picasso
import java.util.*

class DailyWeatherAdapter(var list: List<HourlyForecast>): RecyclerView.Adapter<DailyWeatherAdapter.Vh>() {

    inner class Vh(var itemDailyWeatherBinding: ItemDailyWeatherBinding) :
        RecyclerView.ViewHolder(itemDailyWeatherBinding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(hourlyForecast: HourlyForecast) {
            val calendar = Calendar.getInstance()
            val hourOfDay = calendar[Calendar.HOUR_OF_DAY].toString()
            if (hourlyForecast.time.substring(11,13) == hourOfDay){
                itemDailyWeatherBinding.linearItem.setBackgroundResource(R.drawable.current_day_background)
            }else{
                itemDailyWeatherBinding.linearItem.setBackgroundResource(R.drawable.daily_weather_background)
            }

            Picasso.get().load("https:"+hourlyForecast.condition.icon?.substring(1)).into(itemDailyWeatherBinding.imageViewIcon)
            itemDailyWeatherBinding.textViewDegree.text = hourlyForecast.temp_c.toInt().toString()+"°C"
            itemDailyWeatherBinding.textViewWind.text = hourlyForecast.wind_kph.toString() + " km\\h"
            itemDailyWeatherBinding.textViewTime.text = hourlyForecast.time.substring(11)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemDailyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
}