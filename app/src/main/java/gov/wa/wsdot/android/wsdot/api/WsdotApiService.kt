package gov.wa.wsdot.android.wsdot.api

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.response.amtrakcascades.AmtrakScheduleResponse
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerrySpacesResponse
import gov.wa.wsdot.android.wsdot.api.response.ferries.VesselResponse
import gov.wa.wsdot.android.wsdot.api.response.socialmedia.TwitterResponse
import gov.wa.wsdot.android.wsdot.api.response.tollrates.TollTripResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

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

    @GET("news/socialroom/posts/twitter/{accountName}")
    fun getWSDOTTweets(
        @Path("accountName") accountName: String
    ): LiveData<ApiResponse<List<TwitterResponse>>>

    @Headers("Content-Type:application/json")
    @GET("traffic/api/TollRates/TollRatesREST.svc/GetTollRatesAsJson")
    fun getTollTrips(
        @Query("accesscode") apiKey: String
    ): LiveData<ApiResponse<List<TollTripResponse>>>

    @GET("/traffic/api/amtrak/Schedulerest.svc/GetScheduleAsJson")
    fun getAmtrackSchedule(
        @Query("accesscode") apiKey: String,
        @Query("StatusDate") statusDate: String,
        @Query("TrainNumber") trainNumber: String,
        @Query("FromLocation") fromLocation: String,
        @Query("ToLocation") toLocation: String
    ): LiveData<ApiResponse<List<AmtrakScheduleResponse>>>


}