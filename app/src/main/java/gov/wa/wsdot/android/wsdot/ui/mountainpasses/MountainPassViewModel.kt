package gov.wa.wsdot.android.wsdot.ui.mountainpasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import gov.wa.wsdot.android.wsdot.repository.MountainPassRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class MountainPassViewModel @Inject constructor(mountainPassRepository: MountainPassRepository) : ViewModel() {

    private val repo = mountainPassRepository

    val passes = MediatorLiveData<Resource<List<MountainPass>>>()

    private var _passes: LiveData<Resource<List<MountainPass>>> = mountainPassRepository.loadPasses(false)

    init {
        passes.addSource(_passes) { passes.value = it }
    }

    fun updateFavorite(passId: Int, isFavorite: Boolean) {
        repo.updateFavorite(passId, isFavorite)
    }

    fun refresh() {
        passes.removeSource(_passes)
        _passes = repo.loadPasses(true)
        passes.addSource(_passes) { passes.value = it }
    }
}