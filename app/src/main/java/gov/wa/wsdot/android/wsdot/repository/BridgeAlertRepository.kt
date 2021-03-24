package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLngBounds
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.traffic.HighwayAlertsResponse
import gov.wa.wsdot.android.wsdot.api.response.travelerinfo.BridgeAlertResponse
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlert
import gov.wa.wsdot.android.wsdot.db.traffic.HighwayAlertDao
import gov.wa.wsdot.android.wsdot.db.travelerinfo.BridgeAlert
import gov.wa.wsdot.android.wsdot.db.travelerinfo.BridgeAlertDao
import gov.wa.wsdot.android.wsdot.model.common.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.model.common.Resource
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BridgeAlertRepository @Inject constructor(
    private val dataWebservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val bridgeAlertDao: BridgeAlertDao
) {

    fun loadBridgeAlerts(forceRefresh: Boolean): LiveData<Resource<List<BridgeAlert>>> {

        return object : NetworkBoundResource<List<BridgeAlert>, List<BridgeAlertResponse>>(appExecutors) {

            override fun saveCallResult(items: List<BridgeAlertResponse>) = saveBridgeAlerts(items)

            override fun shouldFetch(data: List<BridgeAlert>?): Boolean {

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

            override fun loadFromDb() = bridgeAlertDao.loadBridgeAlerts()

            override fun createCall() = dataWebservice.getBridgeAlerts()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadBridgeAlert(alertId: Int): LiveData<Resource<BridgeAlert>> {

        return object : NetworkBoundResource<BridgeAlert, List<BridgeAlertResponse>>(appExecutors) {

            override fun saveCallResult(item: List<BridgeAlertResponse>) = saveBridgeAlerts(item)

            override fun shouldFetch(data: BridgeAlert?): Boolean {

                var update = false

                if (data != null){
                    if (TimeUtils.isOverXMinOld(data.localCacheDate, x = 10080)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return update
            }

            override fun loadFromDb() = bridgeAlertDao.loadBridgeAlert(alertId)

            override fun createCall() = dataWebservice.getBridgeAlerts()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    private fun saveBridgeAlerts(bridgeAlertResponse: List<BridgeAlertResponse>) {

        val dbAlertList = arrayListOf<BridgeAlert>()

        for (alertItem in bridgeAlertResponse) {

            val alert = BridgeAlert(
                alertItem.bridgeOpeningId,
                alertItem.bridgeLocation.description,
                alertItem.status,
                alertItem.bridgeLocation.latitude,
                alertItem.bridgeLocation.longitude,
                alertItem.eventText,
                parseBridgeDate(alertItem.openingTime),
                Date()
            )
            dbAlertList.add(alert)
        }
        bridgeAlertDao.updateBridgeAlerts(dbAlertList)
    }

    private fun parseBridgeDate(bridgeDate: String): Date? {
        return try {
            val parseDateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH) //e.g. "2021-03-12T15:00:00"
            parseDateFormat.timeZone = TimeZone.getTimeZone("America/Los_Angeles")
            parseDateFormat.parse(bridgeDate)
        } catch (e: Exception) {
            null
        }
    }

}
