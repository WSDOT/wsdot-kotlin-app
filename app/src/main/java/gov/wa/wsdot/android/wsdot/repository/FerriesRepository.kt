package gov.wa.wsdot.android.wsdot.repository

import android.util.Log
import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.ferries.FerryScheduleDao
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerryScheduleResponse
import gov.wa.wsdot.android.wsdot.db.ferries.FerryAlert
import gov.wa.wsdot.android.wsdot.db.ferries.FerryAlertDao
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.network.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FerriesRepository  @Inject constructor(
    private val webservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val ferryScheduleDao: FerryScheduleDao,
    private val ferryAlertDao: FerryAlertDao
) {

    fun loadSchedule(): LiveData<Resource<List<FerrySchedule>>> {

        return object : NetworkBoundResource<List<FerrySchedule>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveFullSchedule(item)

            override fun shouldFetch(data: List<FerrySchedule>?): Boolean {

                return true
                // TODO: Caching time
                return data == null || data.isEmpty() // || repoListRateLimit.shouldFetch(owner)
            }

            override fun loadFromDb() = ferryScheduleDao.loadSchedules()

            override fun createCall() = webservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadFerryAlerts(forRoute: Int): LiveData<Resource<List<FerryAlert>>> {

        return object : NetworkBoundResource<List<FerryAlert>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveFullSchedule(item)

            override fun shouldFetch(data: List<FerryAlert>?): Boolean {

                return true
                // TODO: Caching time
                return data == null || data.isEmpty() // || repoListRateLimit.shouldFetch(owner)
            }

            override fun loadFromDb() = ferryAlertDao.loadAlertsById(forRoute)

            override fun createCall() = webservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    private fun saveFullSchedule(schedulesResponse: List<FerryScheduleResponse>) {

        var dbSchedulesList = arrayListOf<FerrySchedule>()
        var dbAlertList = arrayListOf<FerryAlert>()

        for (scheduleResponse in schedulesResponse) {

            var schedule = FerrySchedule(
                scheduleResponse.routeId,
                scheduleResponse.description,
                scheduleResponse.crossingTime,
                scheduleResponse.cacheDate
            )
            dbSchedulesList.add(schedule)


            for (scheduleAlertResponse in scheduleResponse.alerts) {
                var alert = FerryAlert(
                    scheduleAlertResponse.alertId,
                    scheduleAlertResponse.fullTitle,
                    schedule.routeId,
                    scheduleAlertResponse.description,
                    scheduleAlertResponse.fullText,
                    scheduleAlertResponse.publishDate
                )
                dbAlertList.add(alert)
            }


        }

        ferryScheduleDao.insertSchedules(dbSchedulesList)
        ferryAlertDao.insertAlerts(dbAlertList)

    }

}