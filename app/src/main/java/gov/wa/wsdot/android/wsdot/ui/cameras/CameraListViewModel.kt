package gov.wa.wsdot.android.wsdot.ui.cameras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class CameraListViewModel @Inject constructor(cameraRepository: CameraRepository) : DataBoundCameraListViewModel, ViewModel() {

    private val cameraRepo = cameraRepository

    private val _cameraQuery: MutableLiveData<CamerasQuery> = MutableLiveData()

    // used for loading & display status
    override val cameras: LiveData<Resource<List<Camera>>> = Transformations
        .switchMap(_cameraQuery) { input ->
            input.ifExists { cameraIds ->
                cameraRepo.loadCamerasWithIds(cameraIds, false)
            }
        }

    fun updateFavorite(cameraId: Int, isFavorite: Boolean) {
        cameraRepo.updateFavorite(cameraId, isFavorite)
    }

    override fun refresh() {
        val pass = _cameraQuery.value?.cameraIds
        if (pass != null) {
            _cameraQuery.value = CamerasQuery(pass)
        }
    }

    fun setCamerasQuery(cameraIds: List<Int>) {
        val update =
            CamerasQuery(cameraIds)
        if (_cameraQuery.value == update) { return }
        _cameraQuery.value = update
    }

    data class CamerasQuery(val cameraIds: List<Int>) {
        fun <T> ifExists(f: (List<Int>) -> LiveData<T>): LiveData<T> {
            return f(cameraIds)
        }
    }


}