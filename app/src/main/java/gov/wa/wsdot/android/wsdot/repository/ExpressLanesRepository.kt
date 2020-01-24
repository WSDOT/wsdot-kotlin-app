package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.ApiResponse
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.traffic.ExpressLanesStatusResponse
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.network.NetworkResource
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpressLanesRepository @Inject constructor(
    private val dataService: WebDataService,
    private val appExecutors: AppExecutors
) {
    fun getExpressLanesStatus(): LiveData<Resource<ExpressLanesStatusResponse>> {
        return object : NetworkResource<ExpressLanesStatusResponse, ExpressLanesStatusResponse>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ExpressLanesStatusResponse>> {
                return dataService.getExpressLanesStatus()
            }
        }.asLiveData()
    }
}