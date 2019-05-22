package gov.wa.wsdot.android.wsdot.db.ferries

data class TerminalCombo(
    val departingTerminalId: Int,
    val departingTerminalName: String,
    val arrivingTerminalId: Int,
    val arrivingTerminalName: String
)