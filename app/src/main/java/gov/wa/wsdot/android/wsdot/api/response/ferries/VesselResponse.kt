package gov.wa.wsdot.android.wsdot.api.response.ferries

import com.google.gson.annotations.SerializedName

data class VesselResponse (
    @field:SerializedName("VesselID")
    val vesselId: Int,
    @field:SerializedName("VesselName")
    val vesselName: String,
    @field:SerializedName("DepartingTerminalID")
    val departingTerminalId: Int,
    @field:SerializedName("DepartingTerminalName")
    val departingTerminalName: String,
    @field:SerializedName("ArrivingTerminalID")
    val arrivingTerminalId: Int?,
    @field:SerializedName("ArrivingTerminalName")
    val arrivingTerminalName: String?,
    @field:SerializedName("Latitude")
    val latitude: Double,
    @field:SerializedName("Longitude")
    val longitude: Double,
    @field:SerializedName("Speed")
    val speed: Double,
    @field:SerializedName("Heading")
    val heading: Double,
    @field:SerializedName("InService")
    val inService: Boolean,
    @field:SerializedName("AtDock")
    val atDock: Boolean,
    @field:SerializedName("LeftDock")
    val leftDock: String?,
    @field:SerializedName("Eta")
    val eta: String?,
    @field:SerializedName("EtaBasis")
    val etaBasis: String?,
    @field:SerializedName("ScheduledDeparture")
    val scheduledDeparture: String?,
    @field:SerializedName("TimeStamp")
    val timeStamp: String
)