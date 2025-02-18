package gov.wa.wsdot.android.wsdot.api

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.response.EventStatusResponse
import gov.wa.wsdot.android.wsdot.api.response.borderwaits.BorderCrossingResponse
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerryScheduleResponse
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse
import gov.wa.wsdot.android.wsdot.api.response.notifications.NotificationTopicResponse
import gov.wa.wsdot.android.wsdot.api.response.notifications.NotificationVersionResponse
import gov.wa.wsdot.android.wsdot.api.response.tollrates.TollRateTableResponse
import gov.wa.wsdot.android.wsdot.api.response.traffic.*
import gov.wa.wsdot.android.wsdot.api.response.travelerinfo.BridgeAlertResponse
import gov.wa.wsdot.android.wsdot.api.response.travelerinfo.NewsReleaseResponse
import retrofit2.http.GET

interface WebDataService {
    /**
     * @GET declares an HTTP GET request
     */
    @GET("WSFRouteSchedules.json")
    fun getFerrySchedules(): LiveData<ApiResponse<List<FerryScheduleResponse>>>

    @GET("MountainPassConditions.js")
    fun getMountainPassReports(): LiveData<ApiResponse<MountainPassResponse>>

    @GET("HighwayAlerts.js")
    fun getHighwayAlerts(): LiveData<ApiResponse<HighwayAlertsResponse>>

    @GET("BridgeOpenings.js")
    fun getBridgeAlerts(): LiveData<ApiResponse<List<BridgeAlertResponse>>>

    @GET("Cameras.js")
    fun getCameras(): LiveData<ApiResponse<CamerasResponse>>

    @GET("ExpressLanes.js")
    fun getExpressLanesStatus(): LiveData<ApiResponse<ExpressLanesStatusResponse>>

    @GET("TravelTimesv2.js")
    fun getTravelTimes(): LiveData<ApiResponse<List<TravelTimesResponse>>>

    @GET("News.js")
    fun getNewsItems(): LiveData<ApiResponse<NewsReleaseResponse>>

    @GET("BorderCrossings.js")
    fun getBorderCrossingItems(): LiveData<ApiResponse<BorderCrossingResponse>>

    @GET("StaticTollRates.js")
    fun getTollRateTables(): LiveData<ApiResponse<TollRateTableResponse>>

    @GET("EventStatus.js")
    fun getEventStatus(): LiveData<ApiResponse<EventStatusResponse>>

    @GET("TravelCharts.js")
    fun getTravelChartsStatus(): LiveData<ApiResponse<TravelChartsStatusResponse>>

    @GET("NotificationTopics.js")
    fun getNotificationTopics(): LiveData<ApiResponse<NotificationTopicResponse>>

    @GET("NotificationTopicsVersion.js")
    fun getNotificationTopicVersion(): LiveData<ApiResponse<NotificationVersionResponse>>

}