package gov.wa.wsdot.android.wsdot.api

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerrySpacesResponse
import gov.wa.wsdot.android.wsdot.api.response.ferries.VesselResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//http://www.wsdot.wa.gov/ferries/api/terminals/rest/terminalsailingspace/" + String(departingId) + "?apiaccesscode=" + ApiKeys.getWSDOTKey()


interface WsdotApiService {

    @GET("ferries/api/terminals/rest/terminalsailingspace/{departingId}")
    fun getFerrySailingSpaces(
        @Path("departingId") departingId: Int,
        @Query("apiaccesscode") apiKey: String
    ): LiveData<ApiResponse<FerrySpacesResponse>>

    @GET("ferries/api/vessels/rest/vessellocations")
    fun getFerryVessels(
        @Query("apiaccesscode") apiKey: String
    ): LiveData<ApiResponse<List<VesselResponse>>>


}