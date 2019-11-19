package gov.wa.wsdot.android.wsdot.ui.ferries.route.ferryAlerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.ferries.FerryAlert
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class FerryAlertDetailsViewModel @Inject constructor(ferryRepository: FerriesRepository): ViewModel() {

    private val _ferryAlertQuery: MutableLiveData<FerryAlertsQuery> = MutableLiveData()

    val ferryAlert: LiveData<Resource<FerryAlert>> = Transformations
        .switchMap(_ferryAlertQuery) { input ->
            input.ifExists { alertId ->
                ferryRepository.loadFerryAlert(alertId)
            }
        }

    fun refresh() {
        val terminalId = _ferryAlertQuery.value?.alertId
        if (terminalId != null) {
            _ferryAlertQuery.value = FerryAlertsQuery(terminalId)
        }
    }

    fun setFerryAlertRouteQuery(alertId: Int) {
        val update = FerryAlertsQuery(alertId)
        if (_ferryAlertQuery.value == update) { return }
        _ferryAlertQuery.value = update
    }

    data class FerryAlertsQuery(val alertId: Int) {
        fun <T> ifExists(f: (Int) -> LiveData<T>): LiveData<T> {
            return f(alertId)
        }
    }

}