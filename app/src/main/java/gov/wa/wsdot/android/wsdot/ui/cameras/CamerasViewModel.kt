package gov.wa.wsdot.android.wsdot.ui.cameras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

/**
 * View model that returns a list of every camera in the database.
 */
class CamerasViewModel @Inject constructor(cameraRepository: CameraRepository) : ViewModel() {

    private val repo = cameraRepository

    // mediator handles resubscribe on refresh
    val cameras = MediatorLiveData<Resource<List<Camera>>>()

    private var camerasLiveData : LiveData<Resource<List<Camera>>> = cameraRepository.loadCameras(false)

    init {
        cameras.addSource(camerasLiveData) { cameras.value = it }
    }

    fun refresh() {
        cameras.removeSource(camerasLiveData)
        camerasLiveData = repo.loadCameras(true)
        cameras.addSource(camerasLiveData) { cameras.value = it }
    }
}