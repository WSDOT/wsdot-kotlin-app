package gov.wa.wsdot.android.wsdot.ui.bordercrossings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing
import gov.wa.wsdot.android.wsdot.repository.BorderCrossingRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

/**
 * ViewModel to manage data for border wait times.
 *
 * Holds a list of crossing times for a specific direction.
 * The direction is set by the _crossingDirection val.
 */
class BorderCrossingViewModel @Inject constructor(borderCrossingRepository: BorderCrossingRepository) : ViewModel() {

    private val repo = borderCrossingRepository

    private val _crossingDirection: MutableLiveData<CrossingDirection> = MutableLiveData()

    val crossings : LiveData<Resource<List<BorderCrossing>>> = Transformations
        .switchMap(_crossingDirection) { crossingDirection ->
            borderCrossingRepository.loadCrossingsForDirection(crossingDirection.direction, crossingDirection.needsRefresh)
        }

    fun updateFavorite(crossingId: Int, isFavorite: Boolean) {
        repo.updateFavorite(crossingId, isFavorite)
    }

    fun refresh() {
        val crossingDirection = _crossingDirection.value?.direction
        if (crossingDirection != null) {
            _crossingDirection.value = CrossingDirection(crossingDirection, true)
        }
    }

    fun setCrossingDirection(newCrossingDirection: String) {
        val update = CrossingDirection(newCrossingDirection, false)
        if (_crossingDirection.value == update) {
            return
        }
        _crossingDirection.value = update
    }

    data class CrossingDirection(val direction: String, val needsRefresh: Boolean)

}