package gov.wa.wsdot.android.wsdot.utils

import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import java.util.*

object TravelTimeTestUtil {

    fun createTravelTime(id: Int, title: String = "") = TravelTime(
        travelTimeId = id,
        title = title,
        via = "",
        status = "",
        avgTime = 0,
        currentTime = 0,
        miles = 0f,
        startLocationLatitude = 0.0,
        startLocationLongitude = 0.0,
        endLocationLatitude = 0.0,
        endLocationLongitude = 0.0,
        updated = Date(),
        localCacheDate = Date(),
        favorite = false,
        remove = false,
        hovCurrentTime = TODO()
    )

}