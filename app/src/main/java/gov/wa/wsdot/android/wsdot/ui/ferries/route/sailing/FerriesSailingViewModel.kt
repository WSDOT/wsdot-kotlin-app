package gov.wa.wsdot.android.wsdot.ui.ferries.route.sailing

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailingWithSpaces
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.network.Resource
import java.util.*
import javax.inject.Inject

class FerriesSailingViewModel @Inject constructor(ferriesRepository: FerriesRepository) : ViewModel() {

    private val _sailingQuery: MutableLiveData<SailingQuery> = MutableLiveData()

    private val sailings: LiveData<Resource<List<FerrySailingWithSpaces>>> = Transformations
        .switchMap(_sailingQuery) { input ->
            input.ifExists { routeId, departingId, arrivingId, sailingDate ->
                ferriesRepository.loadSailings(routeId, departingId, arrivingId, sailingDate, false)
            }
        }

    private val spaces: LiveData<Resource<List<FerrySailingWithSpaces>>> = Transformations
        .switchMap(_sailingQuery) { input ->
            input.ifExists { routeId, departingId, arrivingId, sailingDate ->
                ferriesRepository.loadSpaces(routeId, departingId, arrivingId, sailingDate)
            }
        }

    val sailingsWithSpaces: MediatorLiveData<Resource<List<FerrySailingWithSpaces>>> = MediatorLiveData()

    init {
        sailingsWithSpaces.addSource(sailings) { sailingsWithSpaces.value = it }
        sailingsWithSpaces.addSource(spaces) { sailingsWithSpaces.value = it }
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

    fun reset() {
        _sailingQuery.value =  SailingQuery(
            0,
            0,
            0,
            Date()
        )
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