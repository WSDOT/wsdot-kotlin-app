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

    private val _ferryAlertQuery: MutableLiveData<FerryAlertQuery> = MutableLiveData()

    val ferryAlerts: LiveData<Resource<List<FerryAlert>>> = Transformations
        .switchMap(_ferryAlertQuery) { input ->
            input.ifExists { routeId ->
                ferryRepository.loadFerryAlerts(routeId)
            }
        }

    fun refresh() {
        val terminalId = _ferryAlertQuery.value?.routeId
        if (terminalId != null) {
            _ferryAlertQuery.value = FerryAlertQuery(terminalId)
        }
    }

    fun setFerryAlertQuery(routeId: Int) {
        val update = FerryAlertQuery(routeId)
        if (_ferryAlertQuery.value == update) { return }
        _ferryAlertQuery.value = update
    }

    data class FerryAlertQuery(val routeId: Int) {
        fun <T> ifExists(f: (Int) -> LiveData<T>): LiveData<T> {
            return f(routeId)
        }
    }

}