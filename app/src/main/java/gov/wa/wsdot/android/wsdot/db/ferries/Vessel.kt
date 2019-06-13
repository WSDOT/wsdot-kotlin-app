package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Vessel (

    @PrimaryKey
    val vesselId: Int,
    val vesselName: String,
    val departingTerminalId: Int,
    val departingTerminalName: String,
    val arrivingTerminalId: Int?,
    val arrivingTerminalName: String?,

    val latitude: Double,
    val longitude: Double,

    val speed: Double,
    val heading: Double,

    val inService: Boolean,
    val atDock: Boolean,

    val scheduledDeparture: Date?,
    val leftDock: Date?,
    val eta: Date?,
    val etaDetails: String?,

    val localCacheDate: Date,
    val serverCacheDate: Date
)