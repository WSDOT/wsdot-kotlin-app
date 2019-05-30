package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    indices = [Index(value = ["route", "sailingDate", "departingTerminalId", "arrivingTerminalId", "departingTime", "arrivingTime"], unique = true)],
    primaryKeys = ["route", "sailingDate", "departingTerminalId", "arrivingTerminalId", "departingTime"],
    foreignKeys = [ForeignKey(
        entity = FerrySchedule::class,
        parentColumns = ["routeId"],
        childColumns = ["route"],
        onUpdate = ForeignKey.CASCADE,
        deferred = true
    )]
)
data class FerrySailing(
    val route: Int,
    val sailingDate: Date,
    val departingTerminalId: Int,
    val departingTerminalName: String,
    val arrivingTerminalId: Int,
    val arrivingTerminalName: String,
    val annotations: List<String>,
    val departingTime: Date,
    val arrivingTime: Date?,
    val cacheDate: Date
)