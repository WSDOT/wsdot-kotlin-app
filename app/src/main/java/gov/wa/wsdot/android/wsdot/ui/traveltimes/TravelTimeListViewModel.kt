package gov.wa.wsdot.android.wsdot.ui.traveltimes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.repository.TravelTimesRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class TravelTimeListViewModel @Inject constructor(travelTimesRepository: TravelTimesRepository):  ViewModel() {

    private val repo = travelTimesRepository

    private val _travelTimeQuery: MutableLiveData<TravelTimeQuery> = MutableLiveData()

    // used for loading & display status
    val travelTimes: LiveData<Resource<List<TravelTime>>> = Transformations
        .switchMap(_travelTimeQuery) { input ->
            input.ifExists { queryText ->
                repo.loadTravelTimesWithQuery(queryText, false)
            }
        }

    fun updateFavorite(travelTimeId: Int, isFavorite: Boolean) {
        repo.updateFavorite(travelTimeId, isFavorite)
    }

    fun refresh() {
        val query = _travelTimeQuery.value?.queryText
        if (query != null) {
            _travelTimeQuery.value = TravelTimeQuery(query)
        }
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