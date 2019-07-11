package gov.wa.wsdot.android.wsdot.ui.mountainpasses.report

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import gov.wa.wsdot.android.wsdot.repository.MountainPassRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class MountainPassReportViewModel @Inject constructor(mountainPassRepository: MountainPassRepository) : ViewModel() {

    private val repo = mountainPassRepository

    private val _passId: MutableLiveData<PassId> = MutableLiveData()
    val passId: LiveData<PassId>
        get() = _passId

    val pass : LiveData<Resource<MountainPass>> = Transformations
        .switchMap(_passId) { passId ->
            mountainPassRepository.loadPass(passId.passId)
        }

    fun updateFavorite(passId: Int) {
        val favorite = pass.value?.data?.favorite
        if (favorite != null) {
            repo.updateFavorite(passId, !favorite)
        }
    }

    fun refresh() {
        val passId = _passId.value?.passId
        if (passId != null) {
            _passId.value = PassId(passId, true)
        }
    }

    fun setPassId(newPassId: Int) {
        val update = PassId(newPassId, false)
        if (_passId.value == update) {
            return
        }
        _passId.value = update
    }

    data class PassId(val passId: Int, val needsRefresh: Boolean)

}