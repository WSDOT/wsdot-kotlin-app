package gov.wa.wsdot.android.wsdot.db.traveltimes

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["travelTimeId"])
data class TravelTime (
    val travelTimeId: Int,
    val title: String,
    val via: String,
    val status: String,
    val avgTime: Int,
    val currentTime: Int,
    val hovCurrentTime: Int?,
    val miles: Float,
    val startLocationLatitude: Double,
    val startLocationLongitude: Double,
    val endLocationLatitude: Double,
    val endLocationLongitude: Double,
    val updated: Date,
    val localCacheDate: Date,
    val favorite: Boolean = false,
    val remove: Boolean = false
)