package gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailingWithStatus
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import gov.wa.wsdot.android.wsdot.repository.VesselRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.model.common.Resource
import java.util.*
import javax.inject.Inject

/**
 *  ViewModel for retrieving sailings data from the FerriesRepository.
 *
 *  loads data from three different sources (sailings, spaces, vessels) into one FerrySailingsWithSpaces mediator object.
 *  We need to check all there points to kick off refreshes for the three different data sources.
 *
 *  Fetches sailings based on values in the SailingQuery object.
 */
class FerriesSailingViewModel @Inject constructor(ferriesRepository: FerriesRepository, vesselRepository: VesselRepository) : ViewModel() {

    private val _sailingQuery: MutableLiveData<SailingQuery> = MutableLiveData()

    private val sailings: LiveData<Resource<List<FerrySailingWithStatus>>> = Transformations
        .switchMap(_sailingQuery) { input ->
            input.ifExists { routeId, departingId, arrivingId, sailingDate ->
                ferriesRepository.loadSailings(routeId, departingId, arrivingId, sailingDate, false)
            }
        }

    private val spaces: LiveData<Resource<List<FerrySailingWithStatus>>> = Transformations
        .switchMap(_sailingQuery) { input ->
            input.ifExists { routeId, departingId, arrivingId, sailingDate ->
                ferriesRepository.loadSpaces(routeId, departingId, arrivingId, sailingDate)
            }
        }

    private val vessels: LiveData<Resource<List<FerrySailingWithStatus>>> = Transformations
        .switchMap(_sailingQuery) { input ->
            input.ifExists { routeId, departingId, arrivingId, sailingDate ->
                vesselRepository.loadSailingWithVessels(routeId, departingId, arrivingId, sailingDate, true)
            }
        }

    val sailingsWithStatus: MediatorLiveData<Resource<List<FerrySailingWithStatus>>> = MediatorLiveData()

    init {
        sailingsWithStatus.addSource(sailings) { sailingsWithStatus.value = it }
        sailingsWithStatus.addSource(spaces) { sailingsWithStatus.value = it }
        sailingsWithStatus.addSource(vessels) { sailingsWithStatus.value = it }
    }

    fun setSailingQuery(routeId: Int, departingId: Int, arrivingId: Int) {

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val sailingDate = _sailingQuery.value?.sailingDate ?: c.time // set to current date if null

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

    fun refresh() {
        val routeId = _sailingQuery.value?.routeId
        val departingId = _sailingQuery.value?.departingId
        val arrivingId = _sailingQuery.value?.arrivingId
        val sailingDate = _sailingQuery.value?.sailingDate

        if (routeId != null && departingId != null && arrivingId != null && sailingDate != null) {
            _sailingQuery.value = SailingQuery(routeId, departingId, arrivingId, sailingDate)
        }
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