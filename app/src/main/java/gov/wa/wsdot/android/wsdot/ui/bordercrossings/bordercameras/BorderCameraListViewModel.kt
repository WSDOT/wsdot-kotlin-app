package gov.wa.wsdot.android.wsdot.ui.bordercrossings.bordercameras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.ui.cameras.DataBoundCameraListViewModel
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

/**
 * ViewModel that holds a list of cameras returned from a query using fields from the
 * BorderCameraQuery values.
 */
class BorderCameraListViewModel @Inject constructor(cameraRepository: CameraRepository) : DataBoundCameraListViewModel, ViewModel() {

    private val cameraRepo = cameraRepository
    private val _cameraQuery: MutableLiveData<BorderCamerasQuery> = MutableLiveData()

    // used for loading & display status
    override val cameras: LiveData<Resource<List<Camera>>> = Transformations
        .switchMap(_cameraQuery) { input ->
            input.ifExists { roadName, latitude ->
                cameraRepo.loadCamerasOnRoadNorthOf(roadName, latitude, false)
            }
        }

    fun updateFavorite(cameraId: Int, isFavorite: Boolean) {
        cameraRepo.updateFavorite(cameraId, isFavorite)
    }

    override fun refresh() {
        val roadName = _cameraQuery.value?.roadName
        val minMilepost = _cameraQuery.value?.latitude

        if (roadName != null && minMilepost != null) {
            _cameraQuery.value = BorderCamerasQuery(roadName, minMilepost)
        }
    }

    fun setCamerasQuery(roadName: String, latitude: Double) {
        val update =
            BorderCamerasQuery(roadName, latitude)
        if (_cameraQuery.value == update) { return }
        _cameraQuery.value = update
    }

    data class BorderCamerasQuery(val roadName: String, val latitude: Double) {
        fun <T> ifExists(f: (String, Double) -> LiveData<T>): LiveData<T> {
            return f(roadName, latitude)
        }
    }


}