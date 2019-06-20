package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

@Entity(
    primaryKeys = ["alertId", "route"],
    foreignKeys = [ForeignKey(
        entity = FerrySchedule::class,
        parentColumns = ["routeId"],
        childColumns = ["route"],
        onUpdate = ForeignKey.CASCADE,
        deferred = true
    )]
)
data class FerryAlert(
    val alertId: Int,
    val title: String,
    val route: Int,
    val description: String?,
    val fullDescription: String,
    val publishDate: Date
)