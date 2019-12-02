package gov.wa.wsdot.android.wsdot.ui.trafficmap.trafficalerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.repository.HighwayAlertRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class HighestImpactAlertsViewModel @Inject constructor(highwayAlertRepository: HighwayAlertRepository) : ViewModel() {

    private val repo = highwayAlertRepository

    // mediator handles resubscribe on refresh
    val highestImpactAlerts = MediatorLiveData<Resource<List<HighwayAlert>>>()

    private var alertsLiveData : LiveData<Resource<List<HighwayAlert>>> = highwayAlertRepository.loadHighestImpactHighwayAlerts(false)

    init {
        highestImpactAlerts.addSource(alertsLiveData) { highestImpactAlerts.value = it }
    }

    fun refresh() {
        highestImpactAlerts.removeSource(alertsLiveData)
        alertsLiveData = repo.loadHighestImpactHighwayAlerts(true)
        highestImpactAlerts.addSource(alertsLiveData) { highestImpactAlerts.value = it }
    }

}