package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.ferries.Vessel
import gov.wa.wsdot.android.wsdot.repository.VesselRepository
import gov.wa.wsdot.android.wsdot.ui.ferries.route.FerriesRouteViewModel
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class VesselWatchViewModel @Inject constructor(vesselRepository: VesselRepository) : ViewModel() {

    private val repo = vesselRepository

    private val _vesselId: MutableLiveData<VesselId> = MutableLiveData()
    val vesselId: LiveData<VesselId>
        get() = _vesselId

    // mediator handles resubscribe on refresh
    val vessels = MediatorLiveData<Resource<List<Vessel>>>()

    private var vesselsLiveData : LiveData<Resource<List<Vessel>>> = vesselRepository.loadVessels(true)

    init {
        vessels.addSource(vesselsLiveData) { vessels.value = it }
    }

    // TODO: let users bookmark vessel watch
    /*
    fun updateFavorite() {
        repo.updateFavorite(isFavorite)
    }
    */

    fun refresh() {
        vessels.removeSource(vesselsLiveData)
        vesselsLiveData = repo.loadVessels(true)
        vessels.addSource(vesselsLiveData) { vessels.value = it }
    }

    data class VesselId(val vesselId: Int, val needsRefresh: Boolean)

}