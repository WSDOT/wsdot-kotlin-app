package gov.wa.wsdot.android.wsdot.ui.cameras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class CameraViewModel @Inject constructor(cameraRepository: CameraRepository) : ViewModel() {

    private val _cameraQuery: MutableLiveData<CameraQuery> = MutableLiveData()

    private val repo = cameraRepository

    val camera: LiveData<Resource<Camera>> = Transformations
        .switchMap(_cameraQuery) { input ->
            input.ifExists {
                cameraRepository.loadCamera(it)
            }
        }

    fun setCameraQuery(cameraId: Int) {
        val update = CameraQuery(cameraId)
        if (_cameraQuery.value == update) {
            return
        }
        _cameraQuery.value = update
    }

    fun refresh() {
        repo.loadCameras(true)
    }

    fun updateFavorite(cameraId: Int) {
        val favorite = camera.value?.data?.favorite
        if (favorite != null) {
            repo.updateFavorite(cameraId, !favorite)
        }
    }

    data class CameraQuery(val cameraId: Int) {
        fun <T> ifExists(f: (Int) -> LiveData<T>): LiveData<T> {
            return if (cameraId == 0 ) {
                AbsentLiveData.create()
            } else {
                f(cameraId)
            }
        }
    }

}