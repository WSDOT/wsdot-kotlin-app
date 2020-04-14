package gov.wa.wsdot.android.wsdot

import gov.wa.wsdot.android.wsdot.db.ferries.FerryAlert
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailing
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import java.util.*

object FerriesTestUtil {

    fun createFerrySchedule(id: Int, desc: String) = FerrySchedule(
        routeId = id,
        description = desc,
        crossingTime= 0,
        serverCacheDate = Date(),
        localCacheDate = Date(),
        favorite = false,
        remove = false
    )

    fun createFerryAlerts(id: Int, routeId: Int) = FerryAlert(
        alertId = id,
        title = "",
        route = routeId,
        description = null,
        fullDescription = "",
        publishDate = Date()
    )

    fun createFerrySailing(
        route: Int = 0,
        sailingDate: Date = Date(),
        departingTerminalId: Int = 0,
        arrivingTerminalId: Int = 0,
        departingTime: Date = Date()
    ) = FerrySailing(
        route = route,
        sailingDate = sailingDate,
        departingTerminalId = departingTerminalId,
        departingTerminalName ="",
        arrivingTerminalId = arrivingTerminalId,
        arrivingTerminalName = "",
        annotations = listOf("test1", "test2"),
        departingTime = departingTime,
        arrivingTime = null,
        cacheDate = Date()
    )

}