package gov.wa.wsdot.android.wsdot.repository

import android.util.Log
import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WsdotApiService
import gov.wa.wsdot.android.wsdot.api.response.socialmedia.TwitterResponse
import gov.wa.wsdot.android.wsdot.db.socialmedia.Tweet
import gov.wa.wsdot.android.wsdot.db.socialmedia.TweetDao
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.util.network.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.util.network.Resource
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TwitterRepository @Inject constructor(
    private val wsdotApiService: WsdotApiService,
    private val appExecutors: AppExecutors,
    private val tweetDao: TweetDao
) {

    fun loadTweetsForAccount(accountName: String, forceRefresh: Boolean): LiveData<Resource<List<Tweet>>> {

        return object : NetworkBoundResource<List<Tweet>, List<TwitterResponse>>(appExecutors) {

            override fun saveCallResult(items: List<TwitterResponse>) = saveTweets(items)

            override fun shouldFetch(data: List<Tweet>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 0)) {
                        update = true
                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = tweetDao.loadTweets()

            override fun createCall() = wsdotApiService.getWSDOTTweets(accountName)

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    private fun saveTweets(tweetsResponse: List<TwitterResponse>) {

        val dbTweetList = arrayListOf<Tweet>()

        for (tweetItem in tweetsResponse) {

            Log.e("debug", tweetItem.createdAt)
            
            val tweet = Tweet(
                tweetId = tweetItem.id,
                userId = tweetItem.user.id,
                userName = tweetItem.user.name,
                text = tweetItem.text,
                mediaUrl = tweetItem.entities.media?.get(0)?.mediaUrl,
                createdAt = parseTwitterDate(tweetItem.createdAt),
                localCacheDate = Date()
            )

            Log.e("debug", tweet.createdAt.toString())

            dbTweetList.add(tweet)
        }

        tweetDao.updateTweets(dbTweetList)
    }


    private fun parseTwitterDate(tweetDate: String): Date {
        val parseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH) //e.g. "2019-09-11T16:42:31.000Z"
        parseDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return parseDateFormat.parse(tweetDate) ?: Date()
    }
}