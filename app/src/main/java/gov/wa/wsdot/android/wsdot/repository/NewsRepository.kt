package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.travelerinfo.NewsReleaseResponse
import gov.wa.wsdot.android.wsdot.db.travelerinfo.NewsRelease
import gov.wa.wsdot.android.wsdot.db.travelerinfo.NewsReleaseDao
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.util.network.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.util.network.Resource
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val dataWebservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val newsReleaseDao: NewsReleaseDao
) {

    fun loadNews(forceRefresh: Boolean): LiveData<Resource<List<NewsRelease>>> {

        return object : NetworkBoundResource<List<NewsRelease>, NewsReleaseResponse>(appExecutors) {

            override fun saveCallResult(item: NewsReleaseResponse) = saveNews(item)

            override fun shouldFetch(data: List<NewsRelease>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].pubdate, x = 60)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = newsReleaseDao.loadNewsReleases()

            override fun createCall() = dataWebservice.getNewsItems()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    private fun saveNews(newsReleaseResponse: NewsReleaseResponse) {

        var dbNewsList = arrayListOf<NewsRelease>()

        for (newsItem in newsReleaseResponse.news.items) {

            val news = NewsRelease(
                newsItem.link,
                newsItem.title,
                newsItem.description,
                parseNewsDate(newsItem.pubdate)
            )

            dbNewsList.add(news)

        }

        newsReleaseDao.updateNewsReleases(dbNewsList)
    }

    private fun parseNewsDate(newsDate: String): Date {
        val parseDateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z") //e.g. "Wed, 14 Aug 2019 00:10:45 +0000"
        parseDateFormat.timeZone = TimeZone.getTimeZone("America/Los_Angeles")
        return parseDateFormat.parse(newsDate)

    }
}
