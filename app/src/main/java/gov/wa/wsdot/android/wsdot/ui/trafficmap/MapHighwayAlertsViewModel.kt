package gov.wa.wsdot.android.wsdot.ui.trafficmap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLngBounds
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class MapCamerasViewModel @Inject constructor(cameraRepository: CameraRepository) : ViewModel() {

    private val _cameraQuery: MutableLiveData<CameraQuery> = MutableLiveData()

    val cameras: LiveData<Resource<List<Camera>>> = Transformations
        .switchMap(_cameraQuery) { input ->
            input.ifExists { bounds, refresh ->
                cameraRepository.loadCamerasInBounds(bounds, refresh)
            }
        }

    fun setCameraQuery(bounds: LatLngBounds, refresh: Boolean) {
        val update = CameraQuery(bounds, refresh)
        if (_cameraQuery.value == update) {
            return
        }
        _cameraQuery.value = update
    }

    fun refresh() {
        _cameraQuery.value?.bounds?.let {
            setCameraQuery(it, true)
        }
    }

    data class CameraQuery(val bounds: LatLngBounds?, val refresh: Boolean) {
        fun <T> ifExists(f: (LatLngBounds, Boolean) -> LiveData<T>): LiveData<T> {
            return if ( bounds == null ) {
                AbsentLiveData.create()
            } else {
                f(bounds, refresh)
            }
        }
    }
}