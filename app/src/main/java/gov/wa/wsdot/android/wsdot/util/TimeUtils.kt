package gov.wa.wsdot.android.wsdot.util

import java.util.*
import java.text.ParseException
import java.text.SimpleDateFormat

object TimeUtils {

    fun isOverXMinOld(date: Date, x: Int): Boolean {
        val previous = Calendar.getInstance()
        previous.time = date
        val now = Calendar.getInstance()
        val diff = now.timeInMillis - previous.timeInMillis
        val duration = x * 60 * 1000
        return diff >= duration
    }

    fun isCurrentHour(startHourStr: String, endHourStr: String, cal: Calendar): Boolean {

        val startTime = Calendar.getInstance()

        startTime.set(
            Calendar.HOUR_OF_DAY,
            Integer.valueOf(startHourStr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        )
        startTime.set(
            Calendar.MINUTE,
            Integer.valueOf(startHourStr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
        )

        val endTime = Calendar.getInstance()

        endTime.set(
            Calendar.HOUR_OF_DAY,
            Integer.valueOf(endHourStr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
        )
        endTime.set(
            Calendar.MINUTE,
            Integer.valueOf(endHourStr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
        )

        val currentTime = cal.time

        return currentTime.after(startTime.time) && currentTime.before(endTime.time)

    }


    fun weekendOrWAC468270071Holiday(cal: Calendar): Boolean {
        // check if weekend
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true
        }

        // check if New Year's Day
        if (cal.get(Calendar.MONTH) == Calendar.JANUARY && cal.get(Calendar.DAY_OF_MONTH) == 1) {
            return true
        }

        // check if Christmas
        if (cal.get(Calendar.MONTH) == Calendar.DECEMBER && cal.get(Calendar.DAY_OF_MONTH) == 25) {
            return true
        }

        // check if 4th of July
        if (cal.get(Calendar.MONTH) == Calendar.JULY && cal.get(Calendar.DAY_OF_MONTH) == 4) {
            return true
        }

        // check Thanksgiving (4th Thursday of November)
        if (cal.get(Calendar.MONTH) == Calendar.NOVEMBER
            && cal.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 4
            && cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY
        ) {
            return true
        }

        // check Memorial Day (last Monday of May)
        return if (cal.get(Calendar.MONTH) == Calendar.MAY
            && cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
            && cal.get(Calendar.DAY_OF_MONTH) > 31 - 7) {
            true
            // check Labor Day (1st Monday of September)
        } else cal.get(Calendar.MONTH) == Calendar.SEPTEMBER
                && cal.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 1
                && cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY

    }

    fun currentDateInRange(startDateString: String, endDateString: String, dateFormat: String): Boolean {

        val df = SimpleDateFormat(dateFormat, Locale.US)

        var startDate: Date?
        var endDate: Date?

        try {
            startDate = df.parse(startDateString)
            endDate = df.parse(endDateString)
        } catch (e: ParseException) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = 0
            cal.set(1997, 1, 1, 0, 0, 0)
            startDate = cal.time
            endDate = cal.time
            e.printStackTrace()
        }

        val today = Date()

        return !(today.before(startDate) || today.after(endDate))

    }

}