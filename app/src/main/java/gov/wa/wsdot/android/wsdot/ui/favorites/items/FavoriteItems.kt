package gov.wa.wsdot.android.wsdot.ui.favorites.items

import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.db.traffic.FavoriteLocation
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime

/**
 *  Item used by the [FavoritesListViewModel] to keep track of loading times
 */

sealed class FavoriteItems
data class CameraData(val cameraItems: List<Camera>): FavoriteItems()
data class TravelTimeData(val travelTimeItems: List<TravelTime>): FavoriteItems()
data class FerryScheduleData(val ferryScheduleItems: List<FerrySchedule>): FavoriteItems()
data class MountainPassData(val mountainPassItems: List<MountainPass>): FavoriteItems()
data class BorderCrossingData(val borderCrossingItems: List<BorderCrossing>): FavoriteItems()
data class FavoriteLocationData(val favoriteLocationData: List<FavoriteLocation>): FavoriteItems()