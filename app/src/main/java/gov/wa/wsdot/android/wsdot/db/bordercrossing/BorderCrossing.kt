package gov.wa.wsdot.android.wsdot.db.bordercrossing

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["crossingId"])
data class BorderCrossing(
    val crossingId: Int,
    val name: String,
    val route: Int,
    val lane: String,
    val direction: String,
    val wait: Int?,
    val updated: Date?,
    val localCacheDate: Date,
    val favorite: Boolean,
    val remove: Boolean
)