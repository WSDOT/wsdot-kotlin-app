package gov.wa.wsdot.android.wsdot.ui.common.viewmodel

import java.util.*
import javax.inject.Inject

// ViewModel lets us share the set data from the daypicker fragment with any fragment that wants it
class SharedDateViewModel @Inject constructor() : SharedValueViewModel<Date>() {

    init {

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        // display previous day's ferry schedule if time is between 12:00am-3:00am
        if (hour < 3) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            setValue(calendar.time)
        }
        else {

            // set to current date on start
            setValue(calendar.time)
        }
    }


}