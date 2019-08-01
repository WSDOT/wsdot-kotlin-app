package gov.wa.wsdot.android.wsdot.ui.trafficmap

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLngBounds
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData

data class BoundQuery(val bounds: LatLngBounds?, val refresh: Boolean) {
    fun <T> ifExists(f: (LatLngBounds, Boolean) -> LiveData<T>): LiveData<T> {
        return if ( bounds == null ) {
            AbsentLiveData.create()
        } else {
            f(bounds, refresh)
        }
    }
}