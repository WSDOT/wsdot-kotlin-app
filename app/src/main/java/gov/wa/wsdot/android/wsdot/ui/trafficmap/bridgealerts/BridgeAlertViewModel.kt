package gov.wa.wsdot.android.wsdot.ui.trafficmap.bridgealerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.repository.HighwayAlertRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

class HighwayAlertViewModel @Inject constructor(highwayAlertsRepository: HighwayAlertRepository) : ViewModel() {

    private val _alertQuery: MutableLiveData<AlertQuery> = MutableLiveData()

    private val repo = highwayAlertsRepository

    val alert: LiveData<Resource<HighwayAlert>> = Transformations
        .switchMap(_alertQuery) { input ->
            input.ifExists {
                highwayAlertsRepository.loadHighwayAlert(it)
            }
        }

    fun setAlertQuery(alertId: Int) {
        val update = AlertQuery(alertId)
        if (_alertQuery.value == update) {
            return
        }
        _alertQuery.value = update
    }

    fun refresh() {
        repo.loadHighwayAlerts(true)
    }

    data class AlertQuery(val alertId: Int) {
        fun <T> ifExists(f: (Int) -> LiveData<T>): LiveData<T> {
            return if (alertId == 0 ) {
                AbsentLiveData.create()
            } else {
                f(alertId)
            }
        }
    }
}