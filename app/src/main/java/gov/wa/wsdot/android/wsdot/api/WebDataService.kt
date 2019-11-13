package gov.wa.wsdot.android.wsdot.api

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.response.EventStatusResponse
import gov.wa.wsdot.android.wsdot.api.response.borderwaits.BorderCrossingResponse
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerryScheduleResponse
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse
import gov.wa.wsdot.android.wsdot.api.response.notifications.NotificationTopicResponse
import gov.wa.wsdot.android.wsdot.api.response.notifications.NotificationVersionResponse
import gov.wa.wsdot.android.wsdot.api.response.tollrates.TollRateTableResponse
import gov.wa.wsdot.android.wsdot.api.response.traffic.CamerasResponse
import gov.wa.wsdot.android.wsdot.api.response.traffic.HighwayAlertsResponse
import gov.wa.wsdot.android.wsdot.api.response.traffic.TravelChartsStatusResponse
import gov.wa.wsdot.android.wsdot.api.response.traffic.TravelTimesResponse
import gov.wa.wsdot.android.wsdot.api.response.travelerinfo.NewsReleaseResponse
import gov.wa.wsdot.android.wsdot.db.notificationtopic.NotificationTopic
import retrofit2.http.GET

interface WebDataService {

    /**
     * @GET declares an HTTP GET request
     */
    @GET("WSFRouteSchedules.js")
    fun getFerrySchedules(): LiveData<ApiResponse<List<FerryScheduleResponse>>>

    @GET("MountainPassConditions.js")
    fun getMountainPassReports(): LiveData<ApiResponse<MountainPassResponse>>

    @GET("HighwayAlerts.js")
    fun getHighwayAlerts(): LiveData<ApiResponse<HighwayAlertsResponse>>

    @GET("Cameras.js")
    fun getCameras(): LiveData<ApiResponse<CamerasResponse>>

    @GET("TravelTimesv2.js")
    fun getTravelTimes(): LiveData<ApiResponse<List<TravelTimesResponse>>>

    @GET("News.js")
    fun getNewsItems(): LiveData<ApiResponse<NewsReleaseResponse>>

    @GET("BorderCrossings.js")
    fun getBorderCrossingItems(): LiveData<ApiResponse<BorderCrossingResponse>>

    @GET("StaticTollRates.js")
    fun getTollRateTables(): LiveData<ApiResponse<TollRateTableResponse>>

    @GET("EventStatusTEST.js")
    fun getEventStatus(): LiveData<ApiResponse<EventStatusResponse>>

    @GET("TravelCharts.js")
    fun getTravelChartsStatus(): LiveData<ApiResponse<TravelChartsStatusResponse>>

    @GET("NotificationTopics.js")
    fun getNotificationTopics(): LiveData<ApiResponse<NotificationTopicResponse>>

    @GET("NotificationTopicsVersion.js")
    fun getNotificationTopicVersion(): LiveData<ApiResponse<NotificationVersionResponse>>

}