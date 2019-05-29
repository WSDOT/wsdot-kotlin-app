package gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailing
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.network.Resource
import java.util.*
import javax.inject.Inject

class FerriesSailingViewModel @Inject constructor(ferriesRepository: FerriesRepository) : ViewModel() {

    private val _sailingQuery: MutableLiveData<SailingQuery> = MutableLiveData()
    val sailingQuery: LiveData<SailingQuery>
        get() = _sailingQuery

    val sailings: LiveData<Resource<List<FerrySailing>>> = Transformations
        .switchMap(_sailingQuery) { input ->
            input.ifExists { routeId, departingId, arrivingId, sailingDate ->
                ferriesRepository.loadSailings(routeId, departingId, arrivingId, sailingDate, false)
            }
        }

    fun setSailingQuery(routeId: Int, departingId: Int, arrivingId: Int, sailingDate: Date) {
        val update = SailingQuery(
            routeId,
            departingId,
            arrivingId,
            sailingDate
        )
        if (_sailingQuery.value == update) {
            return
        }
        _sailingQuery.value = update
    }

    fun setSailingQuery(sailingDate: Date) {

        val routeId = _sailingQuery.value?.routeId
        val departingId = _sailingQuery.value?.departingId
        val arrivingId = _sailingQuery.value?.arrivingId

        if (routeId != null && departingId != null && arrivingId != null){
            val update = SailingQuery(
                routeId,
                departingId,
                arrivingId,
                sailingDate
            )
            if (_sailingQuery.value == update) {
                return
            }
            _sailingQuery.value = update
        }

    }

    // TODO: refresh
    fun refresh() {

    }

    data class SailingQuery(val routeId: Int, val departingId: Int, val arrivingId: Int, val sailingDate: Date) {
        fun <T> ifExists(f: (Int, Int, Int, Date) -> LiveData<T>): LiveData<T> {
            return if (routeId == 0 || departingId == 0 || arrivingId == 0) {
                AbsentLiveData.create()
            } else {
                f(routeId, departingId, arrivingId, sailingDate)
            }
        }
    }

}