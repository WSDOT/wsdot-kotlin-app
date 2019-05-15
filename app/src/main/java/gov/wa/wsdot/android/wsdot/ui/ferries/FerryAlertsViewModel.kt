package gov.wa.wsdot.android.wsdot.ui.ferries

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.ferries.FerryAlert
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject


class FerryAlertsViewModel @Inject constructor(ferriesRepository: FerriesRepository) : ViewModel() {

    private val _routeId: MutableLiveData<RouteId> = MutableLiveData()

    val routeId: LiveData<RouteId>
        get() = _routeId

    val alerts: LiveData<Resource<List<FerryAlert>>> = Transformations
        .switchMap(_routeId) { input ->
            input.ifExists { routeId ->
                ferriesRepository.loadFerryAlerts(routeId)
            }
        }

    fun retry() {
        val routeId = _routeId.value?.routeId
        if (routeId != null) {
            _routeId.value = RouteId(routeId)
        }
    }

    fun setId(routeId: Int) {
        val update = RouteId(routeId)
        if (_routeId.value == update) {
            return
        }
        _routeId.value = update
    }

    data class RouteId(val routeId: Int) {
        fun <T> ifExists(f: (Int) -> LiveData<T>): LiveData<T> {
            return if (routeId < 0) {
                AbsentLiveData.create()
            } else {
                f(routeId)
            }
        }
    }

}