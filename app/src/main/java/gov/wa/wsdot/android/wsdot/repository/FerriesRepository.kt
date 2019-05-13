package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gov.wa.wsdot.android.wsdot.api.Webservice
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FerriesRepository  @Inject constructor(
  //  private val appExecutors: AppExecutors,
   // private val db: GithubDb,
  //  private val repoDao: RepoDao,
  //  private val githubService: GithubService
) {

    private val webservice: Webservice = TODO()

    fun getSchedule(): LiveData<String> {
        // This isn't an optimal implementation. We'll fix it later.
        val data = MutableLiveData<String>()

        webservice.getFerrySchedules().enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                data.value = response.body()
            }

            // Error case is left out for brevity.
            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO()
            }
        })
        return data
    }
}