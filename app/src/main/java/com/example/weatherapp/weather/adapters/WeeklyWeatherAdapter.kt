package com.example.weatherapp.weather.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemDailyWeatherBinding
import com.example.weatherapp.databinding.ItemWeeklyWeatherBinding
import com.example.weatherapp.weather.model.weatherdata.ForecastDetails
import com.example.weatherapp.weather.model.weatherdata.HourlyForecast
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WeeklyWeatherAdapter(var list: List<ForecastDetails>): RecyclerView.Adapter<WeeklyWeatherAdapter.Vh>() {

    inner class Vh(var itemWeeklyWeatherBinding: ItemWeeklyWeatherBinding) :
        RecyclerView.ViewHolder(itemWeeklyWeatherBinding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(forecastDetails: ForecastDetails) {
            Picasso.get().load("https:"+forecastDetails.day?.condition?.icon?.substring(1)).into(itemWeeklyWeatherBinding.imageViewIcon)
            itemWeeklyWeatherBinding.textViewDescription.text = forecastDetails.day?.condition?.text
            itemWeeklyWeatherBinding.textViewAvgC.text = forecastDetails.day?.avgtemp_c?.toInt().toString()+"°"
            itemWeeklyWeatherBinding.textViewMinC.text = forecastDetails.day?.mintemp_c?.toInt().toString()+"°"

            val calendar = Calendar.getInstance().time
            val currentDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val currentDate = currentDateFormat.format(calendar)
            if (forecastDetails.date == currentDate){
                itemWeeklyWeatherBinding.linearItem.setBackgroundResource(R.drawable.current_week_background)
                itemWeeklyWeatherBinding.linearLine.visibility = View.GONE
            }else{
                itemWeeklyWeatherBinding.linearLine.visibility = View.VISIBLE
            }

            var date = forecastDetails.date
            var spf = SimpleDateFormat("yyyy-MM-dd")
            val newDate = spf.parse(date)
            spf = SimpleDateFormat("MMM dd", Locale.ENGLISH)
            date = spf.format(newDate)

            itemWeeklyWeatherBinding.textViewDate.text = date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemWeeklyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size
}