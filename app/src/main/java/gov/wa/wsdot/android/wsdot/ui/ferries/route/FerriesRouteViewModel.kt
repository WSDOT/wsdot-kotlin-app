package gov.wa.wsdot.android.wsdot.ui.ferries.route

import android.location.Location
import android.util.Log
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import javax.inject.Inject
import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.ferries.FerryScheduleRange
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalCombo
import gov.wa.wsdot.android.wsdot.model.FerriesTerminalItem
import gov.wa.wsdot.android.wsdot.util.DistanceUtils
import gov.wa.wsdot.android.wsdot.util.network.Resource
import java.lang.Double.POSITIVE_INFINITY

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

    /*
     *  Swaps the initial terminal query if the arriving terminal is closer to the user than the departing.
     */
    fun selectTerminalNearestTo(location: Location) {
        terminals.value?.let {
            it.data?.let {terminals ->

                val terminalLocations = DistanceUtils.getTerminalLocations()

                var nearestTerminalCombo: TerminalCombo? = null
                var winner = Int.MAX_VALUE

                for (terminalCombo in terminals) {

                    terminalLocations[terminalCombo.departingTerminalId]?.let {

                        val contender = DistanceUtils.getDistanceFromPoints(it.latitude, it.longitude, location.latitude, location.longitude)

                        if (contender < winner) {
                            winner = contender
                            nearestTerminalCombo = terminalCombo
                        }
                    }
                }
                nearestTerminalCombo?.let {termCombo ->
                    _selectedTerminalCombo.value = termCombo
                }
            }
        }
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

