package gov.wa.wsdot.android.wsdot.api.response.traffic

import com.google.gson.annotations.SerializedName

data class ExpressLanesStatusResponse (
    @field:SerializedName("express_lanes")
    val expressLanes: ExpressLanes
) {
    data class ExpressLanes (
        @field:SerializedName("routes")
        val routes: List<ExpressLaneRoute>
    ) {
        data class ExpressLaneRoute(
            @field:SerializedName("title")
            val title: String,
            @field:SerializedName("route")
            val route: String,
            @field:SerializedName("status")
            val status: String,
            @field:SerializedName("updated")
            val updated: String
        )
    }
}