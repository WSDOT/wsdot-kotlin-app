package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.graphics.Color
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime

/**
 * Data Binding adapters specific to the app.
 */
object TrafficBindingAdapters {

    // Regular Binding Adapters
    @JvmStatic
    @BindingAdapter("bindTravelTime")
    fun bindTravelTime(textView: TextView, travelTime: TravelTime) {

        if (travelTime.currentTime < travelTime.avgTime - 1) {
            textView.setTextColor(Color.parseColor("#FFFFFF"))
        } else if (travelTime.currentTime > travelTime.avgTime + 1) {
            textView.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            textView.setTextColor(Color.parseColor("#000000"))
        }

        if (travelTime.currentTime != 0) {
            textView.text = String.format("%s min", travelTime.currentTime)
        } else {
            textView.text = "N/A"
        }
    }

    @JvmStatic
    @BindingAdapter("bindTravelTimeInfo")
    fun bindTravelTimeInfo(textView: TextView, travelTime: TravelTime) {
        if (travelTime.miles != 0f && travelTime.avgTime != 0) {
            textView.text = String.format("%.2f miles / %s min", travelTime.miles, travelTime.avgTime)
        } else {
            textView.text = "Not Available"
        }
    }

    @JvmStatic
    @BindingAdapter("bindTravelTimeColor")
    fun bindTravelTimeColor(cardView: CardView, travelTime: TravelTime) {
        if (travelTime.currentTime < travelTime.avgTime - 1) {
            cardView.setCardBackgroundColor(Color.parseColor("#007B5F"))
        } else if (travelTime.currentTime > travelTime.avgTime + 1) {
            cardView.setCardBackgroundColor(Color.parseColor("#c62828"))
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#eeeeee"))
        }
    }

}
