package gov.wa.wsdot.android.wsdot.utils

import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import java.util.*

object TrafficTestUtil {

    fun createCamera(
        id: Int,
        title: String = "",
        roadName: String = "",
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ) = Camera(
        cameraId = id,
        title = title,
        roadName = roadName,
        milepost = 0,
        direction = "",
        latitude = latitude,
        longitude = longitude,
        url = "",
        hasVideo = false,
        localCacheDate = Date(),
        favorite = false,
        remove = false
    )

}