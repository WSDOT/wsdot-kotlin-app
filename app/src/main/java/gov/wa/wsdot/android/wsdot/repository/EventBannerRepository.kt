package gov.wa.wsdot.android.wsdot.repository

import gov.wa.wsdot.android.wsdot.util.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton
import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.ApiResponse
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.EventStatusResponse
import gov.wa.wsdot.android.wsdot.model.common.NetworkResource
import gov.wa.wsdot.android.wsdot.model.common.Resource

@Singleton
class EventBannerRepository @Inject constructor(
    private val dataService: WebDataService,
    private val appExecutors: AppExecutors
) {
    fun getEventStatus(): LiveData<Resource<EventStatusResponse>> {
        return object : NetworkResource<EventStatusResponse, EventStatusResponse>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<EventStatusResponse>> {
                return dataService.getEventStatus()
            }
        }.asLiveData()
    }
}