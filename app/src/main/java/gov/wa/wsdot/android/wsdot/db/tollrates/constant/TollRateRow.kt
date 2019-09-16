package gov.wa.wsdot.android.wsdot.db.tollrates.constant

data class TollRateRow(
    val header: Boolean,
    val weekday: Boolean,
    val startTime: String?,
    val endTime: String?,
    val values: List<String>
)