package gov.wa.wsdot.android.wsdot.repository

import android.util.Log
import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySchedule
import gov.wa.wsdot.android.wsdot.db.ferries.FerryScheduleDao
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerryScheduleResponse
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.network.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FerriesRepository  @Inject constructor(
    private val webservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val ferryDao: FerryScheduleDao
) {

    fun loadSchedule(): LiveData<Resource<List<FerrySchedule>>> {

        return object : NetworkBoundResource<List<FerrySchedule>, List<FerryScheduleResponse>>(appExecutors) {

            override fun saveCallResult(item: List<FerryScheduleResponse>) {

                var dbSchedulesList = arrayListOf<FerrySchedule>()

                for (scheduleResponse in item) {

                    Log.e("debug", scheduleResponse.schedule[0].sailings[0].departingTerminalName)

                    var schedule = FerrySchedule(
                        scheduleResponse.routeId,
                        scheduleResponse.description,
                        scheduleResponse.crossingTime,
                        scheduleResponse.cacheDate
                    )
                    dbSchedulesList.add(schedule)
                }

                ferryDao.insertSchedules(dbSchedulesList)
            }

            override fun shouldFetch(data: List<FerrySchedule>?): Boolean {

                // TODO: Caching time
                return data == null || data.isEmpty() // || repoListRateLimit.shouldFetch(owner)
            }

            override fun loadFromDb() = ferryDao.loadSchedules()

            override fun createCall() = webservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }


}