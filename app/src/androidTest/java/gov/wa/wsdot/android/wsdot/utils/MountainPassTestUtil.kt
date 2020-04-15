package gov.wa.wsdot.android.wsdot.utils

import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import java.util.*

object MountainPassTestUtil {

    fun createMountainPass(id: Int, passName: String = "") = MountainPass(
        passId = id,
        passName = passName,
        roadCondition = "",
        weatherCondition = "",
        temperatureInFahrenheit = 0,
        elevationInFeet = 0,
        travelAdvisoryActive = false,
        latitude = 0.0,
        longitude = 0.0,
        restrictionOneText = "",
        restrictionOneDirection = "",
        restrictionTwoText = "",
        restrictionTwoDirection = "",
        serverCacheDate = Date(),
        localCacheDate = Date(),
        cameras = listOf(),
        forecasts = listOf(),
        favorite = false,
        remove = false
    )

}