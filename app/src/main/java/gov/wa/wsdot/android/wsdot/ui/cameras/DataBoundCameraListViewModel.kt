package gov.wa.wsdot.android.wsdot.ui.cameras

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.db.traffic.Camera
import gov.wa.wsdot.android.wsdot.model.common.Resource

interface DataBoundCameraListViewModel {
    val cameras: LiveData<Resource<List<Camera>>>
    fun refresh()
}