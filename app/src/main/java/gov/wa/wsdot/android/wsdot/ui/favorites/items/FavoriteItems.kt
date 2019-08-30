package gov.wa.wsdot.android.wsdot.ui.favorites.items

import gov.wa.wsdot.android.wsdot.db.traffic.Camera

sealed class FavoriteItems
data class CameraData(val cameraItems: List<Camera>): FavoriteItems