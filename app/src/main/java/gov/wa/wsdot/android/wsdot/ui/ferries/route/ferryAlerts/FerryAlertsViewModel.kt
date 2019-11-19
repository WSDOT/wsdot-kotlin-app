package gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.ferries.FerryAlert
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class FerryAlertsViewModel @Inject constructor(ferryRepository: FerriesRepository): ViewModel() {

    private val _ferryAlertsQuery: MutableLiveData<FerryAlertsQuery> = MutableLiveData()

    val ferryAlerts: LiveData<Resource<List<FerryAlert>>> = Transformations
        .switchMap(_ferryAlertsQuery) { input ->
            input.ifExists { routeId ->
                ferryRepository.loadFerryAlerts(routeId)
            }
        }

    fun refresh() {
        val terminalId = _ferryAlertsQuery.value?.routeId
        if (terminalId != null) {
            _ferryAlertsQuery.value = FerryAlertsQuery(terminalId)
        }
    }

    fun setFerryAlertsRouteQuery(routeId: Int) {
        val update = FerryAlertsQuery(routeId)
        if (_ferryAlertsQuery.value == update) { return }
        _ferryAlertsQuery.value = update
    }

    data class FerryAlertsQuery(val routeId: Int) {
        fun <T> ifExists(f: (Int) -> LiveData<T>): LiveData<T> {
            return f(routeId)
        }
    }

}