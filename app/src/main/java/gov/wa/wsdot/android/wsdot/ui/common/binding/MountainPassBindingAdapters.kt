package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass


object MountainPassBindingAdapters {

    // Regular Binding Adapters
    @JvmStatic
    @BindingAdapter("bindTemperature")
    fun bindTemperature(textView: TextView, pass: MountainPass?) {

        if (pass != null) {
            if (pass.weatherCondition.isNotEmpty() || pass.forecasts.isNotEmpty()) {
                textView.text =
                    String.format("%d%sF", pass.temperatureInFahrenheit, 0x00B0.toChar())
            } else {
                textView.text = "N/A"
            }
        }

    }


    @JvmStatic
    @BindingAdapter("bindPassWeatherSummary")
    fun bindPassWeatherSummary(textView: TextView, pass: MountainPass) {

        if (pass.weatherCondition.isNotEmpty()) {
            textView.text = pass.weatherCondition.split(".")[0]
            return
        } else {
            if (pass.forecasts.isNotEmpty()) {
                val forecast = pass.forecasts[0]
                textView.text = forecast.forecastText.split(".")[0]
                return
            }
        }
        textView.text = ""
    }

    @JvmStatic
    @BindingAdapter("bindPassWeatherDetails")
    fun bindPassWeatherDetails(textView: TextView, pass: MountainPass?) {

        if (pass != null) {
            if (pass.weatherCondition.isNotEmpty()) {
                textView.text = pass.weatherCondition
                return
            } else {
                if (pass.forecasts.isNotEmpty()) {
                    val forecast = pass.forecasts[0]
                    textView.text = forecast.forecastText
                    return
                }
            }
            textView.text = "N/A"
        }
    }

    @JvmStatic
    @BindingAdapter("bindPassWeatherIcon")
    fun bindPassWeatherIcon(imageView: ImageView, pass: MountainPass) {

        imageView.setImageDrawable(null)
        imageView.visibility = View.GONE

        if (!TextUtils.isEmpty(pass.weatherCondition)) {
            getIconFromForecast(pass.weatherCondition, pass.weatherCondition.split(".")[0])?.let {
                imageView.setImageResource(it)
                imageView.visibility = View.VISIBLE
            }
        } else {
            if (pass.forecasts.isNotEmpty()) {
                val forecast = pass.forecasts[0]
                getIconFromForecast(forecast.day, forecast.forecastText.split(".")[0])?.let {
                    imageView.setImageResource(it)
                    imageView.visibility = View.VISIBLE
                }
            }
        }

    }

    @JvmStatic
    @BindingAdapter("bindPassWeatherIcon")
    fun bindPassWeatherIcon(imageView: ImageView, forecast: MountainPassResponse.PassConditions.PassItem.PassForecast) {
        getIconFromForecast(forecast.day, forecast.forecastText.split(".")[0])?.let {
            imageView.setImageResource(it)
            imageView.visibility = View.VISIBLE
        }
    }

    private fun getIconFromForecast(day: String, forecast: String): Int? {

        val weather_clear = arrayOf("fair", "sunny", "clear")
        val weather_few_clouds = arrayOf("few clouds", "scattered clouds", "scattered clouds", "mostly sunny", "mostly clear")
        val weather_partly_cloudy = arrayOf("partly cloudy", "partly sunny")
        val weather_cloudy = arrayOf("cloudy", "increasing clouds")
        val weather_mostly_cloudy = arrayOf("broken", "mostly cloudy")
        val weather_overcast = arrayOf("overcast")
        val weather_light_rain = arrayOf("light rain", "showers")
        val weather_rain = arrayOf("rain", "heavy rain", "raining")
        val weather_snow = arrayOf("snow", "snowing", "light snow", "heavy snow")
        val weather_fog = arrayOf("fog")
        val weather_sleet = arrayOf("rain snow", "light rain snow", "heavy rain snow", "rain and snow")
        val weather_hail = arrayOf("ice pellets", "light ice pellets", "heavy ice pellets", "hail")
        val weather_thunderstorm = arrayOf("thunderstorm", "thunderstorms")

        if (weather_clear.any{forecast.contains(it, ignoreCase = true)}) {
            return when (day.contains("night", ignoreCase = true)) {
                true -> R.drawable.ic_weather_clear_night
                false -> R.drawable.ic_weather_clear
            }
        }

        if (weather_few_clouds.any{forecast.contains(it, ignoreCase = true)}) {
            return when (day.contains("night", ignoreCase = true)) {
                true -> R.drawable.ic_weather_cloudy_1_night
                false -> R.drawable.ic_weather_cloudy_1
            }
        }

        if (weather_partly_cloudy.any{forecast.contains(it, ignoreCase = true)}) {
            return when (day.contains("night", ignoreCase = true)) {
                true -> R.drawable.ic_weather_cloudy_2_night
                false -> R.drawable.ic_weather_cloudy_2
            }
        }

        if (weather_cloudy.any{forecast.contains(it, ignoreCase = true)}) {
            return when (day.contains("night", ignoreCase = true)) {
                true -> R.drawable.ic_weather_cloudy_3_night
                false -> R.drawable.ic_weather_cloudy_3
            }
        }

        if (weather_mostly_cloudy.any{forecast.contains(it, ignoreCase = true)}) {
            return when (day.contains("night", ignoreCase = true)) {
                true -> R.drawable.ic_weather_cloudy_4_night
                false -> R.drawable.ic_weather_cloudy_4
            }
        }

        if (weather_overcast.any{forecast.contains(it, ignoreCase = true)}) {
            return  R.drawable.ic_weather_overcast
        }

        if (weather_fog.any{forecast.contains(it, ignoreCase = true)}) {
            return when (day.contains("night", ignoreCase = true)) {
                true -> R.drawable.ic_weather_fog_night
                false -> R.drawable.ic_weather_fog
            }
        }

        if (weather_light_rain.any{forecast.contains(it, ignoreCase = true)}) {
            return R.drawable.ic_weather_light_rain
        }

        if (weather_rain.any{forecast.contains(it, ignoreCase = true)}) {
            return R.drawable.ic_weather_shower_3
        }

        if (weather_snow.any{forecast.contains(it, ignoreCase = true)}) {
            return R.drawable.ic_weather_snow_4
        }

        if (weather_sleet.any{forecast.contains(it, ignoreCase = true)}) {
            return R.drawable.ic_weather_sleet
        }

        if (weather_thunderstorm.any{forecast.contains(it, ignoreCase = true)}) {
            return R.drawable.ic_weather_tstorm_3
        }

        return null

    }
}