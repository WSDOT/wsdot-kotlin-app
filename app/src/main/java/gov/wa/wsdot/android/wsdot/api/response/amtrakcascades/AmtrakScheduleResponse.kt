package gov.wa.wsdot.android.wsdot.api.response.amtrakcascades

import com.google.gson.annotations.SerializedName

data class AmtrakScheduleResponse (
    @field:SerializedName("ArrivalComment")
    val arrivalComment: String?,
    @field:SerializedName("ArrivalScheduleType")
    val arrivalScheduleType: String?,
    @field:SerializedName("ArrivalTime")
    val arrivalTime: String?,
    @field:SerializedName("DepartureComment")
    val departureComment: String?,
    @field:SerializedName("DepartureScheduleType")
    val departureScheduleType: String?,
    @field:SerializedName("DepartureTime")
    val departureTime: String?,
    @field:SerializedName("ScheduledArrivalTime")
    val scheduledArrivalTime: String?,
    @field:SerializedName("ScheduledDepartureTime")
    val scheduledDepartureTime: String?,
    @field:SerializedName("SortOrder")
    val sortOrder: Int,
    @field:SerializedName("StationFullName")
    val stationFullName: String,
    @field:SerializedName("StationName")
    val StationName: String,
    @field:SerializedName("TrainMessage")
    val trainMessage: String,
    @field:SerializedName("TrainNumber")
    val trainNumber: Int,
    @field:SerializedName("TripNumber")
    val tripNumber: Int,
    @field:SerializedName("UpdateTime")
    val updateTime: String
)