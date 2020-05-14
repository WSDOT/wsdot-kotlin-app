package gov.wa.wsdot.android.wsdot.db.ferries

import java.util.*

data class FerrySailingWithStatus(
    val route: Int,
    val sailingDate: Date,
    val departingTerminalId: Int,
    val departingTerminalName: String,
    val arrivingTerminalId: Int,
    val arrivingTerminalName: String,
    val annotations: List<String>,
    val departingTime: Date,
    val arrivingTime: Date?,
    val cacheDate: Date,
    val showDriveUpSpaces: Boolean,
    val showResSpaces: Boolean,
    val maxSpaces: Int?,
    val spaces: Int?,
    val reserveSpaces: Int?,
    val spacesCacheDate: Date?,

    // Vessel Data
    val vesselId: Int?,
    val vesselName: String?,
    val vesselEta: Date?,
    val vesselLeftDock: Date?,
    val vesselAtDock: Boolean?,
    val vesselScheduledDeparture: Date?

)