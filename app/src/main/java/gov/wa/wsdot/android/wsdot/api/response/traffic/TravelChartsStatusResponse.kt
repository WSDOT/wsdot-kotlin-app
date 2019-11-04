package gov.wa.wsdot.android.wsdot.api.response.traffic

import com.google.gson.annotations.SerializedName

data class TravelChartsStatusResponse (
    @field:SerializedName("available")
    val available: Boolean,
    @field:SerializedName("routes")
    val routes: List<ChartRoute>
) {
    data class ChartRoute(
        @field:SerializedName("name")
        val string: String,
        @field:SerializedName("charts")
        val charts: List<TravelChart>
    ) {
        data class TravelChart(
            @field:SerializedName("url")
            val imageUrl: String,
            @field:SerializedName("altText")
            val altText: String
        )
    }
}