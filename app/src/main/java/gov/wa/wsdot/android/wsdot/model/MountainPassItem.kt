package gov.wa.wsdot.android.wsdot.model

import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse
import java.util.*
import kotlin.collections.ArrayList

class MountainPassItem (
    var passId: Int,
    var passName: String,
    var roadCondition: String,
    var weatherCondition: String,
    var temperatureInFahrenheit: Int,
    var elevationInFeet: Int,
    var travelAdvisoryActive: Boolean,
    var latitude: Double,
    var longitude: Double,
    var restrictionOneText: String,
    var restrictionOneDirection: String,
    var restrictionTwoText: String,
    var restrictionTwoDirection: String,
    var serverCacheDate: Date,
    var localCacheDate: Date,
    var cameras: List<MountainPassResponse.PassConditions.PassItem.PassCamera>,
    var forecasts: List<MountainPassResponse.PassConditions.PassItem.PassForecast>,
    var favorite: Boolean,
    var remove: Boolean
)
