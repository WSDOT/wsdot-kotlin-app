package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import gov.wa.wsdot.android.wsdot.db.ferries.Vessel
import gov.wa.wsdot.android.wsdot.repository.VesselRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

class VesselDetailsViewModel @Inject constructor(vesselRepository: VesselRepository) : ViewModel() {

    private val repo = vesselRepository

    private val _vesselQuery: MutableLiveData<VesselQuery> = MutableLiveData()

    val vessel: LiveData<Resource<Vessel>> = _vesselQuery.switchMap { input ->
            input.ifExists {
                vesselRepository.loadVessel(it)
            }
        }

    fun setVesselQuery(vesselId: Int) {
        val update = VesselQuery(vesselId)
        if (_vesselQuery.value == update) {
            return
        }
        _vesselQuery.value = update
    }

    fun refresh(vesselId: Int) {
            _vesselQuery.value = VesselQuery(vesselId)
            repo.loadVessel(vesselId)
    }


    data class VesselQuery(val vesselId: Int) {
        fun <T> ifExists(f: (Int) -> LiveData<T>): LiveData<T> {
            return if (vesselId == 0 ) {
                AbsentLiveData.create()
            } else {
                f(vesselId)
            }
        }
    }

}