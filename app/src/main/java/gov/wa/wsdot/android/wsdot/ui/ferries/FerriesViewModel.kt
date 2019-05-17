package gov.wa.wsdot.android.wsdot.ui.ferries

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

// Uses Saved State module for ViewModels: https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate#kotlin

class FerriesViewModel @Inject constructor(ferriesRepository: FerriesRepository) : ViewModel() {

    private val repo = ferriesRepository

    // mediator handles resubscribe on refresh
    val routes = MediatorLiveData<Resource<List<FerrySchedule>>>()

    private var routesLiveData : LiveData<Resource<List<FerrySchedule>>> = ferriesRepository.loadSchedule(false)

    init {
        routes.addSource(routesLiveData) { routes.value = it }
    }

    fun refresh() {
        routes.removeSource(routesLiveData)
        routesLiveData = repo.loadSchedule(false)
        routes.addSource(routesLiveData) { routes.value = it }
    }
}