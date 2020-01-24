package gov.wa.wsdot.android.wsdot.ui.trafficmap.menus.expresslanes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.api.response.traffic.ExpressLanesStatusResponse
import gov.wa.wsdot.android.wsdot.api.response.traffic.ExpressLanesStatusResponse.ExpressLanes
import gov.wa.wsdot.android.wsdot.repository.ExpressLanesRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class ExpressLanesViewModel @Inject constructor(expressLanesRepository: ExpressLanesRepository) : ViewModel() {

    private val repo = expressLanesRepository

    // mediator handles resubscribe on refresh
    val expressLanes = MediatorLiveData<List<ExpressLanes.ExpressLaneRoute>>()
    var expressLanesLiveData : LiveData<Resource<ExpressLanesStatusResponse>> = expressLanesRepository.getExpressLanesStatus()

    init {
        expressLanes.addSource(expressLanesLiveData) {
            it.data?.expressLanes?.routes?.let { routes ->
                expressLanes.value = routes
            }
        }
    }

    fun refresh() {
        expressLanes.removeSource(expressLanesLiveData)
        expressLanesLiveData = repo.getExpressLanesStatus()
        expressLanes.addSource(expressLanesLiveData) {
            it.data?.expressLanes?.routes?.let { routes ->
                expressLanes.value = routes
            }
        }
    }
}