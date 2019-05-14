package gov.wa.wsdot.android.wsdot.api

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET

interface WebDataService {

    /**
     * @GET declares an HTTP GET request
     */
    @GET("WSFRouteSchedules.js")
    fun getFerrySchedules(): LiveData<ApiResponse<List<String>>>
}