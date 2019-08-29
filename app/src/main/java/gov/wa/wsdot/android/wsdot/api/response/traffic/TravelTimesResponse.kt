package gov.wa.wsdot.android.wsdot.api.response.traffic

import com.google.gson.annotations.SerializedName

data class TravelTimesResponse (

    @field:SerializedName("travel_time_id")
    val travelTimeId: Int,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("via")
    val via: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("avg_time")
    val avgTime: Int,

    @field:SerializedName("current_time")
    val currentTime: Int,

    @field:SerializedName("miles")
    val miles: Float,

    @field:SerializedName("startLocationLatitude")
    val startLocationLatitude: Double,

    @field:SerializedName("startLocationLongitude")
    val startLocationLongitude: Double,

    @field:SerializedName("endLocationLatitude")
    val endLocationLatitude: Double,

    @field:SerializedName("endLocationLongitude")
    val endLocationLongitude: Double,

    @field:SerializedName("updated")
    val updated: String

)
