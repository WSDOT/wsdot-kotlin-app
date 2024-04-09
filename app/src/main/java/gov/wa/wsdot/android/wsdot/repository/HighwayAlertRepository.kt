package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLngBounds
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.traffic.HighwayAlertsResponse
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlertDao
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.model.common.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.model.common.Resource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HighwayAlertRepository @Inject constructor(
    private val dataWebservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val highwayAlertDao: HighwayAlertDao
) {

    fun loadHighwayAlerts(forceRefresh: Boolean): LiveData<Resource<List<HighwayAlert>>> {

        return object : NetworkBoundResource<List<HighwayAlert>, HighwayAlertsResponse>(appExecutors) {

            override fun saveCallResult(item: HighwayAlertsResponse) = saveHighwayAlerts(item)

            override fun shouldFetch(data: List<HighwayAlert>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 5)) {
                        update = true
                    }
                } else {
                    update = true
                }


                return forceRefresh || update
            }

            override fun loadFromDb() = highwayAlertDao.loadHighwayAlerts()

            override fun createCall() = dataWebservice.getHighwayAlerts()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadStatewideAndHighestImpactHighwayAlerts(forceRefresh: Boolean): LiveData<Resource<List<HighwayAlert>>> {

        return object : NetworkBoundResource<List<HighwayAlert>, HighwayAlertsResponse>(appExecutors) {

            override fun saveCallResult(item: HighwayAlertsResponse) = saveHighwayAlerts(item)

            override fun shouldFetch(data: List<HighwayAlert>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 5)) {
                        update = true
                    }
                } else {
                    update = true
                }


                return forceRefresh || update
            }

            override fun loadFromDb() = highwayAlertDao.loadStatewideAndHighestImpactAlerts()

            override fun createCall() = dataWebservice.getHighwayAlerts()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadHighwayAlertsInBounds(bounds: LatLngBounds, forceRefresh: Boolean): LiveData<Resource<List<HighwayAlert>>> {

        return object : NetworkBoundResource<List<HighwayAlert>, HighwayAlertsResponse>(appExecutors) {

            override fun saveCallResult(item: HighwayAlertsResponse) = saveHighwayAlerts(item)

            override fun shouldFetch(data: List<HighwayAlert>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 5)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = highwayAlertDao.loadHighwayAlertsInBounds(bounds.southwest.latitude, bounds.northeast.latitude, bounds.southwest.longitude, bounds.northeast.longitude)

            override fun createCall() = dataWebservice.getHighwayAlerts()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadHighwayAlert(alertId: Int): LiveData<Resource<HighwayAlert>> {

        return object : NetworkBoundResource<HighwayAlert, HighwayAlertsResponse>(appExecutors) {

            override fun saveCallResult(item: HighwayAlertsResponse) = saveHighwayAlerts(item)

            override fun shouldFetch(data: HighwayAlert?): Boolean {

                var update = false

                if (data != null){
                    if (TimeUtils.isOverXMinOld(data.localCacheDate, x = 5)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return update
            }

            override fun loadFromDb() = highwayAlertDao.loadHighwayAlert(alertId)

            override fun createCall() = dataWebservice.getHighwayAlerts()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    private fun saveHighwayAlerts(alertsResponse: HighwayAlertsResponse) {

        val dbAlertList = arrayListOf<HighwayAlert>()

        for (alertItem in alertsResponse.alerts.items) {

            val alert = HighwayAlert(
                alertItem.alertId,
                alertItem.HeadlineDescription,
                alertItem.startRoadwayLocation.roadName,
                alertItem.priority,
                alertItem.travelCenterPriorityId,
                alertItem.eventCategory,
                alertItem.eventCategoryType,
                alertItem.eventCategoryTypeDescription,
                alertItem.displayLatitude,
                alertItem.displayLongitude,
                alertItem.startRoadwayLocation.latitude,
                alertItem.startRoadwayLocation.longitude,
                alertItem.startRoadwayLocation.direction,
                alertItem.endRoadwayLocation.latitude,
                alertItem.endRoadwayLocation.longitude,
                Date(alertItem.lastUpdatedTime.substring(6, 19).toLong()),
                Date()
            )
            dbAlertList.add(alert)
        }

        highwayAlertDao.updateHighwayAlerts(dbAlertList)
    }
}
