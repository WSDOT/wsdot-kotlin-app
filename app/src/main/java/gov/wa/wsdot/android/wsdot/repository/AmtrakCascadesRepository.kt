package gov.wa.wsdot.android.wsdot.repository

import android.util.Log
import gov.wa.wsdot.android.wsdot.api.WsdotApiService
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton
import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.ApiResponse
import gov.wa.wsdot.android.wsdot.api.response.amtrakcascades.AmtrakScheduleResponse
import gov.wa.wsdot.android.wsdot.model.amtrakCascades.AmtrakStop
import gov.wa.wsdot.android.wsdot.util.ApiKeys
import gov.wa.wsdot.android.wsdot.util.network.NetworkResource
import gov.wa.wsdot.android.wsdot.util.network.Resource

@Singleton
class AmtrakCascadesRepository @Inject constructor(
    private val wsdotWebservice: WsdotApiService,
    private val appExecutors: AppExecutors
) {

    fun getDestinations(statusDate: String, fromLocation: String, toLocation: String = "N/A", trainNumber: String = "-1"): LiveData<Resource<List<AmtrakScheduleResponse>>> {
        return object : NetworkResource<List<AmtrakScheduleResponse>, List<AmtrakScheduleResponse>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<List<AmtrakScheduleResponse>>> {

                Log.e("debug", "creating call..")
                Log.e("debug", "date")
                Log.e("debug", statusDate)
                Log.e("debug", "from")
                Log.e("debug", fromLocation)
                Log.e("debug", "to")
                Log.e("debug", toLocation)
                Log.e("debug", "train num")
                Log.e("debug", trainNumber)

                return wsdotWebservice.getAmtrackSchedule(ApiKeys.WSDOT_KEY, statusDate, trainNumber, fromLocation, toLocation)
            }
        }.asLiveData()
    }
}