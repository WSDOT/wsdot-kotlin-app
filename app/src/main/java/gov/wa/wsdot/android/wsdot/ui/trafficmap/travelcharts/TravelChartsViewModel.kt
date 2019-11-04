package gov.wa.wsdot.android.wsdot.ui.trafficmap.travelcharts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.api.response.EventStatusResponse
import gov.wa.wsdot.android.wsdot.api.response.traffic.TravelChartsStatusResponse
import gov.wa.wsdot.android.wsdot.repository.EventBannerRepository
import gov.wa.wsdot.android.wsdot.repository.TravelChartsRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject

class TravelChartsViewModel @Inject constructor(travelChartsRepository: TravelChartsRepository) : ViewModel() {
    val travelChartsStatus: LiveData<Resource<TravelChartsStatusResponse>> = travelChartsRepository.getTravelChartsStatus()
}
