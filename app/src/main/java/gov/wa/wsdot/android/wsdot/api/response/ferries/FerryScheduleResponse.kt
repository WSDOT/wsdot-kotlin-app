package gov.wa.wsdot.android.wsdot.api.response.ferries

import com.google.gson.annotations.SerializedName

data class FerryScheduleResponse (

    @field:SerializedName("RouteID")
    val routeId: Int,

    @field:SerializedName("Description")
    val description: String,

    @field:SerializedName("CrossingTime")
    val crossingTime: Int,

    @field:SerializedName("CacheDate")
    val cacheDate: String,

    @field:SerializedName("Date")
    val schedules: List<Schedule>,

    @field:SerializedName("RouteAlert")
    val alerts: List<RouteAlert>

) {

    data class Schedule(
        @field:SerializedName("Date")
        val date: String,

        @field:SerializedName("Sailings")
        val sailings: List<Sailing>

    ) {

        data class Sailing(
            @field:SerializedName("DepartingTerminalID")
            val departingTerminalID: Int,

            @field:SerializedName("DepartingTerminalName")
            val departingTerminalName: String,

            @field:SerializedName("ArrivingTerminalID")
            val arrivingTerminalID: Int,

            @field:SerializedName("ArrivingTerminalName")
            val arrivingTerminalName: String,

            @field:SerializedName("Annotations")
            val annotations: List<String>,

            @field:SerializedName("Times")
            val times: List<SailingTime>

        ) {

            data class SailingTime(

                @field:SerializedName("DepartingTime")
                val departingTime: String,

                @field:SerializedName("ArrivingTime")
                val arrivingTime: String?,

                @field:SerializedName("Annotations")
                val annotations: List<Int>

            )
        }
    }

    data class RouteAlert(
        @field:SerializedName("BulletinID")
        val alertId: Int,
        @field:SerializedName("PublishDate")
        val publishDate: String,
        @field:SerializedName("AlertDescription")
        val description: String,
        @field:SerializedName("AlertFullTitle")
        val fullTitle: String,
        @field:SerializedName("AlertFullText")
        val fullText: String
    )
}