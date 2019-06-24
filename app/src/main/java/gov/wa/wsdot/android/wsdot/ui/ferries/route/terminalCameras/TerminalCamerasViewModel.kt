package gov.wa.wsdot.android.wsdot.ui.ferries.route.terminalCameras

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.ui.cameras.CameraListViewModel
import gov.wa.wsdot.android.wsdot.util.DistanceUtils
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class TerminalCamerasViewModel @Inject constructor(cameraRepository: CameraRepository) : CameraListViewModel() {

    private val repo = cameraRepository

    private val _terminalCameraQuery: MutableLiveData<TerminalCameraQuery> = MutableLiveData()

    override val cameras: LiveData<Resource<List<Camera>>> = Transformations
        .switchMap(_terminalCameraQuery) { input ->
            input.ifExists { _ ->
                cameraRepository.loadCamerasOnRoad("Ferries", false)
            }
        }

    val terminalCameras: LiveData<List<Camera>> = Transformations
        .switchMap(_terminalCameraQuery) { input ->
            input.ifExists { terminalId ->
                Transformations.map(cameras) {
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