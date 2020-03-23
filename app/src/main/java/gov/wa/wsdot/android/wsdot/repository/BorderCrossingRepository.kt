package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.borderwaits.BorderCrossingResponse
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossing
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossingDao
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
class BorderCrossingRepository @Inject constructor(
    private val dataWebservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val borderCrossingDao: BorderCrossingDao
) {

    fun loadCrossingsForDirection(direction: String, forceRefresh: Boolean): LiveData<Resource<List<BorderCrossing>>> {

        return object : NetworkBoundResource<List<BorderCrossing>, BorderCrossingResponse>(appExecutors) {

            override fun saveCallResult(items: BorderCrossingResponse) = saveBorderCrossings(items)

            override fun shouldFetch(data: List<BorderCrossing>?): Boolean {

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

            override fun loadFromDb() = borderCrossingDao.loadCrossingsForDirection(direction)

            override fun createCall() = dataWebservice.getBorderCrossingItems()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadFavoriteCrossings(forceRefresh: Boolean): LiveData<Resource<List<BorderCrossing>>> {

        return object : NetworkBoundResource<List<BorderCrossing>, BorderCrossingResponse>(appExecutors) {

            override fun saveCallResult(items: BorderCrossingResponse) = saveBorderCrossings(items)

            override fun shouldFetch(data: List<BorderCrossing>?): Boolean {

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

            override fun loadFromDb() = borderCrossingDao.loadFavoriteBorderCrossings()

            override fun createCall() = dataWebservice.getBorderCrossingItems()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    private fun saveBorderCrossings(crossingResponse: BorderCrossingResponse) {

        var dbCrossingList = arrayListOf<BorderCrossing>()

        for (crossingItem in crossingResponse.waitTimes.items) {

            // Only show northbound until further notice.
            // Accuracy of southbound times cannot be guaranteed indefinitely.
            if (crossingItem.direction.toLowerCase(Locale.ENGLISH) == "northbound") {
                val crossing = BorderCrossing(
                    crossingId = crossingItem.id,
                    name = crossingItem.name,
                    direction = crossingItem.direction,
                    lane = crossingItem.lane,
                    route = crossingItem.route,
                    wait = crossingItem.wait,
                    updated = parseCrossingDate(crossingItem.updated),
                    localCacheDate = Date(),
                    favorite = false,
                    remove = false
                )
                dbCrossingList.add(crossing)
            }
        }

        borderCrossingDao.updateBorderCrossings(dbCrossingList)
    }



    fun updateFavorite(passId: Int, isFavorite: Boolean) {
        appExecutors.diskIO().execute {
            borderCrossingDao.updateFavorite(passId, isFavorite)
        }
    }


    private fun parseCrossingDate(dateString: String): Date? {
        val parseDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a") //e.g. "2019-08-27 08:40 AM" Can also be "Not Available"
        parseDateFormat.timeZone = TimeZone.getTimeZone("America/Los_Angeles")
        return try {
            return parseDateFormat.parse(dateString)
        } catch (e: ParseException) {
            null
        }
    }

}