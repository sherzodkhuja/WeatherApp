package com.example.weatherapp.weather

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.App
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityWeatherBinding
import com.example.weatherapp.utils.Status
import com.example.weatherapp.weather.adapters.DailyWeatherAdapter
import com.example.weatherapp.weather.adapters.PlacesAdapter
import com.example.weatherapp.weather.adapters.WeeklyWeatherAdapter
import com.example.weatherapp.weather.model.PlaceData
import com.example.weatherapp.weather.model.WeatherForecast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import java.util.*


class WeatherActivity : AppCompatActivity() {

    private lateinit var weatherViewModel: WeatherViewModel

    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private var LocationPermissionsGranted = false
    private val LOCATION_PERMISSION_REQUEST_CODE = 1234

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: LatLng? = null
    private var currentLocation: LatLng? = null

    lateinit var dailyWeatherAdapter: DailyWeatherAdapter
    lateinit var weeklyWeatherAdapter: WeeklyWeatherAdapter
    lateinit var placesAdapter: PlacesAdapter

    var isToday: Boolean = false

    lateinit var binding: ActivityWeatherBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appContainer = (application as App).appContainer
        weatherViewModel = appContainer.weatherViewModel

        getLocationPermission()
        setupObservers()

        binding.textViewSevenDays.setOnClickListener {
            if (isToday) {
                setupWeekly()
            }
        }

        binding.textViewToday.setOnClickListener {
            if (!isToday) {
                setupDaily()
            }
        }

        binding.linearCurrentLocation.setOnClickListener {
            if (userLocation != null) {
                currentLocation = LatLng(userLocation!!.latitude, userLocation!!.longitude)
                binding.editTextSearch.hint = getString(R.string.search_hint)
                binding.imageViewSearchIcon.visibility = View.VISIBLE
                setupDaily()
            }
        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (query?.isNotEmpty() == true) {
                    weatherViewModel.getPlaces(query.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun setupObservers() {
        weatherViewModel.hourlyWeather.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val weatherForecast = it.data
                    if (weatherForecast != null) {
                        setupUi(weatherForecast)
                        weatherForecast?.forecast?.forecastday?.get(0)?.hour.let { dailyList ->
                            val calendar = Calendar.getInstance()
                            val hourOfDay = calendar[Calendar.HOUR_OF_DAY]

                            dailyWeatherAdapter = DailyWeatherAdapter(dailyList)
                            val layoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                            layoutManager.scrollToPosition(hourOfDay)
                            binding.rv.layoutManager = layoutManager

                            binding.rv.adapter = dailyWeatherAdapter
                        }
                    }
                }
                Status.LOST_INTERNET -> {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR ->{
                    Toast.makeText(this, "Something went wrong!!!", Toast.LENGTH_SHORT).show()
                }
            }
        })

        weatherViewModel.weeklyWeather.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val weatherForecast = it.data
                    if (weatherForecast != null) {
                        setupUi(weatherForecast)
                        weatherForecast?.forecast?.forecastday?.let { weeklyList ->
                            weeklyWeatherAdapter = WeeklyWeatherAdapter(weeklyList)
                        }
                        val layoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.rv.layoutManager = layoutManager
                        binding.rv.adapter = weeklyWeatherAdapter
                    }
                }
                Status.LOST_INTERNET -> {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR ->{
                    Toast.makeText(this, "Something went wrong!!!", Toast.LENGTH_SHORT).show()
                }
            }
        })

        weatherViewModel.places.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val listPlaces = it.data
                    if (listPlaces?.isNotEmpty() == true) {
                        binding.rvPlaces.visibility = View.VISIBLE
                        placesAdapter = PlacesAdapter(
                            listPlaces ?: emptyList(),
                            object : PlacesAdapter.OnItemClickListener {
                                override fun onItemClick(placeData: PlaceData) {

                                    binding.rvPlaces.visibility = View.GONE
                                    currentLocation = LatLng(placeData.lat!!, placeData.lon!!)

                                    val placeName =
                                        placeData.name + " (${placeData.region}, ${placeData.country})"

                                    if (placeName.length > 26) {
                                        binding.imageViewSearchIcon.visibility = View.GONE
                                    } else {
                                        binding.imageViewSearchIcon.visibility = View.VISIBLE
                                    }

                                    if (isToday) {
                                        setupDaily()
                                    } else {
                                        setupWeekly()
                                    }

                                    hideSoftKeyboard()
                                    binding.editTextSearch.text.clear()
                                    binding.editTextSearch.hint = placeName
                                }
                            })
                        binding.rvPlaces.adapter = placesAdapter
                    } else {
                        binding.rvPlaces.visibility = View.GONE
                    }
                }
                Status.LOST_INTERNET -> {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR ->{
                    Toast.makeText(this, "Something went wrong!!!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupUi(weatherForecast: WeatherForecast) {
        binding.apply {
            textViewLocationName.text = weatherForecast?.location?.name

            textviewRegionCountry.text =
                "${weatherForecast?.location?.region}, ${weatherForecast?.location?.country}"

            Picasso.get().load("https:" + weatherForecast?.current?.condition?.icon)
                .into(binding.imageViewWeatherIcon)

            textViewWeatherCelsius.text =
                weatherForecast?.current?.temp_c?.toInt().toString() + "°C"

            textViewWeatherDescription.text =
                weatherForecast?.current?.condition?.text.toString()

            textViewWind.text =
                weatherForecast?.current?.wind_kph?.toInt().toString() + " km/h"

            textViewHumidity.text = weatherForecast?.current?.humidity.toString() + "%"

            textViewSunrise.text =
                "sunrise\n" + weatherForecast?.forecast?.forecastday?.get(0)?.astro?.sunrise

            textViewSunset.text =
                "sunset\n" + weatherForecast?.forecast?.forecastday?.get(0)?.astro?.sunset
        }
    }

    private fun setupDaily() {
        isToday = true
        binding.shadowView.visibility = View.GONE
        binding.rv.adapter = null
        binding.textViewToday.textSize = 21F
        binding.textViewSevenDays.textSize = 18F
        binding.textViewToday.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.textViewSevenDays.setTextColor(ContextCompat.getColor(this, R.color.grey))

        weatherViewModel.getHourlyWeather("${currentLocation?.latitude},${currentLocation?.longitude}")

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.layoutManager = layoutManager
    }

    private fun setupWeekly() {
        isToday = false
        binding.shadowView.visibility = View.VISIBLE
        binding.rv.adapter = null
        binding.textViewSevenDays.textSize = 21F
        binding.textViewToday.textSize = 18F
        binding.textViewSevenDays.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.textViewToday.setTextColor(ContextCompat.getColor(this, R.color.grey))

        weatherViewModel.getWeeklyWeather("${currentLocation?.latitude},${currentLocation?.longitude}")

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rv.layoutManager = layoutManager
    }

    private fun getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (LocationPermissionsGranted) {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            userLocation = LatLng(location.latitude, location.longitude)
                            if (currentLocation == null) {
                                currentLocation = LatLng(location.latitude, location.longitude)
                            } else {
                                hideSoftKeyboard()
                            }
                            weatherViewModel.getHourlyWeather("${currentLocation?.latitude},${currentLocation?.longitude}")
                        } else {
                            alertMessageNoGpsOrNoPermission(R.string.request_gps)
                        }
                    }
            } catch (e: SecurityException) {
            }
        } else {
            getLocationPermission()
        }
    }

    private fun hideSoftKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)
    }

    private fun alertMessageNoGpsOrNoPermission(type: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        when (type) {

            R.string.request_permission -> {
                builder.setMessage(getString(R.string.enable_permission))
            }
            R.string.request_gps -> {
                builder.setMessage(R.string.enable_gps)
            }
        }
        builder.setCancelable(false)
            .setPositiveButton(
                getString(R.string.ok)
            ) { dialog, id ->

                when (type) {
                    R.string.request_permission -> {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                            )
                        )
                    }
                    R.string.request_gps -> {
                        startActivity(
                            Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS
                            )
                        )
                    }
                }
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun getLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this.applicationContext,
                COURSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            LocationPermissionsGranted = true
            isToday = true
            getCurrentLocation()

        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        LocationPermissionsGranted = false
        isToday = false

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    for (i in grantResults.indices) {

                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            alertMessageNoGpsOrNoPermission(R.string.request_permission)
                            LocationPermissionsGranted = false
                            isToday = false
                            return
                        }
                    }
                    LocationPermissionsGranted = true
                    isToday = true
                    getCurrentLocation()
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this.applicationContext,
                COURSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            LocationPermissionsGranted = true
            isToday = true
            getCurrentLocation()

        } else {
            alertMessageNoGpsOrNoPermission(R.string.request_permission)
        }
        setupDaily()
    }

}