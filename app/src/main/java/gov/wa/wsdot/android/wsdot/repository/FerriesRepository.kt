package gov.wa.wsdot.android.wsdot.repository

import android.util.Log
import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerryScheduleResponse
import gov.wa.wsdot.android.wsdot.db.ferries.*
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.network.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.util.network.Resource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FerriesRepository  @Inject constructor(
    private val webservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val ferryScheduleDao: FerryScheduleDao,
    private val ferrySailingDao: FerrySailingDao,
    private val ferryAlertDao: FerryAlertDao
) {

    fun loadSchedules(forceRefresh: Boolean): LiveData<Resource<List<FerrySchedule>>> {

        return object : NetworkBoundResource<List<FerrySchedule>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveFullSchedule(item)

            override fun shouldFetch(data: List<FerrySchedule>?): Boolean {
                Log.e("debug", "forcing refresh...")
                Log.e("debug", data.toString())

                return forceRefresh || data?.isEmpty() ?: true
                // TODO: Caching time
                // repoListRateLimit.shouldFetch(owner)
            }

            override fun loadFromDb() = ferryScheduleDao.loadSchedules()

            override fun createCall() = webservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadSchedule(routeId: Int, forceRefresh: Boolean): LiveData<Resource<FerrySchedule>> {

        return object : NetworkBoundResource<FerrySchedule, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveFullSchedule(item)

            override fun shouldFetch(data: FerrySchedule?): Boolean {
                Log.e("debug", "forcing refresh...")
                Log.e("debug", data.toString())

                return forceRefresh
                // TODO: Caching time
                // repoListRateLimit.shouldFetch(owner)
            }

            override fun loadFromDb() = ferryScheduleDao.loadSchedule(routeId)

            override fun createCall() = webservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadSailings(routeId: Int, departingId: Int, arrivingId: Int, sailingDate: Date, forceRefresh: Boolean): LiveData<Resource<List<FerrySailing>>> {

        return object : NetworkBoundResource<List<FerrySailing>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveFullSchedule(item)

            override fun shouldFetch(data: List<FerrySailing>?): Boolean {

                return forceRefresh || data?.isEmpty() ?: true
                // TODO: Caching time
            }

            override fun loadFromDb() = ferrySailingDao.loadSailings(routeId, departingId, arrivingId, sailingDate)

            override fun createCall() = webservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()

    }

    fun loadTerminalCombos(routeId: Int, forceRefresh: Boolean): LiveData<Resource<List<TerminalCombo>>> {

        return object : NetworkBoundResource<List<TerminalCombo>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveFullSchedule(item)

            override fun shouldFetch(data: List<TerminalCombo>?): Boolean {
                return forceRefresh
            }

            override fun loadFromDb() = ferrySailingDao.loadTerminalCombos(routeId)

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
        var dbSailingsList = arrayListOf<FerrySailing>()
        var dbAlertList = arrayListOf<FerryAlert>()

        for (scheduleResponse in schedulesResponse) {

            val cacheDate = Date(scheduleResponse.cacheDate.substring(6, 19).toLong()) // TODO: Test failure and new API date format

            var schedule = FerrySchedule(
                scheduleResponse.routeId,
                scheduleResponse.description,
                scheduleResponse.crossingTime,
                cacheDate
            )
            dbSchedulesList.add(schedule)


            for (routeSchedules in scheduleResponse.schedules) {
                for (sailing in routeSchedules.sailings) {

                    for (sailingTime in sailing.times) {

                        val sailingItem = FerrySailing(0,
                            scheduleResponse.routeId,
                            Date(routeSchedules.date.substring(6, 19).toLong()),
                            sailing.departingTerminalID,
                            sailing.departingTerminalName,
                            sailing.arrivingTerminalID,
                            sailing.arrivingTerminalName,
                            emptyList(), // TODO: annotations
                            Date(sailingTime.departingTime.substring(6, 19).toLong()),
                            Date(), // TODO: can be null
                            cacheDate
                        )
                        dbSailingsList.add(sailingItem)

                    }
                }
            }


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
        ferrySailingDao.insertSailings(dbSailingsList)
        ferryAlertDao.insertAlerts(dbAlertList)

    }

}