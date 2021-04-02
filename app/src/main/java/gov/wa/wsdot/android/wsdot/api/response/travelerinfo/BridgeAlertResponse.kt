package gov.wa.wsdot.android.wsdot.api.response.travelerinfo

import com.google.gson.annotations.SerializedName

class BridgeAlertResponse (
    @field:SerializedName("BridgeOpeningId")
    val bridgeOpeningId: Int,
    @field:SerializedName("Status")
    val status: String,
    @field:SerializedName("OpeningTime")
    val openingTime: String,
    @field:SerializedName("OriginalBridgeOpenScheduleDate")
    val originalBridgeOpenScheduleDate: String?,
    @field:SerializedName("Duration")
    val duration: Int,
    @field:SerializedName("EventText")
    val eventText: String,
    @field:SerializedName("BridgeLocation")
    val bridgeLocation: BridgeLocation
) {
    data class BridgeLocation (
        @field:SerializedName("Description")
        val description: String,
        @field:SerializedName("RoadName")
        val roadName: String,
        @field:SerializedName("Direction")
        val direction: String,
        @field:SerializedName("MilePost")
        val milepost: Double,
        @field:SerializedName("Latitude")
        val latitude: Double,
        @field:SerializedName("Longitude")
        val longitude: Double
    )
}