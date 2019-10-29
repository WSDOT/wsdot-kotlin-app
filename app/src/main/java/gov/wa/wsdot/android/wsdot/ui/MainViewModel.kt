package gov.wa.wsdot.android.wsdot.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.api.response.EventStatusResponse
import gov.wa.wsdot.android.wsdot.repository.EventBannerRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class MainViewModel @Inject constructor(eventBannerRepository: EventBannerRepository) : ViewModel() {
    val eventStatus: LiveData<Resource<EventStatusResponse>> = eventBannerRepository.getEventStatus()
}
