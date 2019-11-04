package gov.wa.wsdot.android.wsdot.repository

import gov.wa.wsdot.android.wsdot.util.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton
import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.ApiResponse
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.EventStatusResponse
import gov.wa.wsdot.android.wsdot.api.response.traffic.TravelChartsStatusResponse
import gov.wa.wsdot.android.wsdot.util.network.NetworkResource
import gov.wa.wsdot.android.wsdot.util.network.Resource

@Singleton
class TravelChartsRepository @Inject constructor(
    private val dataService: WebDataService,
    private val appExecutors: AppExecutors
) {
    fun getTravelChartsStatus(): LiveData<Resource<TravelChartsStatusResponse>> {
        return object : NetworkResource<TravelChartsStatusResponse, TravelChartsStatusResponse>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<TravelChartsStatusResponse>> {
                return dataService.getTravelChartsStatus()
            }
        }.asLiveData()
    }
}