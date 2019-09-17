package gov.wa.wsdot.android.wsdot.api.response.tollrates

import com.google.gson.annotations.SerializedName

data class TollTripResponse (
    @field:SerializedName("TripName")
    val tripName: String,
    @field:SerializedName("CurrentToll")
    val currentToll: Int,
    @field:SerializedName("CurrentMessage")
    val currentMessage: String?,
    @field:SerializedName("StateRoute")
    val stateRoute: Int,
    @field:SerializedName("TravelDirection")
    val travelDirection: String,
    @field:SerializedName("StartMilepost")
    val startMilepost: Float,
    @field:SerializedName("StartLocationName")
    val startLocationName: String,
    @field:SerializedName("StartLatitude")
    val startLatitude: Double,
    @field:SerializedName("StartLongitude")
    val startLongitude: Double,
    @field:SerializedName("EndMilepost")
    val endMilepost: Float,
    @field:SerializedName("EndLocationName")
    val endLocationName: String,
    @field:SerializedName("EndLatitude")
    val endLatitude: Double,
    @field:SerializedName("EndLongitude")
    val endLongitude: Double
)