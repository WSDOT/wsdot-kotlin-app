package gov.wa.wsdot.android.wsdot.db.ferries

import java.util.*

// data class for retrieving the date range of available sailing days from API data
data class FerryScheduleRange(
    val startDate: Date,
    val endDate: Date
)