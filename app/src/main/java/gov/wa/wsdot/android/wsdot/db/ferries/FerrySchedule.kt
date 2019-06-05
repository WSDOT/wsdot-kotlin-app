package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["routeId"])
data class FerrySchedule(

    val routeId: Int,
    val description: String,
    val crossingTime: Int,
    val serverCacheDate: Date,
    val localCacheDate: Date,
    val favorite: Boolean,
    val remove: Boolean

)