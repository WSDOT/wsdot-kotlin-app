package gov.wa.wsdot.android.wsdot.ui.ferries.route

import android.view.View
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import javax.inject.Inject
import android.widget.AdapterView
import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.util.network.Resource


class FerriesRouteViewModel @Inject constructor(ferriesRepository: FerriesRepository) : ViewModel() {

    private val _routeId: MutableLiveData<RouteId> = MutableLiveData()
    val routeId: LiveData<RouteId>
        get() = _routeId

    val route : LiveData<Resource<FerrySchedule>> = Transformations
        .switchMap(_routeId) { routeId ->
            ferriesRepository.loadSchedule(routeId.routeId, routeId.needsRefresh)
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

    fun onSelectSailing(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        //pos                                 get selected item position
        //view.getText()                      get lable of selected item
        //parent.getAdapter().getItem(pos)    get item by pos
        //parent.getAdapter().getCount()      get item count
        //parent.getCount()                   get item count
        //parent.getSelectedItem()            get selected item
        //and other...
    }

    data class RouteId(val routeId: Int, val needsRefresh: Boolean)

}