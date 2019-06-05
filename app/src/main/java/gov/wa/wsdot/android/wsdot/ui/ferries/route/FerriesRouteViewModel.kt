package gov.wa.wsdot.android.wsdot.ui.ferries.route

import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import javax.inject.Inject
import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.ferries.FerryScheduleRange
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalCombo
import gov.wa.wsdot.android.wsdot.util.network.Resource

class FerriesRouteViewModel @Inject constructor(ferriesRepository: FerriesRepository) : ViewModel() {

    private val repo = ferriesRepository

    private val _routeId: MutableLiveData<RouteId> = MutableLiveData()
    val routeId: LiveData<RouteId>
        get() = _routeId

    val scheduleRange: LiveData<FerryScheduleRange> = Transformations
        .switchMap(_routeId) { routeId ->
            ferriesRepository.loadScheduleRange(routeId.routeId)
        }

    val route : LiveData<Resource<FerrySchedule>> = Transformations
        .switchMap(_routeId) { routeId ->
            ferriesRepository.loadSchedule(routeId.routeId, routeId.needsRefresh)
        }

    // Terminals - Used by spinner
    val terminals : LiveData<Resource<List<TerminalCombo>>> = Transformations
        .switchMap(_routeId) { routeId ->
            ferriesRepository.loadTerminalCombos(routeId.routeId, routeId.needsRefresh)
        }

    // 2-way binding value for spinner
    private val _selectedTerminalCombo = MediatorLiveData<TerminalCombo>()
    val selectedTerminalCombo: MutableLiveData<TerminalCombo>
        get() = _selectedTerminalCombo


    fun setRouteId(newRouteId: Int) {
        val update = RouteId(newRouteId, false)
        if (_routeId.value == update) {
            return
        }
        _routeId.value = update
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

    data class RouteId(val routeId: Int, val needsRefresh: Boolean)

}

