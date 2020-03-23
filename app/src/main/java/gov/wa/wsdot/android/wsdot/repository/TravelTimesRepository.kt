package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.traffic.TravelTimesResponse
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTime
import gov.wa.wsdot.android.wsdot.db.traveltimes.TravelTimeDao
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.model.common.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.model.common.Resource
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TravelTimesRepository @Inject constructor(
    private val dataWebservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val travelTimeDao: TravelTimeDao
) {

    fun loadTravelTimes(forceRefresh: Boolean): LiveData<Resource<List<TravelTime>>> {

        return object : NetworkBoundResource<List<TravelTime>, List<TravelTimesResponse>>(appExecutors) {

            override fun saveCallResult(items: List<TravelTimesResponse>) = saveTravelTimes(items)

            override fun shouldFetch(data: List<TravelTime>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 10)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = travelTimeDao.loadTravelTimes()

            override fun createCall() = dataWebservice.getTravelTimes()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadTravelTimesWithQuery(queryText: String, forceRefresh: Boolean): LiveData<Resource<List<TravelTime>>> {

        return object : NetworkBoundResource<List<TravelTime>, List<TravelTimesResponse>>(appExecutors) {

            override fun saveCallResult(items: List<TravelTimesResponse>) = saveTravelTimes(items)

            override fun shouldFetch(data: List<TravelTime>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 10)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = travelTimeDao.loadTravelTimesWithQueryText(queryText)

            override fun createCall() = dataWebservice.getTravelTimes()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadTravelTime(travelTimeId: Int): LiveData<Resource<TravelTime>> {

        return object : NetworkBoundResource<TravelTime, List<TravelTimesResponse>>(appExecutors) {

            override fun saveCallResult(items: List<TravelTimesResponse>) = saveTravelTimes(items)

            override fun shouldFetch(data: TravelTime?): Boolean {

                var update = false

                if (data != null){
                    if (TimeUtils.isOverXMinOld(data.localCacheDate, x = 10)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return update
            }

            override fun loadFromDb() = travelTimeDao.loadTravelTime(travelTimeId)

            override fun createCall() = dataWebservice.getTravelTimes()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadTravelTimesWithIDs(ids: List<Int>, forceRefresh: Boolean): LiveData<Resource<List<TravelTime>>>  {
        return object : NetworkBoundResource<List<TravelTime>, List<TravelTimesResponse>>(appExecutors) {

            override fun saveCallResult(items: List<TravelTimesResponse>) = saveTravelTimes(items)

            override fun shouldFetch(data: List<TravelTime>?): Boolean {

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

            override fun loadFromDb() = travelTimeDao.loadTravelTimesWithIds(ids)

            override fun createCall() = dataWebservice.getTravelTimes()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadFavoriteTravelTimes(forceRefresh: Boolean): LiveData<Resource<List<TravelTime>>> {

        return object : NetworkBoundResource<List<TravelTime>, List<TravelTimesResponse>>(appExecutors) {

            override fun saveCallResult(items: List<TravelTimesResponse>) = saveTravelTimes(items)

            override fun shouldFetch(data: List<TravelTime>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 10)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = travelTimeDao.loadFavoriteTravelTimes()

            override fun createCall() = dataWebservice.getTravelTimes()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun updateFavorite(travelTimeId: Int, isFavorite: Boolean) {
        appExecutors.diskIO().execute {
            travelTimeDao.updateFavorite(travelTimeId, isFavorite)
        }
    }

    private fun saveTravelTimes(travelTimes: List<TravelTimesResponse>) {

        var dbTravelTimeList = arrayListOf<TravelTime>()

        for (travelTimeItem in travelTimes) {

            val travelTime = TravelTime(
                travelTimeItem.travelTimeId,
                travelTimeItem.title,
                travelTimeItem.via,
                travelTimeItem.status,
                travelTimeItem.avgTime,
                travelTimeItem.currentTime,
                travelTimeItem.miles,
                travelTimeItem.startLocationLatitude,
                travelTimeItem.startLocationLongitude,
                travelTimeItem.endLocationLatitude,
                travelTimeItem.endLocationLongitude,
                parseTravelTimeDate(travelTimeItem.updated),
                Date(),
                favorite = false,
                remove = false
            )
            dbTravelTimeList.add(travelTime)
        }
        travelTimeDao.updateTravelTimes(dbTravelTimeList)
    }

    private fun parseTravelTimeDate(dateString: String): Date {
        val parseDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm a", Locale.ENGLISH) //e.g. "2019-08-27 08:40 AM"
        parseDateFormat.timeZone = TimeZone.getTimeZone("America/Los_Angeles")
        return try  {
            parseDateFormat.parse(dateString)!!
        } catch (e: ParseException) {
            Date()
        }
    }

}