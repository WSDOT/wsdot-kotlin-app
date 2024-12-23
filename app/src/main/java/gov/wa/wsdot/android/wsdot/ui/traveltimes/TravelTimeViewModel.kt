package gov.wa.wsdot.android.wsdot.ui.traveltimes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.model.common.Resource
import gov.wa.wsdot.android.wsdot.repository.TravelTimesRepository
import javax.inject.Inject

class TravelTimeViewModel @Inject constructor(travelTimesRepository: TravelTimesRepository) : ViewModel() {

    private val repo = travelTimesRepository

    private val _routeId: MutableLiveData<RouteId> = MutableLiveData()

    val routeId: LiveData<RouteId>
        get() = _routeId

    val route : LiveData<Resource<TravelTime>> = _routeId.switchMap { routeId ->
        travelTimesRepository.loadTravelTime(routeId.routeId)

    }

    val routes = MediatorLiveData<Resource<List<TravelTime>>>()

    private var _routes: LiveData<Resource<List<TravelTime>>> = travelTimesRepository.loadTravelTimes(false)

    init {
        routes.addSource(_routes) { routes.value = it }
    }

    fun updateFavorite(routeId: Int) {
        val favorite = route.value?.data?.favorite
        if (favorite != null) {
            repo.updateFavorite(routeId, !favorite)
        }
    }

    fun refresh() {
        val routeId = _routeId.value?.routeId
        if (routeId != null) {
            _routeId.value = RouteId(routeId, true)
        }
    }

    fun setRouteId(newRouteId: Int) {
        val update = RouteId(newRouteId, false)
        if (_routeId.value == update) {
            return
        }
        _routeId.value = update
    }

    data class RouteId(val routeId: Int, val needsRefresh: Boolean)

}