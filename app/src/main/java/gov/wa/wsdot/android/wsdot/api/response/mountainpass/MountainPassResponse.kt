package gov.wa.wsdot.android.wsdot.api.response.mountainpass

import com.google.gson.annotations.SerializedName

data class MountainPassResponse (
    @field:SerializedName("GetMountainPassConditionsResult")
    val passConditions: PassConditions
) {
    data class PassConditions (
        @field:SerializedName("PassCondition")
        val items: List<PassItem>
    ) {
        data class PassItem(
            @field:SerializedName("MountainPassId")
            val mountainPassId: Int,
            @field:SerializedName("MountainPassName")
            val mountainPassName: String,
            @field:SerializedName("RoadCondition")
            val roadCondition: String,
            @field:SerializedName("WeatherCondition")
            val weatherCondition: String,
            @field:SerializedName("TemperatureInFahrenheit")
            val temperatureInFahrenheit: Int,
            @field:SerializedName("ElevationInFeet")
            val elevationInFeet: Int,
            @field:SerializedName("TravelAdvisoryActive")
            val travelAdvisoryActive: Boolean,
            @field:SerializedName("Latitude")
            val latitude: Double,
            @field:SerializedName("Longitude")
            val longitude: Double,
            @field:SerializedName("RestrictionOne")
            val restrictionOne: PassRestriction,
            @field:SerializedName("RestrictionTwo")
            val restrictionTwo: PassRestriction,
            @field:SerializedName("Cameras")
            val cameras: List<PassCamera>,
            @field:SerializedName("Forecast")
            val forecast: List<PassForecast>,
            @field:SerializedName("DateUpdated")
            val dateUpdated: List<Int>
        ) {
            data class PassRestriction(
                @field:SerializedName("RestrictionText")
                val restrictionText: String,
                @field:SerializedName("TravelDirection")
                val travelDirection: String
            )
            data class PassCamera(
                @field:SerializedName("id")
                val id: Int,
                @field:SerializedName("title")
                val title: String,
                @field:SerializedName("url")
                val url: String,
                @field:SerializedName("lat")
                val lat: Double,
                @field:SerializedName("lon")
                val lon: Double
            )
            data class PassForecast(
                @field:SerializedName("Day")
                val day: String,
                @field:SerializedName("ForecastText")
                val forecastText: String
            )
        }
    }
}