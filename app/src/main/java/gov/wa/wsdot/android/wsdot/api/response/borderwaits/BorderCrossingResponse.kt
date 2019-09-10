package gov.wa.wsdot.android.wsdot.api.response.borderwaits

import com.google.gson.annotations.SerializedName

data class BorderCrossingResponse (
    @field:SerializedName("waittimes")
    val waitTimes: WaitTime
) {
    data class WaitTime (
        @field:SerializedName("items")
        val items: List<WaitTimeItem>
    ) {
        data class WaitTimeItem(
            @field:SerializedName("id")
            val id: Int,
            @field:SerializedName("name")
            val name: String,
            @field:SerializedName("lane")
            val lane: String,
            @field:SerializedName("route")
            val route: Int,
            @field:SerializedName("direction")
            val direction: String,
            @field:SerializedName("wait")
            val wait: Int,
            @field:SerializedName("updated")
            val updated: String
        )
    }
}