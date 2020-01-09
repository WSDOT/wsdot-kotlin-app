package gov.wa.wsdot.android.wsdot.ui.traveltimes

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.repository.TravelTimesRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class TravelTimeListViewModel @Inject constructor(travelTimesRepository: TravelTimesRepository):  ViewModel() {

    private val repo = travelTimesRepository

    private val _travelTimeQuery: MutableLiveData<TravelTimeQuery> = MutableLiveData()

    // used for loading & display status
    private var travelTimesLiveDate: LiveData<Resource<List<TravelTime>>> = Transformations
        .switchMap(_travelTimeQuery) { input ->
            input.ifExists { queryText ->
                repo.loadTravelTimesWithQuery(queryText, false)
            }
        }

    // mediator handles resubscribe on refresh
    val travelTimes = MediatorLiveData<Resource<List<TravelTime>>>()

    init {
        travelTimes.addSource(travelTimesLiveDate) { travelTimes.value = it }
    }

    fun updateFavorite(travelTimeId: Int, isFavorite: Boolean) {
        repo.updateFavorite(travelTimeId, isFavorite)
    }

    fun refresh() {
        travelTimes.removeSource(travelTimesLiveDate)
        travelTimesLiveDate = Transformations
            .switchMap(_travelTimeQuery) { input ->
                input.ifExists { queryText ->
                    repo.loadTravelTimesWithQuery(queryText, true)
                }
            }
        travelTimes.addSource(travelTimesLiveDate) { travelTimes.value = it }
    }

    fun setTravelTimeQuery(queryText: String) {
        val update = TravelTimeQuery(queryText)
        if (_travelTimeQuery.value == update) { return }
        _travelTimeQuery.value = update
    }

    data class TravelTimeQuery(val queryText: String) {
        fun <T> ifExists(f: (String) -> LiveData<T>): LiveData<T> {
            return f(queryText)
        }
    }

}