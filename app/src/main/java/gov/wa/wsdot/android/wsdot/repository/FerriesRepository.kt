package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.network.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FerriesRepository  @Inject constructor(
    private val webservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val ferriesDao: ferriesScheduleDao
) {

    fun loadSchedule(): LiveData<Resource<List<String>>> {
        return object : NetworkBoundResource<List<String>, List<String>>(appExecutors) {

            override fun saveCallResult(item: List<String>) {
                ferriesDao.insertRepos(item)
            }

            override fun shouldFetch(data: List<String>?): Boolean {
                return data == null || data.isEmpty() // || repoListRateLimit.shouldFetch(owner)
            }

             override fun loadFromDb() = ferriesDao.loadSchedules()

            override fun createCall() = webservice.getFerrySchedules()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }


}