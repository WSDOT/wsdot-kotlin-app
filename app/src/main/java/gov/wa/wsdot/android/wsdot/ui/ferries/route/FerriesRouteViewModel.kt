package gov.wa.wsdot.android.wsdot.ui.ferries.route

import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import javax.inject.Inject
import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalCombo
import gov.wa.wsdot.android.wsdot.util.network.Resource

class FerriesRouteViewModel @Inject constructor(ferriesRepository: FerriesRepository) : ViewModel() {

    private val _routeId: MutableLiveData<RouteId> = MutableLiveData()
    val routeId: LiveData<RouteId>
        get() = _routeId

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

    init {
        // dummy value before we get it from two-way binding
        selectedTerminalCombo.value = TerminalCombo(
            0,
            "",
            0,
            ""
        )
    }

    fun setRouteId(newRouteId: Int) {
        val update = RouteId(newRouteId, false)
        if (_routeId.value == update) {
            return
        }
        _routeId.value = update
    }

    fun refresh() {
        val routeId = _routeId.value?.routeId
        if (routeId != null) {
            _routeId.value = RouteId(routeId, true)
        }
    }

    data class RouteId(val routeId: Int, val needsRefresh: Boolean)

}

