package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSign
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.repository.TollSignRepository
import gov.wa.wsdot.android.wsdot.repository.TravelTimesRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

class TollSignsViewModel @Inject constructor(
    tollSignRepository: TollSignRepository,
    travelTimesRepository: TravelTimesRepository
) : ViewModel() {

    private val tollRepo = tollSignRepository
    private val travelTimeRepo = travelTimesRepository

    private val _route: MutableLiveData<TollRoute> = MutableLiveData()
    private val _travelTimeQuery: MutableLiveData<TravelTimesQuery> = MutableLiveData()

    private var southboundGeneralTravelTimeId: Int = 0
    private var southboundTollTravelTimeId: Int = 0
    private var northboundGeneralTravelTimeId: Int = 0
    private var northboundTollTravelTimeId: Int = 0

    // 2-way binding value for spinner
    private val _selectedDirection = MediatorLiveData<Pair<String, String>>()
    val selectedDirection: MutableLiveData<Pair<String, String>>
        get() = _selectedDirection

    // Used by spinner
    val directions = listOf(
        Pair("Northbound","N"),
        Pair("Southbound", "S")
    )

    private val _tollSigns: LiveData<Resource<List<TollSign>>> = _route.switchMap { route ->
            tollSignRepository.loadTollSignsOnRouteForDirection(route.route, route.direction, route.needsRefresh)
        }


    private val _tollTravelTimes: LiveData<Resource<List<TravelTime>>> = _travelTimeQuery.switchMap { input ->
            input.ifExists { id1, id2 ->
                travelTimeRepo.loadTravelTimesWithIDs(listOf(id1, id2), false)
            }
        }


    val tollSigns: MediatorLiveData<Resource<List<TollSign>>> = MediatorLiveData()
    val tollTravelTimes: MediatorLiveData<Resource<List<TravelTime>>> = MediatorLiveData()

    init {
        _selectedDirection.value = directions[0]
        tollSigns.addSource(_tollSigns) { tollSigns.value = it}
        tollSigns.addSource(selectedDirection) { setRouteDirection(it.second) }

        tollTravelTimes.addSource(_tollTravelTimes) { tollTravelTimes.value = it}
        tollTravelTimes.addSource(selectedDirection) {
            if (it.second == "N") {
                setTravelTimeQuery(northboundGeneralTravelTimeId, northboundTollTravelTimeId)
            } else if (it.second == "S") {
                setTravelTimeQuery(southboundGeneralTravelTimeId, southboundTollTravelTimeId)
            }
        }
    }

    fun updateFavorite(id: String, isFavorite: Boolean) {
        tollRepo.updateFavorite(id, isFavorite)
    }

    fun refresh() {
        val route = _route.value?.route
        val direction = _route.value?.direction

        if (route != null && direction != null) {
            _route.value =
                TollRoute(
                    route,
                    direction,
                    true
                )
        }
    }

    fun setTravelTimeIds(nbGenId: Int, nbTollId: Int, sbGenId: Int, sbTollId: Int) {
        northboundGeneralTravelTimeId = nbGenId
        northboundTollTravelTimeId = nbTollId
        southboundGeneralTravelTimeId = sbGenId
        southboundTollTravelTimeId = sbTollId
    }

    fun setRoute(route: Int, direction: String){
        val update = TollRoute(
            route,
            direction,
            false
        )
        if (_route.value == update) {
            return
        }
        _route.value = update
    }

    private fun setRouteDirection(newDirection: String) {

        val route = _route.value?.route

        if (route != null) {
            val update = TollRoute(
                route,
                newDirection,
                false
            )
            if (_route.value == update) {
                return
            }
            _route.value = update
        }
    }

    data class TollRoute(val route: Int, val direction: String, val needsRefresh: Boolean)


    // Travel Time logic

    private fun setTravelTimeQuery(generalId: Int, tollId: Int) {
        val update = TravelTimesQuery(generalId, tollId)
        if (_travelTimeQuery.value == update) { return }
        _travelTimeQuery.value = update
    }

    data class TravelTimesQuery(val generalId: Int, val tollId: Int) {
        fun <T> ifExists(f: (Int, Int) -> LiveData<T>): LiveData<T> {
            return f(generalId, tollId)
        }
    }

}