package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.mountainpass.MountainPassResponse
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPass
import gov.wa.wsdot.android.wsdot.db.mountainpass.MountainPassDao
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.model.common.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.model.common.Resource
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MountainPassRepository @Inject constructor(
    private val dataWebservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val mountainPassDao: MountainPassDao
) {

    fun loadPasses(forceRefresh: Boolean): LiveData<Resource<List<MountainPass>>> {

        return object : NetworkBoundResource<List<MountainPass>, MountainPassResponse>(appExecutors) {

            override fun saveCallResult(item: MountainPassResponse) = savePasses(item)

            override fun shouldFetch(data: List<MountainPass>?): Boolean {

                if (forceRefresh) {
                    return true
                }

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 5)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return update
            }

            override fun loadFromDb() = mountainPassDao.loadPasses()

            override fun createCall() = dataWebservice.getMountainPassReports()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadPass(passId: Int, forceRefresh: Boolean): LiveData<Resource<MountainPass>> {

        return object : NetworkBoundResource<MountainPass, MountainPassResponse>(appExecutors) {

            override fun saveCallResult(item: MountainPassResponse) = savePasses(item)

            override fun shouldFetch(data: MountainPass?): Boolean {

                if (forceRefresh) {
                    return true
                }

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

            override fun loadFromDb() = mountainPassDao.loadPass(passId)

            override fun createCall() = dataWebservice.getMountainPassReports()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun loadFavoritePasses(forceRefresh: Boolean): LiveData<Resource<List<MountainPass>>> {

        return object : NetworkBoundResource<List<MountainPass>, MountainPassResponse>(appExecutors) {

            override fun saveCallResult(item: MountainPassResponse) = savePasses(item)

            override fun shouldFetch(data: List<MountainPass>?): Boolean {

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

            override fun loadFromDb() = mountainPassDao.loadFavoritePasses()

            override fun createCall() = dataWebservice.getMountainPassReports()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    private fun savePasses(passResponse: MountainPassResponse) {

        var dbPassList = arrayListOf<MountainPass>()

        for (passItem in passResponse.passConditions.items) {

            val pass = MountainPass(
                passId = passItem.mountainPassId,
                passName = passItem.mountainPassName,
                roadCondition = passItem.roadCondition,
                weatherCondition = passItem.weatherCondition,
                temperatureInFahrenheit = passItem.temperatureInFahrenheit,
                elevationInFeet = passItem.elevationInFeet,
                travelAdvisoryActive = passItem.travelAdvisoryActive,
                latitude = passItem.latitude,
                longitude = passItem.longitude,
                restrictionOneText = passItem.restrictionOne.restrictionText,
                restrictionOneDirection = passItem.restrictionOne.travelDirection,
                restrictionTwoText = passItem.restrictionTwo.restrictionText,
                restrictionTwoDirection = passItem.restrictionTwo.travelDirection,
                serverCacheDate = parsePassDate(passItem.dateUpdated),
                localCacheDate = Date(),
                cameras = passItem.cameras,
                forecasts = passItem.forecast,
                favorite = false,
                remove = false
            )
            dbPassList.add(pass)

        }
        mountainPassDao.updateMountainPasses(dbPassList)
    }



    fun updateFavorite(passId: Int, isFavorite: Boolean) {
        appExecutors.diskIO().execute {
            mountainPassDao.updateFavorite(passId, isFavorite)
        }
    }

    private fun parsePassDate(passDate: List<Int>): Date {
        // DateUpdated: [2019,6,24,20,46]
        val parseDateFormat = SimpleDateFormat("yyyy,M,d,HH,m") //e.g. [2010, 11, 2, 8, 22, 32, 883, 0, 0]

        val sb = StringBuilder()
        for (dateItem in passDate) {
            sb.append(dateItem)
            sb.append(",")
        }

        val dateString = sb.toString()
        parseDateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"))
        return parseDateFormat.parse(dateString)

    }

}