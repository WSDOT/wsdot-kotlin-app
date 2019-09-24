package gov.wa.wsdot.android.wsdot.db.tollrates.dynamic

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["id"])
data class TollSign(
    val id: String,
    val locationName: String,
    val stateRoute: Int,
    val milepost: Float,
    val travelDirection: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val trips: MutableList<TollTrip>,
    val localCacheDate: Date,
    val favorite: Boolean,
    val remove: Boolean
)