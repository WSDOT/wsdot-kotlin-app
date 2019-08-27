package gov.wa.wsdot.android.wsdot.db.traffic

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["creationDate"])
data class FavoriteLocation(
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val zoom: Float,
    val creationDate: Date
)