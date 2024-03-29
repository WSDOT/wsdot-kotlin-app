package gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.google.android.gms.maps.model.LatLngBounds
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.repository.HighwayAlertRepository
import gov.wa.wsdot.android.wsdot.ui.trafficmap.LatLngBoundQuery
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

class MapHighwayAlertsViewModel @Inject constructor(highwayAlertRepository: HighwayAlertRepository) : ViewModel() {

    private val _alertQueryLatLng: MutableLiveData<LatLngBoundQuery> = MutableLiveData()

    val alerts: LiveData<Resource<List<HighwayAlert>>> = _alertQueryLatLng.switchMap { input ->
            input.ifExists { bounds, refresh ->

                highwayAlertRepository.loadHighwayAlertsInBounds(bounds, refresh)
            }
        }

    fun setAlertQuery(bounds: LatLngBounds, refresh: Boolean) {
        val update = LatLngBoundQuery(bounds, refresh)
        if (_alertQueryLatLng.value == update) {
            return
        }
        _alertQueryLatLng.value = update
    }

    fun refresh() {
        _alertQueryLatLng.value?.bounds?.let {
            setAlertQuery(it, true)
        }
    }

}