package gov.wa.wsdot.android.wsdot.ui.amtrakcascades.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import gov.wa.wsdot.android.wsdot.api.response.amtrakcascades.AmtrakScheduleResponse
import gov.wa.wsdot.android.wsdot.ui.common.binding.BindingAdapters

/**
 * Class that holds Amtrak specific functions for data binding.
 */
object AmtrakCascadesBindingAdapters {

    /**
     *  Given a departure item displays first available from:
     *  1. actual arrival
     *  2. schedule arrival
     *  3. actual departure
     *  4. scheduled departure
     *
     *  This handles unique case where no arrival time is listen, and instead
     *  a departure time is given in it's place. Can happen when trains make quick
     *  stops and aren't given arrival times at a destination.
     */
    @JvmStatic
    @BindingAdapter("bindDepartureDateHour")
    fun bindDepartureDateHour(textView: TextView, departureItem: AmtrakScheduleResponse?) {
        departureItem?.let {
            it.arrivalTime?.let { date ->
                textView.text =
                    BindingAdapters.getHourString(date)
            }
            it.scheduledArrivalTime?.let { date ->
                textView.text =
                    BindingAdapters.getHourString(date)
            }
            it.departureTime?.let { date ->
                textView.text =
                    BindingAdapters.getHourString(date)
            }
            it.scheduledDepartureTime?.let { date ->
                textView.text =
                    BindingAdapters.getHourString(date)
            }
        }
    }

    /**
     *  displays an arrival time for train schedules, handles similar
     *  edge case as the binding method for departure times.
     */
    @JvmStatic
    @BindingAdapter("bindArrivalDateHour")
    fun bindArrivalDateHour(textView: TextView, arrivalItem: AmtrakScheduleResponse?) {

        arrivalItem?.let {
            it.departureTime?.let { date ->
                textView.text =
                    BindingAdapters.getHourString(date)
            }
            it.scheduledDepartureTime?.let { date ->
                textView.text =
                    BindingAdapters.getHourString(date)
            }
            it.arrivalTime?.let { date ->
                textView.text =
                    BindingAdapters.getHourString(date)
            }
            it.scheduledArrivalTime?.let { date ->
                textView.text =
                    BindingAdapters.getHourString(date)
            }
        }
    }

    /**
     * Given a station code returns a display name for the station
     */
    @JvmStatic
    @BindingAdapter("bindTrainStationName")
    fun bindTrainStationName(textView: TextView, station: String?) {
        textView.text = when(station) {
            "VAC" -> "Vancouver, BC"
            "BEL" -> "Bellingham, WA"
            "MVW" -> "Mount Vernon, WA"
            "STW" -> "Stanwood, WA"
            "EVR" -> "Everett, WA"
            "EDM" -> "Edmonds, WA"
            "SEA" -> "Seattle, WA"
            "TUK" -> "Tukwila, WA"
            "TAC" -> "Tacoma, WA"
            "OLW" -> "Olympia/Lacey, WA"
            "CTL" -> "Centralia, WA"
            "KEL" -> "Kelso/Longview, WA"
            "VAN" -> "Vancouver, WA"
            "PDX" -> "Portland, OR"
            "ORC" -> "Oregon City, OR"
            "SLM" -> "Salem, OR"
            "ALY" -> "Albany, OR"
            "EUG" -> "Eugene, OR"
            else -> station
        }
    }

    /**
     *  Maps train/bus numbers to display name strings
     */
    @JvmStatic
    @BindingAdapter("bindTrainNumber")
    fun bindTrainNumber(textView: TextView, trainNumber: Int) {
        textView.text = when(trainNumber) {
            7 -> "7 Empire Builder Train"
            8 -> "8 Empire Builder Train"
            11 -> "11 Coast Starlight Train"
            14 -> "14 Coast Starlight Train"
            27 -> "27 Empire Builder Train"
            28 -> "28 Empire Builder Train"
            500 -> "500 Amtrak Cascades Train"
            501 -> "501 Amtrak Cascades Train"
            502 -> "502 Amtrak Cascades Train"
            503 -> "503 Amtrak Cascades Train"
            504 -> "504 Amtrak Cascades Train"
            505 -> "505 Amtrak Cascades Train"
            506 -> "506 Amtrak Cascades Train"
            507 -> "507 Amtrak Cascades Train"
            508 -> "508 Amtrak Cascades Train"
            509 -> "509 Amtrak Cascades Train"
            510 -> "510 Amtrak Cascades Train"
            511 -> "511 Amtrak Cascades Train"
            512 -> "512 Amtrak Cascades Train"
            513 -> "513 Amtrak Cascades Train"
            516 -> "516 Amtrak Cascades Train"
            517 -> "517 Amtrak Cascades Train"
            518 -> "518 Amtrak Cascades Train"
            519 -> "519 Amtrak Cascades Train"
            else -> String.format("%d Bus Service", trainNumber)
        }
    }
}