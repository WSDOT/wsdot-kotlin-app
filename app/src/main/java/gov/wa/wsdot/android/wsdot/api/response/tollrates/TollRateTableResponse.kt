package gov.wa.wsdot.android.wsdot.api.response.tollrates

import com.google.gson.annotations.SerializedName

data class TollRateTableResponse (
    @field:SerializedName("TollRates")
    val tollRates: List<TollTable>
) {
    data class TollTable (
        @field:SerializedName("id")
        val id: Int,
        @field:SerializedName("route")
        val route: Int,
        @field:SerializedName("message")
        val message: String,
        @field:SerializedName("numCol")
        val numCol: Int,
        @field:SerializedName("tollTable")
        val rows: List<TollTableRowJson>
    ) {
        data class TollTableRowJson(
            @field:SerializedName("header")
            val isHeader: Boolean,
            @field:SerializedName("weekday")
            val isWeekday: Boolean,
            @field:SerializedName("start_time")
            val startTime: String?,
            @field:SerializedName("end_time")
            val endTime: String?,
            @field:SerializedName("rows")
            val values: List<String>
        )
    }
}