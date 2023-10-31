package gov.wa.wsdot.android.wsdot.db.traffic

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["alertId"])
data class HighwayAlert(
    val alertId: Int,
    val headline: String,
    val roadName: String?,
    val priority: String,
    val travelCenterPriorityId: Int,
    val category: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val direction: String,
    val endLatitude: Double,
    val endLongitude: Double,
    val lastUpdatedTime: Date,
    val localCacheDate: Date
)