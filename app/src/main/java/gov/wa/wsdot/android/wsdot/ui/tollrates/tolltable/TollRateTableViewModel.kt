package gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTable
import gov.wa.wsdot.android.wsdot.repository.TollRateRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

class TollRateTableViewModel @Inject constructor(tollRateRepository: TollRateRepository) : ViewModel() {

    private val _route: MutableLiveData<Route> = MutableLiveData()
    val route: LiveData<Route>
        get() = _route

    val tollTable : LiveData<Resource<TollRateTable>> = _route.switchMap { route ->
            tollRateRepository.loadTollTableForRoute(route.route, false)
        }

    // 2-way binding value for spinner
    private val _selectedDirection = MediatorLiveData<Pair<String, String>>()
    val selectedDirection: MutableLiveData<Pair<String, String>>
        get() = _selectedDirection

    // Used by spinner
    val directions = listOf(
        Pair("Northbound","N"),
        Pair("Southbound", "S")
    )

    init {
        _selectedDirection.value = directions[0]
    }


    fun refresh() {
        val route = _route.value?.route
        if (route != null) {
            _route.value =
                Route(
                    route,
                    true
                )
        }
    }

    fun setRoute(newRoute: Int) {
        val update = Route(
            newRoute,
            false
        )
        if (_route.value == update) {
            return
        }
        _route.value = update
    }

    data class Route(val route: Int, val needsRefresh: Boolean)

}