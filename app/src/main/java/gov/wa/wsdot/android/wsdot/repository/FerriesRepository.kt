package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.WsdotApiService
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerryScheduleResponse
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerrySpacesResponse
import gov.wa.wsdot.android.wsdot.api.response.ferries.VesselResponse
import gov.wa.wsdot.android.wsdot.db.ferries.*
import gov.wa.wsdot.android.wsdot.util.ApiKeys
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.util.network.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.util.network.Resource
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList


@Singleton
class FerriesRepository @Inject constructor(
    private val dataWebservice: WebDataService,
    private val wsdotWebservice: WsdotApiService,
    private val appExecutors: AppExecutors,
    private val ferryScheduleDao: FerryScheduleDao,
    private val ferrySailingDao: FerrySailingDao,
    private val ferrySpaceDao: FerrySpaceDao,
    private val ferrySailingWithSpacesDao: FerrySailingWithSpacesDao,
    private val ferryAlertDao: FerryAlertDao
) {

    fun loadSchedules(forceRefresh: Boolean): LiveData<Resource<List<FerrySchedule>>> {

        return object : NetworkBoundResource<List<FerrySchedule>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveSchedule(item)

            override fun shouldFetch(data: List<FerrySchedule>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {

                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 15)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = ferryScheduleDao.loadSchedules()

            override fun createCall() = dataWebservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadSchedule(routeId: Int, forceRefresh: Boolean): LiveData<Resource<FerrySchedule>> {

        return object : NetworkBoundResource<FerrySchedule, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveSchedule(item)

            override fun shouldFetch(data: FerrySchedule?): Boolean {

                var update = false

                if (data != null) {

                    if (TimeUtils.isOverXMinOld(data.localCacheDate, x = 15)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = ferryScheduleDao.loadSchedule(routeId)

            override fun createCall() = dataWebservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadSailings(routeId: Int, departingId: Int, arrivingId: Int, sailingDate: Date, forceRefresh: Boolean): LiveData<Resource<List<FerrySailingWithSpaces>>> {

        return object : NetworkBoundResource<List<FerrySailingWithSpaces>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveSchedule(item)

            override fun shouldFetch(data: List<FerrySailingWithSpaces>?): Boolean {
                return forceRefresh || data?.isEmpty() ?: true
            }

            override fun loadFromDb() = ferrySailingWithSpacesDao.loadSailingsWithSpaces(routeId, departingId, arrivingId, sailingDate)

            override fun createCall() = dataWebservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadSpaces(routeId: Int, departingId: Int, arrivingId: Int, sailingDate: Date): LiveData<Resource<List<FerrySailingWithSpaces>>> {

        return object : NetworkBoundResource<List<FerrySailingWithSpaces>, FerrySpacesResponse>(appExecutors) {

            override fun saveCallResult(item: FerrySpacesResponse) = saveSailingSpaces(item)

            override fun shouldFetch(data: List<FerrySailingWithSpaces>?): Boolean {
                var update = false

                if (data != null) {
                    if (data.isNotEmpty()) {
                        if (TimeUtils.isOverXMinOld(data[0].cacheDate, x = 3)) {
                            update = true
                        }
                    }
                } else {
                    update = true
                }

                return update
            }

            override fun loadFromDb() = ferrySailingWithSpacesDao.loadSailingsWithSpaces(routeId, departingId, arrivingId, sailingDate)

            override fun createCall() = wsdotWebservice.getFerrySailingSpaces(departingId, apiKey = ApiKeys.WSDOT_KEY)

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()

    }


    fun loadScheduleRange(routeId: Int) = ferrySailingDao.loadScheduleDateRange(routeId)

    fun loadTerminalCombos(routeId: Int, forceRefresh: Boolean): LiveData<Resource<List<TerminalCombo>>> {

        return object : NetworkBoundResource<List<TerminalCombo>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveSchedule(item)

            override fun shouldFetch(data: List<TerminalCombo>?): Boolean {
                return forceRefresh
            }

            override fun loadFromDb() = ferrySailingDao.loadTerminalCombos(routeId)

            override fun createCall() = dataWebservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()

    }

    fun loadFerryAlerts(forRoute: Int): LiveData<Resource<List<FerryAlert>>> {

        return object : NetworkBoundResource<List<FerryAlert>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveAlerts(item)

            override fun shouldFetch(data: List<FerryAlert>?): Boolean {
                return true
            }

            override fun loadFromDb() = ferryAlertDao.loadAlertsById(forRoute)

            override fun createCall() = dataWebservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadFerryAlert(alertId: Int): LiveData<Resource<FerryAlert>> {

        return object : NetworkBoundResource<FerryAlert, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveAlerts(item)

            override fun shouldFetch(data: FerryAlert?): Boolean {
                return true
            }

            override fun loadFromDb() = ferryAlertDao.loadAlertById(alertId)

            override fun createCall() = dataWebservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadFavoriteSchedules(forceRefresh: Boolean): LiveData<Resource<List<FerrySchedule>>> {

        return object : NetworkBoundResource<List<FerrySchedule>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) = saveSchedule(item)

            override fun shouldFetch(data: List<FerrySchedule>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {

                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 15)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = ferryScheduleDao.loadFavoriteSchedules()

            override fun createCall() = dataWebservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun updateFavorite(routeId: Int, isFavorite: Boolean) {
        appExecutors.diskIO().execute {
            ferryScheduleDao.updateFavorite(routeId, isFavorite)
        }
    }

    private fun saveSchedule(schedulesResponse: List<FerryScheduleResponse>) {

        val dbSchedulesList = arrayListOf<FerrySchedule>()
        val dbSailingsList = arrayListOf<FerrySailing>()

        for (scheduleResponse in schedulesResponse) {

            val apiSailingDateFormat = SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.ENGLISH)
            val apiScheduleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

            var cacheDate: Date? = apiSailingDateFormat.parse(scheduleResponse.cacheDate)

            if (cacheDate == null) {
                cacheDate = Date()
            }

            var schedule = FerrySchedule(
                scheduleResponse.routeId,
                scheduleResponse.description,
                scheduleResponse.crossingTime,
                cacheDate,
                Date(),
                favorite = false,
                remove = false
            )
            dbSchedulesList.add(schedule)

            for (routeSchedules in scheduleResponse.schedules) {
                for (sailing in routeSchedules.sailings) {
                    for (sailingTime in sailing.times) {

                        // set up arrivingTime
                        var arrivingTime: Date? = null
                        if (sailingTime.arrivingTime != null) {
                            arrivingTime = apiSailingDateFormat.parse(sailingTime.arrivingTime)
                        }

                        // set up annotations
                        val annotations = ArrayList<String>()

                        for (annotationIndex: Int in sailingTime.annotationIndexes) {
                            annotations.add(sailing.annotations[annotationIndex])
                        }

                        val sailingDate = apiScheduleDateFormat.parse(routeSchedules.date)
                        val departTime = apiSailingDateFormat.parse(sailingTime.departingTime)

                        if (sailingDate != null && departTime != null) {

                            val sailingItem = FerrySailing(
                                scheduleResponse.routeId,
                                sailingDate,
                                sailing.departingTerminalID,
                                sailing.departingTerminalName,
                                sailing.arrivingTerminalID,
                                sailing.arrivingTerminalName,
                                annotations,
                                departTime,
                                arrivingTime,
                                cacheDate
                            )

                            dbSailingsList.add(sailingItem)
                        }

                    }
                }
            }
        }

        ferryScheduleDao.updateSchedules(dbSchedulesList)
        ferrySailingDao.updateSailings(dbSailingsList.distinct())

    }

    private fun saveAlerts(schedulesResponse: List<FerryScheduleResponse>) {

        val dbAlertList = arrayListOf<FerryAlert>()

        val apiSailingDateFormat = SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.ENGLISH)

        for (scheduleResponse in schedulesResponse) {
            for (scheduleAlertResponse in scheduleResponse.alerts) {
                dbAlertList.add(FerryAlert(
                    scheduleAlertResponse.alertId,
                    scheduleAlertResponse.fullTitle,
                    scheduleResponse.routeId,
                    scheduleAlertResponse.description,
                    scheduleAlertResponse.fullText,
                    apiSailingDateFormat.parse(scheduleAlertResponse.publishDate)!!
                ))
            }
        }

        ferryAlertDao.insertAlerts(dbAlertList.distinct())

    }

    private fun saveSailingSpaces(spaceResponse: FerrySpacesResponse?) {

        var dbSpacesList = arrayListOf<FerrySpace>()

        if (spaceResponse == null) {
            ferrySpaceDao.insertSpaces(dbSpacesList)
            return
        }

        val departingTerminalId = spaceResponse.terminalId

        for (departingSpaces in spaceResponse.departingSpaces) {

            val maxSpaces = departingSpaces.maxSpaceCount
            val departureTime = departingSpaces.departureTime

            for (arrivalSpaces in departingSpaces.spaceForArrivalTerminals) {

                val displayRes = arrivalSpaces.displayReservableSpace
                val displayDriveUp = arrivalSpaces.displayDriveUpSpace

                val resSpaces = arrivalSpaces.reservableSpaceCount
                val resColor = arrivalSpaces.reservableSpaceHexColor

                val spaces = arrivalSpaces.driveUpSpaceCount
                val color = arrivalSpaces.driveUpSpaceHexColor


                var spacesItem = FerrySpace(
                    departingTerminalId,
                    arrivalSpaces.terminalId,
                    displayDriveUp,
                    displayRes,
                    maxSpaces,
                    spaces,
                    resSpaces,
                    color,
                    resColor,
                    Date(departureTime.substring(6, 19).toLong()),
                    Date()
                )

                dbSpacesList.add(spacesItem)

                // Check for '->' because some entries in the data
                // have arrival terminals for sailings that aren't there
                if (arrivalSpaces.terminalName.contains("->")) {
                    for (arrivalTerminal in arrivalSpaces.arrivalTerminalIds) {
                        if (arrivalTerminal != arrivalSpaces.terminalId) {

                            spacesItem = FerrySpace(
                                departingTerminalId,
                                arrivalTerminal,
                                displayDriveUp,
                                displayRes,
                                maxSpaces,
                                spaces,
                                resSpaces,
                                color,
                                resColor,
                                Date(departureTime.substring(6, 19).toLong()),
                                Date()
                            )

                            dbSpacesList.add(spacesItem)
                        }
                    }
                }
            }
        }
        ferrySpaceDao.updateSpaces(dbSpacesList)
    }
}