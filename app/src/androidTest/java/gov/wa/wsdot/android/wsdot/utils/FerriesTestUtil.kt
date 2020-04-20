package gov.wa.wsdot.android.wsdot.utils

import gov.wa.wsdot.android.wsdot.db.ferries.*
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
        description = "",
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
        arrivingTime = Date(),
        cacheDate = Date()
    )

    fun createVessel(
        vesselId: Int = 0,
        departingTerminalId: Int = 0,
        arrivingTerminalId: Int = 0,
        departingTime: Date = Date()
    ) = Vessel(
        vesselId = vesselId,
        vesselName = "",
        departingTerminalId = departingTerminalId,
        departingTerminalName = "",
        arrivingTerminalId = arrivingTerminalId,
        arrivingTerminalName = "",

        latitude = 0.0,
        longitude = 0.0,

        speed = 0.0,
        heading = 0.0,

        inService = false,
        atDock = false,

        scheduledDeparture = departingTime,
        leftDock = Date(),
        eta = Date(),
        etaDetails = "",

        localCacheDate = Date(),
        serverCacheDate = Date()
    )

    fun createFerrySpace(
        departingTerminalId: Int = 0,
        arrivingTerminalId: Int = 0,
        departureTime: Date = Date(),
        maxSpaces: Int = 0,
        currentSpaces: Int = 0
    ) = FerrySpace(
        departingTerminalId = departingTerminalId,
        arrivingTerminalId = arrivingTerminalId,
        departureTime = departureTime,
        showDriveUpSpaces = true,
        showResSpaces = false,
        maxSpacesCount = maxSpaces,
        currentSpacesCount = currentSpaces,
        reservableSpacesCount = 0,
        currentSpacesColor = null,
        reservableSpacesColor = null,
        localCacheDate = Date()
    )

}