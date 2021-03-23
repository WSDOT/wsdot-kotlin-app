package gov.wa.wsdot.android.wsdot.ui.trafficmap.bridgealerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.travelerinfo.BridgeAlert
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.model.common.Resource
import gov.wa.wsdot.android.wsdot.repository.BridgeAlertRepository
import javax.inject.Inject

class BridgeAlertViewModel @Inject constructor(bridgeAlertsRepository: BridgeAlertRepository) : ViewModel() {

    private val _alertQuery: MutableLiveData<AlertQuery> = MutableLiveData()

    private val repo = bridgeAlertsRepository

    val alert: LiveData<Resource<BridgeAlert>> = Transformations
        .switchMap(_alertQuery) { input ->
            input.ifExists {
                bridgeAlertsRepository.loadBridgeAlert(it)
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
        repo.loadBridgeAlerts(true)
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