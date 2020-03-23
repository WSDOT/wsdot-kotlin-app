package gov.wa.wsdot.android.wsdot.repository

import androidx.lifecycle.LiveData
import gov.wa.wsdot.android.wsdot.api.WebDataService
import gov.wa.wsdot.android.wsdot.api.response.notifications.NotificationTopicResponse
import gov.wa.wsdot.android.wsdot.db.notificationtopic.NotificationTopic
import gov.wa.wsdot.android.wsdot.db.notificationtopic.NotificationTopicDao
import gov.wa.wsdot.android.wsdot.util.AppExecutors
import gov.wa.wsdot.android.wsdot.util.TimeUtils
import gov.wa.wsdot.android.wsdot.model.common.NetworkBoundResource
import gov.wa.wsdot.android.wsdot.model.common.Resource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationTopicRepository @Inject constructor(
    private val dataWebservice: WebDataService,
    private val appExecutors: AppExecutors,
    private val notificationTopicDao: NotificationTopicDao
) {

    fun loadTopics(forceRefresh: Boolean): LiveData<Resource<List<NotificationTopic>>> {

        return object : NetworkBoundResource<List<NotificationTopic>, NotificationTopicResponse>(appExecutors) {

            override fun saveCallResult(item: NotificationTopicResponse) = saveTopics(item)

            override fun shouldFetch(data: List<NotificationTopic>?): Boolean {

                var update = false

                if (data != null && data.isNotEmpty()) {
                    if (TimeUtils.isOverXMinOld(data[0].localCacheDate, x = 4320)) {
                        update = true

                    }
                } else {
                    update = true
                }

                return forceRefresh || update
            }

            override fun loadFromDb() = notificationTopicDao.loadTopics()

            override fun createCall() = dataWebservice.getNotificationTopics()

            override fun onFetchFailed() {
                //repoListRateLimit.reset(owner)
            }

        }.asLiveData()
    }

    fun updateSubscription(topicId: String, isSubscribed: Boolean) {
        appExecutors.diskIO().execute {
            notificationTopicDao.updateSubscribed(topicId, isSubscribed)
        }
    }

    private fun saveTopics(topicResponse: NotificationTopicResponse) {
        val dbTopicList = arrayListOf<NotificationTopic>()
        for (topicItem in topicResponse.topics) {
            val topic = NotificationTopic(
                topicItem.topic,
                topicItem.title,
                topicItem.category,
                false,
                Date(),
                false
            )
            dbTopicList.add(topic)
        }
        notificationTopicDao.updateTopics(dbTopicList)
    }
}
