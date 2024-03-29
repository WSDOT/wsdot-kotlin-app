package gov.wa.wsdot.android.wsdot.ui.ferries.route.terminalCameras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.ui.cameras.DataBoundCameraListViewModel
import gov.wa.wsdot.android.wsdot.util.DistanceUtils
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

class TerminalCamerasViewModel @Inject constructor(cameraRepository: CameraRepository) : DataBoundCameraListViewModel, ViewModel() {

    private val repo = cameraRepository

    private val _terminalCameraQuery: MutableLiveData<TerminalCameraQuery> = MutableLiveData()

    // needed for loading status
    override val cameras: LiveData<Resource<List<Camera>>> = _terminalCameraQuery.switchMap { input ->
            input.ifExists {
                cameraRepository.loadCamerasOnRoad("Ferries", false)
            }
        }

    // used for display
    val terminalCameras: LiveData<List<Camera>> = _terminalCameraQuery.switchMap { input ->
            input.ifExists { terminalId -> cameras.map {
                    processTerminalCameras(terminalId, it.data)
                }
            }
        }

    fun updateFavorite(cameraId: Int, isFavorite: Boolean) {
        repo.updateFavorite(cameraId, isFavorite)
    }

    override fun refresh() {
        val terminalId = _terminalCameraQuery.value?.terminalId
        if (terminalId != null) {
            _terminalCameraQuery.value = TerminalCameraQuery(terminalId)
        }
    }

    fun setCameraQuery(terminalId: Int) {
        val update = TerminalCameraQuery(terminalId)
        if (_terminalCameraQuery.value == update) { return }
        _terminalCameraQuery.value = update
    }

    data class TerminalCameraQuery(val terminalId: Int) {
        fun <T> ifExists(f: (Int) -> LiveData<T>): LiveData<T> {
            return f(terminalId)
        }
    }

    private fun processTerminalCameras(terminalId: Int?, ferryCameras: List<Camera>?): List<Camera> {
        if (terminalId == null || ferryCameras == null) return emptyList()

        val terminalCameras = ArrayList<Camera>()
        val terminals = DistanceUtils.getTerminalLocations()

        for (camera in ferryCameras) {
            if (DistanceUtils.getDistanceFromPoints(camera.latitude, camera.longitude, terminals[terminalId].latitude, terminals[terminalId].longitude) < 2 ) {
                terminalCameras.add(camera)
            }
        }

        return terminalCameras.toList()
    }
}