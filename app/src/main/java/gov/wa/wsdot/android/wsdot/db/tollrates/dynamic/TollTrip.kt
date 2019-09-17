package gov.wa.wsdot.android.wsdot.db.tollrates.dynamic

import java.util.*

data class TollTrip(
    val tripName: String,
    val endLocationName: String,
    val currentRate: Float,
    val message: String?,
    val endMilepost: Float,
    val endLatitude: Double,
    val endLongitude: Double
)