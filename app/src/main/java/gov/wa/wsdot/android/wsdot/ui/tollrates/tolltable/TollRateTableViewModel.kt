package gov.wa.wsdot.android.wsdot.ui.tollrates.tolltable

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateTable
import gov.wa.wsdot.android.wsdot.repository.TollRateRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

class TollRateTableViewModel @Inject constructor(tollRateRepository: TollRateRepository) : ViewModel() {

    private val _route: MutableLiveData<Route> = MutableLiveData()
    val route: LiveData<Route>
        get() = _route

    val tollTable : LiveData<Resource<TollRateTable>> = Transformations
        .switchMap(_route) { route ->
            tollRateRepository.loadTollTableForRoute(route.route, false)
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