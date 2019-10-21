package gov.wa.wsdot.android.wsdot.model.amtrakCascades

import java.util.*

data class AmtrakStop(
    val arrivalComment: String?,
    val arrivalScheduleType: Date?,
    val arrivalTime: Date?,
    val departureComment: String?,
    val departureScheduleType: String?,
    val departureTime: String?,
    val scheduledArrivalTime: Date?,
    val scheduledDepartureTime: Date?,
    val sortOrder: Int,
    val stationFullName: String,
    val StationName: String,
    val trainMessage: String,
    val trainNumber: Int,
    val tripNumber: Int,
    val updateTime: Date
)