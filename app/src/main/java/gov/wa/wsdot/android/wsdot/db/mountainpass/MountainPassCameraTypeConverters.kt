package gov.wa.wsdot.android.wsdot.db.mountainpass

import androidx.room.TypeConverter
import com.google.gson.Gson
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse.PassConditions.PassItem.PassCamera
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse.PassConditions.PassItem.PassForecast


class MountainPassCameraTypeConverters {

    @TypeConverter
    fun fromPassCameraList(passCameras: List<PassCamera>): String {
        return Gson().toJson(passCameras)
    }

    @TypeConverter
    fun toPassCameraList(passCameraString: String): List<PassCamera> {
        val cameras = Gson().fromJson(passCameraString, Array<PassCamera>::class.java) as Array<PassCamera>
        return cameras.toList()
    }

    @TypeConverter
    fun fromPassForecastList(passForecast: List<PassForecast>): String {
        return Gson().toJson(passForecast)
    }

    @TypeConverter
    fun toPassForecastList(passForecastString: String): List<PassForecast> {
        val forecasts = Gson().fromJson(passForecastString, Array<PassForecast>::class.java) as Array<PassForecast>
        return forecasts.toList()
    }

}