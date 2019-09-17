package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.Ignore
import java.util.*

data class FerrySailingWithSpaces(
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
    val maxSpaces: Int?,
    val spaces: Int?,
    val reserveSpaces: Int?,
    val spacesCacheDate: Date?
)