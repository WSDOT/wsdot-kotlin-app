package gov.wa.wsdot.android.wsdot.db.travelerinfo

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["alertId"])
data class BridgeAlert(
    val alertId: Int,
    var bridge: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val travelCenterPriorityId: Int?,
    val openingTime: Date?,
    val localCacheDate: Date
)