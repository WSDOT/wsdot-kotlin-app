package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WsdotApiService
import gov.wa.wsdot.android.wsdot.api.response.ferries.VesselResponse
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailingWithSpaces
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailingWithSpacesDao
import gov.wa.wsdot.android.wsdot.db.ferries.Vessel
import gov.wa.wsdot.android.wsdot.db.ferries.VesselDao
import gov.wa.wsdot.android.wsdot.util.ApiKeys
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.util.network.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.util.network.Resource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VesselRepository @Inject constructor(
    private val wsdotWebservice: WsdotApiService,
    private val appExecutors: AppExecutors,
    private val vesselDao: VesselDao,
    private val ferrySailingWithSpacesDao: FerrySailingWithSpacesDao
) {

    fun loadVessels(forceRefresh: Boolean): LiveData<Resource<List<Vessel>>> {

        return object : NetworkBoundResource<List<Vessel>, List<VesselResponse>>(appExecutors) {

            override fun saveCallResult(item: List<VesselResponse>) = saveVessels(item)

            override fun shouldFetch(data: List<Vessel>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {

                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 2)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = vesselDao.loadVessels()

            override fun createCall() = wsdotWebservice.getFerryVessels(ApiKeys.WSDOT_KEY)

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }


    fun loadSailingWithVessels(routeId: Int, departingId: Int, arrivingId: Int, sailingDate: Date, forceRefresh: Boolean): LiveData<Resource<List<FerrySailingWithSpaces>>> {

        return object : NetworkBoundResource<List<FerrySailingWithSpaces>, List<VesselResponse>>(appExecutors) {

            override fun saveCallResult(item: List<VesselResponse>) = saveVessels(item)

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

                return update || forceRefresh
            }

            override fun loadFromDb() = ferrySailingWithSpacesDao.loadSailingsWithSpaces(routeId, departingId, arrivingId, sailingDate)

            override fun createCall() = wsdotWebservice.getFerryVessels(ApiKeys.WSDOT_KEY)

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()

    }


    fun loadVessel(vesselId: Int): LiveData<Resource<Vessel>> {

        return object : NetworkBoundResource<Vessel, List<VesselResponse>>(appExecutors) {

            override fun saveCallResult(item: List<VesselResponse>) = saveVessels(item)

            override fun shouldFetch(data: Vessel?): Boolean {

                var update = false

                if (data != null) {
                    if (TimeUtils.isOverXMinOld(data.localCacheDate, x = 2)) {
                        update = true
                    }
                }

                return update
            }

            override fun loadFromDb() = vesselDao.loadVessel(vesselId)

            override fun createCall() = wsdotWebservice.getFerryVessels(ApiKeys.WSDOT_KEY)

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

/*
    // TODO: favorite vessel locations
    fun updateFavorite(routeId: Int, isFavorite: Boolean) {
        appExecutors.diskIO().execute {
            ferryScheduleDao.updateFavorite(routeId, isFavorite)
        }
    }
*/

    private fun saveVessels(vesselResponse: List<VesselResponse>) {

        var dbVesselList = arrayListOf<Vessel>()

        for (vesselItem in vesselResponse) {

            var schDepart: Date? = null
            if (vesselItem.scheduledDeparture != null) {
                schDepart =  Date(vesselItem.scheduledDeparture.substring(6, 19).toLong())
            }

            var leftDock: Date? = null
            if (vesselItem.leftDock != null){
                leftDock = Date(vesselItem.leftDock.substring(6, 19).toLong())
            }

            var eta: Date? = null
            if (vesselItem.eta != null) {
                eta = Date(vesselItem.eta.substring(6, 19).toLong())
            }

            var vessel = Vessel(
                vesselItem.vesselId,
                vesselItem.vesselName,
                vesselItem.departingTerminalId,
                vesselItem.departingTerminalName,
                vesselItem.arrivingTerminalId,
                vesselItem.arrivingTerminalName,
                vesselItem.latitude,
                vesselItem.longitude,
                vesselItem.speed,
                vesselItem.heading,
                vesselItem.inService,
                vesselItem.atDock,
                schDepart,
                leftDock,
                eta,
                vesselItem.etaBasis,
                Date(),
                Date(vesselItem.timeStamp.substring(6, 19).toLong())
            )

            dbVesselList.add(vessel)

        }

        vesselDao.updateVessels(dbVesselList)
    }



}