package gov.wa.wsdot.android.wsdot.api.response.traffic

import com.google.gson.annotations.SerializedName

data class HighwayAlertsResponse (
    @field:SerializedName("alerts")
    val alerts: Alerts
) {
    data class Alerts (
        @field:SerializedName("items")
        val items: List<AlertItem>
    ) {
        data class AlertItem(
            @field:SerializedName("AlertID")
            val alertId: Int,
            @field:SerializedName("HeadlineDescription")
            val HeadlineDescription: String,
            @field:SerializedName("ExtendedDescription")
            val extendedDescription: String?,
            @field:SerializedName("Priority")
            val priority: String,
            @field:SerializedName("TravelCenterPriorityId")
            val travelCenterPriorityId: Int,
            @field:SerializedName("Region")
            val region: String,
            @field:SerializedName("EventCategory")
            val eventCategory: String,
            @field:SerializedName("EventCategoryTypeDescription")
            val eventCategoryTypeDescription: String,
            @field:SerializedName("EventCategoryType")
            val eventCategoryType: String,
            @field:SerializedName("County")
            val county: String,
            @field:SerializedName("EventStatus")
            val eventStatus: String,
            @field:SerializedName("DisplayLatitude")
            val displayLatitude: Double,
            @field:SerializedName("DisplayLongitude")
            val displayLongitude: Double,
            @field:SerializedName("StartRoadwayLocation")
            val startRoadwayLocation: RoadwayLocation,
            @field:SerializedName("EndRoadwayLocation")
            val endRoadwayLocation: RoadwayLocation,
            @field:SerializedName("StartTime")
            val startTime: String,
            @field:SerializedName("EndTime")
            val endTime: String,
            @field:SerializedName("LastUpdatedTime")
            val lastUpdatedTime: String
        ) {
            data class RoadwayLocation(
                @field:SerializedName("Direction")
                val direction: String,
                @field:SerializedName("Description")
                val description: String?,
                @field:SerializedName("RoadName")
                val roadName: String,
                @field:SerializedName("MilePost")
                val milepost: Double,
                @field:SerializedName("Latitude")
                val latitude: Double,
                @field:SerializedName("Longitude")
                val longitude: Double
            )
        }
    }
}