package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelerinformation.bridgeAlerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.travelerinfo.BridgeAlert
import gov.wa.wsdot.android.wsdot.model.common.Resource
import gov.wa.wsdot.android.wsdot.repository.BridgeAlertRepository
import javax.inject.Inject

class BridgeAlertsViewModel @Inject constructor(bridgeAlertRepository: BridgeAlertRepository) : ViewModel() {

    private val repo = bridgeAlertRepository

    // mediator handles resubscribe on refresh
    val alerts = MediatorLiveData<Resource<List<BridgeAlert>>>()

    private var alertsLiveData : LiveData<Resource<List<BridgeAlert>>> = bridgeAlertRepository.loadBridgeAlerts(false)

    init {
        alerts.addSource(alertsLiveData) { alerts.value = it }
    }

    fun refresh() {
        alerts.removeSource(alertsLiveData)
        alertsLiveData = repo.loadBridgeAlerts(true)
        alerts.addSource(alertsLiveData) { alerts.value = it }
    }

}