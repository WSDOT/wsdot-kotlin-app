package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WsdotApiService
import gov.wa.wsdot.android.wsdot.api.response.tollrates.TollTripResponse
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSign
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSignDao
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollTrip
import gov.wa.wsdot.android.wsdot.util.ApiKeys
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.model.common.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.model.common.Resource
import javax.inject.Inject
import javax.inject.Singleton
import org.json.JSONException
import java.util.*

@Singleton
class TollSignRepository @Inject constructor(
    private val wsdotApiService: WsdotApiService,
    private val appExecutors: AppExecutors,
    private val tollSignDao: TollSignDao
) {

    fun loadTollSignsOnRouteForDirection(route: Int, direction: String, forceRefresh: Boolean): LiveData<Resource<List<TollSign>>> {

        return object : NetworkBoundResource<List<TollSign>, List<TollTripResponse>>(appExecutors) {

            override fun saveCallResult(item: List<TollTripResponse>) = saveTollSigns(item)

            override fun shouldFetch(data: List<TollSign>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 1)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = tollSignDao.loadTollSignsOnRouteForDirection(route, direction)

            override fun createCall() = wsdotApiService.getTollTrips(ApiKeys.WSDOT_KEY)

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }


    fun loadFavoriteTollSign(forceRefresh: Boolean): LiveData<Resource<List<TollSign>>> {

        return object : NetworkBoundResource<List<TollSign>, List<TollTripResponse>>(appExecutors) {

            override fun saveCallResult(item: List<TollTripResponse>) = saveTollSigns(item)

            override fun shouldFetch(data: List<TollSign>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 1)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = tollSignDao.loadFavoriteTollSigns()

            override fun createCall() = wsdotApiService.getTollTrips(ApiKeys.WSDOT_KEY)

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun updateFavorite(id: String, isFavorite: Boolean) {
        appExecutors.diskIO().execute {
            tollSignDao.updateFavorite(id, isFavorite)
        }
    }

    private fun saveTollSigns(tollTripResponse: List<TollTripResponse>) {

        val dbSignList = arrayListOf<TollSign>()

        for (tripItem in tollTripResponse) {
            if (!shouldSkipTrip(tripItem)) {

                var startLocationName = ""

                if (tripItem.stateRoute == 405) {
                    startLocationName = filter405LocationName(tripItem.startLocationName, tripItem.travelDirection)
                } else if (tripItem.stateRoute == 167) {
                    startLocationName = filter167LocationName(tripItem.startLocationName, tripItem.travelDirection)
                }

                val trip = TollTrip(
                    tripName = tripItem.tripName,
                    endLocationName = tripItem.endLocationName,
                    currentRate = tripItem.currentToll.toFloat() * .01f, // converts API value to dollar value (ex. 75 -> $0.75)
                    message = tripItem.currentMessage,
                    endMilepost = tripItem.endMilepost,
                    endLatitude = tripItem.endLatitude,
                    endLongitude = tripItem.endLongitude
                )

                val sign = dbSignList.firstOrNull {
                    it.startLatitude == tripItem.startLatitude
                            && it.startLongitude == tripItem.startLongitude
                            && it.travelDirection == tripItem.travelDirection
                }

                if (sign != null){
                        dbSignList.first {
                            it.startLatitude == tripItem.startLatitude
                                    && it.startLongitude == tripItem.startLongitude
                                    && it.travelDirection == tripItem.travelDirection
                        }.trips.add(trip)
                } else {

                    val sign = TollSign(
                        id = startLocationName + tripItem.travelDirection,
                        locationName = startLocationName,
                        stateRoute = tripItem.stateRoute,
                        milepost = tripItem.startMilepost,
                        travelDirection = tripItem.travelDirection,
                        startLatitude = tripItem.startLatitude,
                        startLongitude = tripItem.startLongitude,
                        trips = mutableListOf(trip),
                        localCacheDate = Date(),
                        favorite = false,
                        remove = false
                    )
                    dbSignList.add(sign)
                }

            }
        }

        for (sign in dbSignList) {
            if (sign.travelDirection == "N") {
                sign.trips.sortBy { it.endMilepost }
            } else if (sign.travelDirection == "S") {
                sign.trips.sortByDescending { it.endMilepost }
            }
        }

        tollSignDao.updateTollSigns(dbSignList)
    }


    private fun filter405LocationName(locationName: String, direction: String): String {
        var locationName = locationName

        // Southbound name changes suggested by Tolling
        if (direction == "S") {

            if (locationName == "231st SE") {
                locationName = "SR 527"
            }

            if (locationName == "NE 53rd") {
                locationName = "NE 70th Place"
            }

        }

        // Northbound name changes suggested by Tolling
        if (direction == "N") {

            if (locationName == "NE 97th") {
                locationName = "NE 85th St"
            }

            if (locationName == "231st SE") {
                locationName = "SR 522"
            }

            if (locationName == "216th SE") {
                locationName = "SR 527"
            }
        }

        if (locationName == "SR 524" || locationName == "NE 4th") {
            locationName =
                (if (direction == "N") "Bellevue" else "Lynnwood") + " - Start of toll lanes"
        } else {
            locationName = "Lane entrance near $locationName"
        }

        return locationName
    }


    private fun filter167LocationName(locationName: String, direction: String): String {
        var locationName = locationName

        // Southbound name changes suggested by Tolling
        if (direction == "S") {

            if (locationName == "4th Ave N") {
                locationName = "SR 516"
            }

            if (locationName == "S 192nd St") {
                locationName = "S 180th St"
            }

            if (locationName == "S 23rd St") {
                locationName = "I-405 (Renton)"
            }
        }

        // Northbound name changes suggested by Tolling
        if (direction == "N") {

            if (locationName == "15th St SW") {
                locationName = "SR 18 (Auburn)"
            }

            if (locationName == "7th St NW") {
                locationName = "15th St SW"
            }

            if (locationName == "30th St NW") {
                locationName = "S 277th St"
            }

            if (locationName == "S 265th St") {
                locationName = "SR 516"
            }
        }

        locationName = "Lane entrance near $locationName"

        return locationName
    }

    @Throws(JSONException::class)
    private fun shouldSkipTrip(trip: TollTripResponse): Boolean {

        /*
         * Removal of these routes since their displays are already shown
         * by other signs from the API.
         */
        if (trip.startLocationName == "NE 6th" && trip.travelDirection == "N") {
            return true
        }

        if (trip.startLocationName == "216th ST SE" && trip.travelDirection == "S") {
            return true
        }

        if (trip.startLocationName == "NE 145th" && trip.travelDirection == "S") {
            return true
        }

        /*
         * Removal suggested by tolling division since it's very similar to another location
         * and difficult to come up with a label people will recognize.
         */
        if (trip.startLocationName == "NE 108th" && trip.travelDirection == "S") {
            return true
        }

        // SR 167 trips to remove
        if (trip.startLocationName == "James St" && trip.travelDirection == "N") {
            return true
        }

        if (trip.startLocationName == "S 204th St" && trip.travelDirection == "N") {
            return true
        }

        if (trip.startLocationName == "1st Ave S" && trip.travelDirection == "S") {
            return true
        }

        if (trip.startLocationName == "12th St NW" && trip.travelDirection == "S") {
            return true
        }

        if (trip.startLocationName == "37th St NW" && trip.travelDirection == "S") {
            return true
        }

        return trip.startLocationName == "Green River" && trip.travelDirection == "S"

    }

}
