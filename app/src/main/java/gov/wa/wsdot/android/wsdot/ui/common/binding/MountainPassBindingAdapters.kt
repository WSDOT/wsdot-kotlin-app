package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.text.Html
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
            if (pass.temperatureInFahrenheit != 0) {
                textView.text =
                    Html.fromHtml("<b>Temperature: </b>" + String.format("%d%sF", pass.temperatureInFahrenheit, 0x00B0.toChar()))
            } else {
                textView.text =
                    Html.fromHtml("<b>Temperature: </b>" + "N/A")

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
    @BindingAdapter("bindPassRestrictionsOne")
    fun bindPassRestrictionsOne(textView: TextView, pass: MountainPass?) {

        if (pass != null) {
            textView.text =
                Html.fromHtml("<b>" + "Travel " + pass.restrictionOneDirection + ": </b>" + pass.restrictionOneText)
        } else {
            textView.text =
                Html.fromHtml("<b>Travel: </b>" + "N/A")
        }

        // Check if advisory is active
        if (pass != null) {
            if (!pass.travelAdvisoryActive) {
                textView.visibility = View.GONE
            }
            else {
                textView.visibility = View.VISIBLE
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindPassRestrictionsTwo")
    fun bindPassRestrictionsTwo(textView: TextView, pass: MountainPass?) {

        if (pass != null) {
            textView.text =
                Html.fromHtml("<b>" + "Travel " + pass.restrictionTwoDirection + ": </b>" + pass.restrictionTwoText)
        } else {
            textView.text =
                Html.fromHtml("<b>Travel: </b>" + "N/A")
        }

        // Check if advisory is active
        if (pass != null) {
            if (!pass.travelAdvisoryActive) {
                textView.visibility = View.GONE
            }
            else {
                textView.visibility = View.VISIBLE
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindPassElevation")
    fun bindPassElevation(textView: TextView, pass: MountainPass?) {

        if (pass != null) {
            textView.text =
                Html.fromHtml("<b>" + "Elevation: </b>" + pass.elevationInFeet + " ft")
        } else {
            textView.text =
                Html.fromHtml("<b>Elevation: </b>" + "N/A")
        }
    }

    @JvmStatic
    @BindingAdapter("bindPassConditions")
    fun bindPassConditions(textView: TextView, pass: MountainPass?) {

        if (pass != null) {
            if (pass.roadCondition.isNotEmpty()) {
                textView.text =
                    Html.fromHtml("<b>" + "Conditions: </b>" + pass.roadCondition)
            }
        } else {
            textView.text =
                Html.fromHtml("<b>Conditions: </b>" + "N/A")
        }
    }


    @JvmStatic
    @BindingAdapter("bindPassWeatherDetails")
    fun bindPassWeatherDetails(textView: TextView, pass: MountainPass?) {

        if (pass != null) {
            if (pass.weatherCondition.isNotEmpty()) {
                textView.text = Html.fromHtml("<b>Weather: </b>" + pass.weatherCondition)

                return
            } else {
                if (pass.forecasts.isNotEmpty()) {
                    val forecast = pass.forecasts[0]
                    textView.text = Html.fromHtml("<b>Weather: </b>" + forecast.forecastText)
                    return
                }
            }
            textView.text = Html.fromHtml("<b>Weather: </b>" + "N/A")
        }
    }

    @JvmStatic
    @BindingAdapter("bindPassWeatherIcon")
    fun bindPassWeatherIcon(imageView: ImageView, pass: MountainPass) {

        imageView.setImageDrawable(null)
        imageView.visibility = View.GONE

            if (!TextUtils.isEmpty(pass.weatherCondition) && !pass.weatherCondition.contains("No current information available")) {
            getIconFromForecast("day", pass.weatherCondition.split(".")[0])?.let {
                imageView.setImageResource(it)
                imageView.visibility = View.VISIBLE
            }

        } else {
            if (pass.forecasts.isNotEmpty()) {
                val forecast = pass.forecasts[0]
                getIconFromForecast("day", forecast.forecastText.split(".")[0])?.let {
                    imageView.setImageResource(it)
                    imageView.visibility = View.VISIBLE
                }

                if (getIconFromForecast("day", forecast.forecastText.split(".")[0]) == null)
                    getIconFromForecast("day", forecast.forecastText.split(". ")[1])?.let {
                        imageView.setImageResource(it)
                        imageView.visibility = View.VISIBLE
                        println(forecast.forecastText.split(". ")[1])

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

            if (getIconFromForecast(forecast.day, forecast.forecastText.split(".")[0]) == null)
                getIconFromForecast(forecast.day, forecast.forecastText.split(". ")[1])?.let {
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