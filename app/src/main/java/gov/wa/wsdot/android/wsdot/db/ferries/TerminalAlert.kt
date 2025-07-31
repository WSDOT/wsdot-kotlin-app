package gov.wa.wsdot.android.wsdot.db.ferries

import androidx.room.Entity
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerryTerminalResponse

@Entity(
    primaryKeys = ["terminalID"]
)
data class TerminalAlert(
    val terminalID: Int,
    val terminalName: String,
    val addressLineOne: String,
    val city: String?,
    val state: String,
    val zipCode: String,
    val latitude: Double,
    val longitude: Double,
    val bulletins: List<FerryTerminalResponse.BulletinAlert>

)