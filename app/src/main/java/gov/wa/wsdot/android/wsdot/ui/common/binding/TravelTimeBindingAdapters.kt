package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.graphics.Color
import android.text.Html
import android.widget.TextView
import androidx.databinding.BindingAdapter
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime


object TravelTimeBindingAdapters {

    @JvmStatic
    @BindingAdapter("bindTravelTimeTitle")
    fun bindTravelTimeTitle(textView: TextView, travelTime: TravelTime?) {

        textView.text = Html.fromHtml("<b>" + travelTime?.let { String.format(travelTime.title) } + "</b>")

    }


    @JvmStatic
    @BindingAdapter("bindTravelTimeRoute")
    fun bindTravelTimeRoute(textView: TextView, travelTime: TravelTime?) {

        val via = travelTime?.via

        textView.text =
            Html.fromHtml("<b>Routes: </b>" + travelTime?.let {
                if (via != null) {
                    String.format(via)
                }
                else {
                    "N/A"
                }
            })

    }

    @JvmStatic
    @BindingAdapter("bindTravelTimeDistance")
    fun bindTravelTimeDistance(textView: TextView, travelTime: TravelTime?) {

        textView.text = Html.fromHtml("<b>Distance: </b>" + travelTime?.let { String.format(travelTime.miles.toString()) + " miles" })

    }

    @JvmStatic
    @BindingAdapter("bindTravelTimeAverageTime")
    fun bindTravelTimeAverageTime(textView: TextView, travelTime: TravelTime?) {

        if (travelTime != null) {
            if (travelTime.avgTime == -1) {
                textView.text = "N/A"
                textView.text = Html.fromHtml("<b>Average Time: </b>" + travelTime.let {
                    "N/A"})
            } else {
                textView.text = Html.fromHtml("<b>Average Time: </b>" + travelTime.let { String.format(travelTime.avgTime.toString()) + " minutes"
                })
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindTravelTimeCurrentTime")
    fun bindTravelTimeCurrentTime(textView: TextView, travelTime: TravelTime?) {

        if (travelTime != null) {
            if (travelTime.currentTime == -1) {
                textView.text = "N/A"
                textView.text = Html.fromHtml("<b>Current Time: </b>" + travelTime.let {
                    "N/A"})
            } else {
                textView.text = Html.fromHtml("<b>Current Time: </b>" + travelTime.let { String.format(travelTime.currentTime.toString())  + " minutes"})
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindTravelTimeHOVCurrentTime")
    fun bindTravelTimeHOVCurrentTime(textView: TextView, travelTime: TravelTime?) {

        if (travelTime != null) {
            if (travelTime.hovCurrentTime == 0) {
                textView.text = "N/A"
                textView.text = Html.fromHtml("<b>HOV Lane Time: </b>" + travelTime.let {
                    "N/A"})
            } else {
                textView.text = Html.fromHtml("<b>HOV Lane Time: </b>" + travelTime.let { String.format(travelTime.currentTime.toString())  + " minutes"})
            }
        }
    }

}