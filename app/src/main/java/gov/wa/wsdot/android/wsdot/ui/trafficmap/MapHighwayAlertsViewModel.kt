package gov.wa.wsdot.android.wsdot.ui.trafficmap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLngBounds
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.repository.HighwayAlertRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class MapHighwayAlertsViewModel @Inject constructor(highwayAlertRepository: HighwayAlertRepository) : ViewModel() {

    private val _alertQuery: MutableLiveData<BoundQuery> = MutableLiveData()

    val alerts: LiveData<Resource<List<HighwayAlert>>> = Transformations
        .switchMap(_alertQuery) { input ->
            input.ifExists { bounds, refresh ->

                highwayAlertRepository.loadHighwayAlertsInBounds(bounds, refresh)
            }
        }

    fun setAlertQuery(bounds: LatLngBounds, refresh: Boolean) {
        val update = BoundQuery(bounds, refresh)
        if (_alertQuery.value == update) {
            return
        }
        _alertQuery.value = update
    }

    fun refresh() {
        _alertQuery.value?.bounds?.let {
            setAlertQuery(it, true)
        }
    }

}