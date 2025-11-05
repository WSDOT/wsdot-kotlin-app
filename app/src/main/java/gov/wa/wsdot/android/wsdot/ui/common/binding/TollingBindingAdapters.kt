package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateRow
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTable
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollTrip
import gov.wa.wsdot.android.wsdot.model.common.Resource
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import java.util.Calendar

object TollingBindingAdapters {

    // Regular Binding Adapters
    @JvmStatic
    @BindingAdapter("bindTollTrip")
    fun bindTollTrip(textView: TextView, tollTrip: TollTrip?) {
        tollTrip?.let {
            textView.text = String.format("to %s", it.endLocationName)
        }
    }

    @JvmStatic
    @BindingAdapter("bindTollMessage")
    fun bindTollMessage(textView: TextView, tollTable: Resource<TollRateTable>) {
        if (tollTable.data != null){
            if (tollTable.data.message != "") {
                textView.text = tollTable.data.message
                textView.visibility = View.VISIBLE
            }
        } else {
            textView.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("showTrip")
    fun showTrip(view: View, tollTrip: TollTrip?) {
        view.visibility = View.GONE
        tollTrip?.let {
            view.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("bindTripCost")
    fun bindTripCost(view: TextView, tollTrip: TollTrip?) {

        tollTrip?.let {

            view.text = String.format("$%.2f", it.currentRate)
            view.setTextColor(Color.WHITE)

        }
    }

    @JvmStatic
    @BindingAdapter("bindTripColor")
    fun bindTripColor(view: CardView, tollTrip: TollTrip?) {
        view.setBackgroundColor(Color.DKGRAY)

    }

    @JvmStatic
    @BindingAdapter("bindTextColor")

    fun bindTextColor(textView: TextView, tollRateRow: TollRateRow) {
        val context = textView.context
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        textView.setTextColor(ContextCompat.getColor(context, R.color.black))

       if (tollRateRow.startTime != null && tollRateRow.endTime != null){
            if (TimeUtils.isCurrentHour(tollRateRow.startTime, tollRateRow.endTime, Calendar.getInstance())){
                if ((tollRateRow.weekday && !TimeUtils.weekendOrWAC468270071Holiday(Calendar.getInstance()))
                    || (!tollRateRow.weekday && TimeUtils.weekendOrWAC468270071Holiday(Calendar.getInstance()))) {
                        textView.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            }
        }
        else {
           textView.setTextColor(ContextCompat.getColor(context, R.color.black))
       }
        if (isDarkModeOn) {
           textView.setTextColor(ContextCompat.getColor(context, R.color.white))
       }
    }

    @JvmStatic
    @BindingAdapter("showTollSpinner")
    fun showTollSpinner(view: View, tollTable: Resource<TollRateTable>) {

        view.visibility = View.GONE

        if (tollTable.data != null) {
            if (tollTable.data.route == 50978 || tollTable.data.route == 50983) {
                view.visibility = View.VISIBLE
            }
        }
    }
}