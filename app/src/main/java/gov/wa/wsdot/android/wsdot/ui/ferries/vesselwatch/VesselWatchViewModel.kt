package gov.wa.wsdot.android.wsdot.ui.ferries.vesselwatch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import gov.wa.wsdot.android.wsdot.db.ferries.TerminalAlert
import gov.wa.wsdot.android.wsdot.db.ferries.Vessel
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.repository.VesselRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import gov.wa.wsdot.android.wsdot.repository.FerriesRepository
import gov.wa.wsdot.android.wsdot.ui.highwayAlerts.HighwayAlertViewModel.AlertQuery
import javax.inject.Inject

class VesselWatchViewModel @Inject constructor(vesselRepository: VesselRepository, cameraRepository: CameraRepository, ferriesRepository: FerriesRepository) : ViewModel() {

    private val vesselRepo = vesselRepository

    private val _showCameras: MutableLiveData<Boolean> = MutableLiveData()
    private val _showVessels: MutableLiveData<Boolean> = MutableLiveData()
    private val _showLabels: MutableLiveData<Boolean> = MutableLiveData()
    private val _showTerminals: MutableLiveData<Boolean> = MutableLiveData()
    private val _showTrafficLayer: MutableLiveData<Boolean> = MutableLiveData()

    private val _terminalQuery: MutableLiveData<terminalAlertsQuery> = MutableLiveData()
    private val _alertQuery: MutableLiveData<AlertQuery> = MutableLiveData()

    val terminals = ferriesRepository.loadTerminals()

    val terminal: LiveData<Resource<TerminalAlert>> = _alertQuery.switchMap { input ->
        input.ifExists {
            ferriesRepository.loadTerminal(it)
        }
    }

    fun refresh(terminalId1: Int) {
        val terminalId = _terminalQuery.value?.terminalId
        if (terminalId != null) {
            _terminalQuery.value = terminalAlertsQuery(terminalId, true)
        }
    }

    data class terminalAlertsQuery(val terminalId: Int, val forceRefresh: Boolean) {
        fun <T> ifExists(f: (Int, Boolean) -> LiveData<T>): LiveData<T> {
            return f(terminalId, forceRefresh)
        }
    }

    fun setShowCameras(showCameras: Boolean) {
        _showCameras.value = showCameras
    }

    fun setShowVessels(showVessels: Boolean) {
        _showVessels.value = showVessels
    }

    fun setShowLabels(showLabels: Boolean) {
        _showLabels.value = showLabels
    }

    fun setShowTerminals(showTerminals: Boolean) {
        _showTerminals.value = showTerminals
    }

    fun setTrafficLayer(showTrafficLayer: Boolean) {
        _showTrafficLayer.value = showTrafficLayer
    }


    val showCameras: LiveData<Boolean>
        get() = _showCameras


    val cameras = cameraRepository.loadCamerasOnRoad("Ferries", false)

    private val _vesselId: MutableLiveData<VesselId> = MutableLiveData()
    val vesselId: LiveData<VesselId>
        get() = _vesselId

    // mediator handles resubscribe on refresh
    val vessels = MediatorLiveData<Resource<List<Vessel>>>()

    private var vesselsLiveData : LiveData<Resource<List<Vessel>>> = vesselRepository.loadVessels(true)

    init {
        vessels.addSource(vesselsLiveData) { vessels.value = it }
    }

    fun refresh() {
        vessels.removeSource(vesselsLiveData)
        vesselsLiveData = vesselRepo.loadVessels(true)
        vessels.addSource(vesselsLiveData) { vessels.value = it }
    }

    data class VesselId(val vesselId: Int, val needsRefresh: Boolean)

}