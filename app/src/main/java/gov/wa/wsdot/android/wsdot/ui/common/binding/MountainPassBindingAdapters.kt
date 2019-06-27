package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass

object MountainPassBindingAdapters {

    // Regular Binding Adapters
    @JvmStatic
    @BindingAdapter("bindPassWeatherSummary")
    fun bindPassWeatherSummary(textView: TextView, pass: MountainPass) {

        if (!TextUtils.isEmpty(pass.weatherCondition)) {
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
}