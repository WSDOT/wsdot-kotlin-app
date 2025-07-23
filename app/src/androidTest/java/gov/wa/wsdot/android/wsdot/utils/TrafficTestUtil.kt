package gov.wa.wsdot.android.wsdot.utils

import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.db.traffic.FavoriteLocation
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.db.travelerinfo.NewsRelease
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

    fun createHighwayAlert(
        id: Int,
        headline: String = "",
        roadName: String = "",
        startLatitude: Double = 0.0,
        startLongitude: Double = 0.0
    ) = HighwayAlert(
        alertId = id,
        headline = headline,
        roadName = roadName,
        priority = "",
        category = "",
        startLatitude = startLatitude,
        startLongitude = startLongitude,
        endLatitude = 0.0,
        endLongitude = 0.0,
        lastUpdatedTime = Date(),
        localCacheDate = Date(),
        travelCenterPriorityId = TODO(),
        eventCategoryType = TODO(),
        eventCategoryTypeDescription = TODO(),
        displayLatitude = TODO(),
        displayLongitude = TODO(),
        direction = TODO()
    )

    fun createFavoriteLocation(
        creationDate: Date = Date(),
        title: String = ""
    ) = FavoriteLocation(
        title = title,
        latitude = 0.0,
        longitude = 0.0,
        zoom = 0f,
        creationDate = creationDate
    )

    fun createNewsRelease(
        link: String = ""
    ) = NewsRelease (
        link = link,
        title = "",
        description = "",
        pubdate = Date()
    )

}