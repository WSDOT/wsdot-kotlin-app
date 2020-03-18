package gov.wa.wsdot.android.wsdot.ui.ferries.route

import android.location.Location
import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.ferries.FerryScheduleRange
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalCombo
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.DistanceUtils
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

/**
 * ViewModel that handles retrieval of route info from the FerriesRepository using
 * a query based on the values of a RouteIdQuery.
 *
 * provides UI with terminal pairs for selecting origin/destinations as well
 * as a the available range of schedule data for the route.
 *
 * Also holds logic for refreshing data and setting route as a favorite.
 *
 */
class FerriesRouteViewModel @Inject constructor(ferriesRepository: FerriesRepository) : ViewModel() {

    private val repo = ferriesRepository
    private val _routeIdQuery: MutableLiveData<RouteIdQuery> = MutableLiveData()

    // used by day picker to present available sailing dates.
    val scheduleRange: LiveData<FerryScheduleRange> = Transformations
        .switchMap(_routeIdQuery) { routeId ->
            ferriesRepository.loadScheduleRange(routeId.routeId)
        }

    private val _route : LiveData<Resource<FerrySchedule>> = Transformations
        .switchMap(_routeIdQuery) { routeId ->
            ferriesRepository.loadSchedule(routeId.routeId, routeId.needsRefresh)
        }

    val isFavoriteRoute: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        isFavoriteRoute.addSource(_route) {
            isFavoriteRoute.value = it.data?.favorite ?: false
        }
    }

    // Used by spinner
    val terminals : LiveData<Resource<List<TerminalCombo>>> = Transformations
        .switchMap(_routeIdQuery) { routeId ->
            ferriesRepository.loadTerminalCombos(routeId.routeId, routeId.needsRefresh)
        }

    // 2-way binding value for spinner
    private val _selectedTerminalCombo = MediatorLiveData<TerminalCombo>()
    val selectedTerminalCombo: MutableLiveData<TerminalCombo>
        get() = _selectedTerminalCombo


    fun setRouteId(newRouteId: Int) {
        val update = RouteIdQuery(newRouteId, false)
        if (_routeIdQuery.value == update) {
            return
        }
        _routeIdQuery.value = update
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
        val favorite = _route.value?.data?.favorite
        if (favorite != null) {
            repo.updateFavorite(routeId, !favorite)
        }
    }

    fun refresh() {
        val routeId = _routeIdQuery.value?.routeId
        if (routeId != null) {
            _routeIdQuery.value = RouteIdQuery(routeId, true)
        }
    }

    data class RouteIdQuery(val routeId: Int, val needsRefresh: Boolean) {
        fun <T> ifExists(f: (Int, Boolean) -> LiveData<T>): LiveData<T> {
            return if (routeId == 0) {
                AbsentLiveData.create()
            } else {
                f(routeId, needsRefresh)
            }
        }
    }

}

