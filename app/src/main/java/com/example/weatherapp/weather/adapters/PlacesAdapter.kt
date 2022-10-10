package com.example.weatherapp.weather.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemAutocompleteBinding
import com.example.weatherapp.weather.model.PlaceData

class PlacesAdapter(var list: List<PlaceData>, var onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<PlacesAdapter.Vh>() {
    inner class Vh(var itemAutocompleteBinding: ItemAutocompleteBinding) :
        RecyclerView.ViewHolder(itemAutocompleteBinding.root) {

        fun onBind(placeData: PlaceData) {
            itemAutocompleteBinding.textViewPlaceName.text = placeData.name
            itemAutocompleteBinding.textviewRegionCountry.text =
                placeData.region + ", " + placeData.country
            itemAutocompleteBinding.linearPlace.setOnClickListener {
                onItemClickListener.onItemClick(placeData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemAutocompleteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener {
        fun onItemClick(placeData: PlaceData)
    }
}