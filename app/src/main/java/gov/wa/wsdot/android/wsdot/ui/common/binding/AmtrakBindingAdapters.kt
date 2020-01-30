package gov.wa.wsdot.android.wsdot.ui.common.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import gov.wa.wsdot.android.wsdot.api.response.amtrakcascades.AmtrakScheduleResponse
import java.util.*

object AmtrakBindingAdapters {

    @JvmStatic
    @BindingAdapter("bindTrainDepartureStatus")
    fun bindDepartureStatus(view: TextView, departureItem: AmtrakScheduleResponse?) {
        departureItem?.let {
            it.departureScheduleType?.let { type ->
                if (it.departureTime != null && (it.departureTime != it.scheduledDepartureTime)) {
                    view.text = String.format(
                        "%s %s at %s",
                        if (type.equals("Estimated",true)) "Estimated" else "Departed",
                        it.departureComment?.toLowerCase(Locale.ENGLISH),
                        BindingAdapters.getHourString(it.departureTime))
                } else {
                    view.text = String.format(
                        "%s %s",
                        if (type.equals("Estimated",true)) "Estimated" else "Departed",
                        it.departureComment?.toLowerCase(Locale.ENGLISH))
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindTrainArrivalStatus")
    fun bindArrivalStatus(view: TextView, arrivalItem: AmtrakScheduleResponse?) {
        arrivalItem?.let {
            it.arrivalScheduleType?.let { type ->
                if (it.arrivalTime != null && (it.arrivalTime != it.scheduledArrivalTime)) {
                    view.text = String.format(
                        "%s %s at %s",
                        if (type.equals("Estimated",true)) "Estimated" else "Arrived",
                        it.arrivalComment?.toLowerCase(Locale.ENGLISH),
                        BindingAdapters.getHourString(it.arrivalTime))
                } else {
                    view.text = String.format(
                        "%s %s",
                        if (type.equals("Estimated",true)) "Estimated" else "Arrived",
                        it.arrivalComment?.toLowerCase(Locale.ENGLISH))
                }
            }
        }
    }

}