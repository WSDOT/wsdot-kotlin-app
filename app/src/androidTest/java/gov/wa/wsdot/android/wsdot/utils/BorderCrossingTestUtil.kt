package gov.wa.wsdot.android.wsdot.utils

import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing
import java.util.*

object BorderCrossingTestUtil {

    fun createBorderCrossing(id: Int, name: String = "") = BorderCrossing(
        crossingId = id,
        name = name,
        route = 0,
        lane = "",
        direction = "",
        wait = null,
        updated = Date(),
        localCacheDate = Date(),
        favorite = false,
        remove = false
    )
}