package gov.wa.wsdot.android.wsdot.api.response.amtrakcascades

import com.google.gson.annotations.SerializedName
import java.util.*

data class AmtrakScheduleResponse (
    @field:SerializedName("ArrivalComment")
    val arrivalComment: String?,
    @field:SerializedName("ArrivalScheduleType")
    val arrivalScheduleType: String?,
    @field:SerializedName("ArrivalTime")
    val arrivalTime:  Date?,
    @field:SerializedName("DepartureComment")
    val departureComment: String?,
    @field:SerializedName("DepartureScheduleType")
    val departureScheduleType: String?,
    @field:SerializedName("DepartureTime")
    val departureTime: Date?,
    @field:SerializedName("ScheduledArrivalTime")
    val scheduledArrivalTime:  Date?,
    @field:SerializedName("ScheduledDepartureTime")
    val scheduledDepartureTime:  Date?,
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
    val updateTime: Date
)