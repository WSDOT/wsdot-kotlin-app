package gov.wa.wsdot.android.wsdot.db.mountainpass

import androidx.room.Entity
import java.util.*
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse.PassConditions.PassItem.PassCamera
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse.PassConditions.PassItem.PassForecast

@Entity(primaryKeys = ["passId"])
data class MountainPass (
    val passId: Int,
    val passName: String,
    val roadCondition: String,
    val weatherCondition: String,
    val temperatureInFahrenheit: Int,
    val elevationInFeet: Int,
    val travelAdvisoryActive: Boolean,
    val latitude: Double,
    val longitude: Double,
    val restrictionOneText: String,
    val restrictionOneDirection: String,
    val restrictionTwoText: String,
    val restrictionTwoDirection: String,
    val serverCacheDate: Date,
    val localCacheDate: Date,
    val cameras: List<PassCamera>,
    val forecasts: List<PassForecast>,
    val favorite: Boolean,
    val remove: Boolean
)