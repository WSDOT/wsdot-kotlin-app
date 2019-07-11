package gov.wa.wsdot.android.wsdot.ui.cameras

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.repository.CameraRepository
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

interface DataBoundCameraListViewModel {
    val cameras: LiveData<Resource<List<Camera>>>
    fun refresh()
}