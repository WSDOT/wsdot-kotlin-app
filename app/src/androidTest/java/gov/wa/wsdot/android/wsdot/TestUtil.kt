package gov.wa.wsdot.android.wsdot

import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import java.util.*

object TestUtil {

    fun createFerrySchedule(id: Int, desc: String) = FerrySchedule(
        routeId = id,
        description = desc,
        crossingTime= 0,
        serverCacheDate = Date(),
        localCacheDate = Date(),
        favorite = false,
        remove = false
    )
}