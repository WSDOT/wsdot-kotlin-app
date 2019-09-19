package gov.wa.wsdot.android.wsdot.ui.tollrates.tollsigns

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSign
import gov.wa.wsdot.android.wsdot.repository.TollSignRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class TollSignsViewModel @Inject constructor(tollSignRepository: TollSignRepository) : ViewModel() {

    val repo = tollSignRepository

    private val _route: MutableLiveData<TollRoute> = MutableLiveData()
    val route: LiveData<TollRoute>
        get() = _route

    // 2-way binding value for spinner
    private val _selectedDirection = MediatorLiveData<Pair<String, String>>()
    val selectedDirection: MutableLiveData<Pair<String, String>>
        get() = _selectedDirection

    // Used by spinner
    val directions = listOf(
        Pair("Northbound","N"),
        Pair("Southbound", "S")
    )

    private val _tollSigns: LiveData<Resource<List<TollSign>>> = Transformations
        .switchMap(_route) { route ->
            tollSignRepository.loadTollSignsOnRouteForDirection(route.route, route.direction, route.needsRefresh)
        }

    val tollSigns: MediatorLiveData<Resource<List<TollSign>>> = MediatorLiveData()

    init {
        _selectedDirection.value = directions[0]
        tollSigns.addSource(_tollSigns) { tollSigns.value = it}
        tollSigns.addSource(selectedDirection) { setRouteDirection(it.second) }
    }

    fun updateFavorite(id: String, isFavorite: Boolean) {
        repo.updateFavorite(id, isFavorite)
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

}