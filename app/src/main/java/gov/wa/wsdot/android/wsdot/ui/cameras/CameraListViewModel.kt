package gov.wa.wsdot.android.wsdot.ui.cameras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

/**
 * ViewModel that handles retrieval of a cameras from the CameraRepository using
 * a query based on the values of a CameraQuery.
 */
class CameraListViewModel @Inject constructor(cameraRepository: CameraRepository) : DataBoundCameraListViewModel, ViewModel() {

    private val cameraRepo = cameraRepository
    private val _camerasQuery: MutableLiveData<CamerasQuery> = MutableLiveData()

    // used for loading & display status
    override val cameras: LiveData<Resource<List<Camera>>> = _camerasQuery.switchMap { input ->
            input.ifExists { cameraIds ->
                cameraRepo.loadCamerasWithIds(cameraIds, false)
            }
        }

    fun updateFavorite(cameraId: Int, isFavorite: Boolean) {
        cameraRepo.updateFavorite(cameraId, isFavorite)
    }

    override fun refresh() {
        val pass = _camerasQuery.value?.cameraIds
        if (pass != null) {
            _camerasQuery.value = CamerasQuery(pass)
        }
    }

    fun setCamerasQuery(cameraIds: List<Int>) {
        val update =
            CamerasQuery(cameraIds)
        if (_camerasQuery.value == update) { return }
        _camerasQuery.value = update
    }

    data class CamerasQuery(val cameraIds: List<Int>) {
        fun <T> ifExists(f: (List<Int>) -> LiveData<T>): LiveData<T> {
            return f(cameraIds)
        }
    }


}