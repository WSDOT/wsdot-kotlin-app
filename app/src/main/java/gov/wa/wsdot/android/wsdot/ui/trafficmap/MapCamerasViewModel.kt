package gov.wa.wsdot.android.wsdot.ui.trafficmap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLngBounds
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class MapCamerasViewModel @Inject constructor(cameraRepository: CameraRepository) : ViewModel() {

    private val _cameraQueryLatLng: MutableLiveData<LatLngBoundQuery> = MutableLiveData()

    val cameras: LiveData<Resource<List<Camera>>> = Transformations
        .switchMap(_cameraQueryLatLng) { input ->
            input.ifExists { bounds, refresh ->
                cameraRepository.loadCamerasInBounds(bounds, refresh)
            }
        }

    fun setCameraQuery(bounds: LatLngBounds, refresh: Boolean) {
        val update = LatLngBoundQuery(bounds, refresh)
        if (_cameraQueryLatLng.value == update) {
            return
        }
        _cameraQueryLatLng.value = update
    }

    fun refresh() {
        _cameraQueryLatLng.value?.bounds?.let {
            setCameraQuery(it, true)
        }
    }

}