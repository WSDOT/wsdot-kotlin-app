package gov.wa.wsdot.android.wsdot.ui.eventbanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.api.response.EventStatusResponse
import gov.wa.wsdot.android.wsdot.repository.EventBannerRepository
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject

class EventBannerViewModel @Inject constructor(eventBannerRepository: EventBannerRepository) : ViewModel() {

    private val repo = eventBannerRepository
    private val eventStatusResponse = MediatorLiveData<Resource<EventStatusResponse>>()
    var eventStatus: LiveData<Resource<EventStatusResponse>> = eventBannerRepository.getEventStatus()
    
    // refreshes the repo source
    fun refresh() {
        eventStatusResponse.removeSource(eventStatus)
        eventStatus = repo.getEventStatus()
        eventStatusResponse.addSource(eventStatus) { eventStatusResponse.value = it }
    }
}
