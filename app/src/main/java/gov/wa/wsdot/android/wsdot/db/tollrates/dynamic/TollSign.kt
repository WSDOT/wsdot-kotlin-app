package gov.wa.wsdot.android.wsdot.db.tollrates.dynamic

import androidx.room.Entity

@Entity(primaryKeys = ["id"])
data class TollSign(
    val id: Int,
    val locationName: String,
    val stateRoute: Int,
    val milepost: Float,
    val travelDirection: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val trips: List<TollTrip>,
    val favorite: Boolean,
    val remove: Boolean
)