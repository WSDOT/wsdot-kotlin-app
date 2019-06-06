package gov.wa.wsdot.android.wsdot.util

import java.util.*

object TimeUtils {

    fun isOverXMinOld(date: Date, x: Int): Boolean {
        val previous = Calendar.getInstance()
        previous.time = date
        val now = Calendar.getInstance()
        val diff = now.timeInMillis - previous.timeInMillis
        val duration = x * 60 * 1000
        return diff >= duration
    }

}