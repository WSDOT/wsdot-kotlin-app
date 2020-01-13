package gov.wa.wsdot.android.wsdot.ui.trafficmap

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.model.MapLocationItem
import gov.wa.wsdot.android.wsdot.util.getDouble
import gov.wa.wsdot.android.wsdot.util.putDouble
import javax.inject.Inject

class MapLocationViewModel @Inject constructor(private val context: Context) : ViewModel() {

    val mapLocation = MutableLiveData<MapLocationItem>()

    init {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        val latitude = settings.getDouble(context.getString(R.string.user_preference_traffic_map_latitude), 47.6062)
        val longitude = settings.getDouble(context.getString(R.string.user_preference_traffic_map_longitude), -122.3321)
        val zoom = settings.getFloat(context.getString(R.string.user_preference_traffic_map_zoom), 12.0f)
        val loc = LatLng(latitude, longitude)
        mapLocation.value = MapLocationItem(loc, zoom)
    }

    fun updateLocation(location: MapLocationItem) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = settings.edit()
        editor.putDouble(
            context.getString(R.string.user_preference_traffic_map_latitude),
            location.location.latitude
        )
        editor.putDouble(
            context.getString(R.string.user_preference_traffic_map_longitude),
            location.location.longitude
        )
        editor.putFloat(
            context.getString(R.string.user_preference_traffic_map_zoom),
            location.zoom
        )
        editor.apply()

        mapLocation.value = location
    }
}