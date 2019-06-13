package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.*

@Entity(
    indices = [Index(value = ["departingTerminalId", "arrivingTerminalId", "departureTime"], unique = true)],
    primaryKeys = ["departingTerminalId", "arrivingTerminalId", "departureTime"]
)
data class FerrySpace(

    val departingTerminalId: Int,
    val arrivingTerminalId: Int,
    val maxSpacesCount: Int,
    val currentSpacesCount: Int?,
    val reservableSpacesCount: Int?,
    val currentSpacesColor: String?,
    val reservableSpacesColor: String?,
    val departureTime: Date,
    val localCacheDate: Date

)
