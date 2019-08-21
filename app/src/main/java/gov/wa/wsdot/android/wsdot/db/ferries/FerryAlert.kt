package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.Entity
import java.util.*

@Entity(
    primaryKeys = ["alertId", "route"]
)
data class FerryAlert(
    val alertId: Int,
    val title: String,
    val route: Int,
    val description: String?,
    val fullDescription: String,
    val publishDate: Date
)