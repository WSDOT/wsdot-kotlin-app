package gov.wa.wsdot.android.wsdot.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import gov.wa.wsdot.android.wsdot.db.notificationtopic.NotificationTopic
import gov.wa.wsdot.android.wsdot.repository.NotificationTopicRepository
import gov.wa.wsdot.android.wsdot.util.network.Resource
import javax.inject.Inject


class NotificationsViewModel @Inject constructor(notificationTopicRepository: NotificationTopicRepository) : ViewModel() {

    private val repo = notificationTopicRepository

    // mediator handles resubscribe on refresh
    private var topicsLiveData : LiveData<Resource<List<NotificationTopic>>> = notificationTopicRepository.loadTopics(false)

    val topics = MediatorLiveData<Resource<List<NotificationTopic>>>()
    val topicsMap = MediatorLiveData<HashMap<String, List<NotificationTopic>>>()

    init {
        topics.addSource(topicsLiveData) { topics.value = it }
        topicsMap.addSource(topicsLiveData) { topicsMap.value = mapTopics(it.data)  }
    }

    fun updateSubscription(topicId: String, subscribed: Boolean) {
        repo.updateSubscription(topicId, subscribed)
    }

    fun refresh() {
        topics.removeSource(topicsLiveData)
        topicsLiveData = repo.loadTopics(true)
        topics.addSource(topicsLiveData) { topics.value = it }
    }

    private fun mapTopics(topics: List<NotificationTopic>?): HashMap<String, List<NotificationTopic>> {

        topics?.let {
            val mTopicsMap = HashMap<String, MutableList<NotificationTopic>>()

            for (topic in topics) {

                val category = topic.category

                if (mTopicsMap[category] == null) {
                    val mTopics = mutableListOf<NotificationTopic>()
                    mTopics.add(topic)
                    mTopicsMap[category] = mTopics
                } else {
                    mTopicsMap[category]?.add(topic)
                }

            }

            val map = HashMap<String, List<NotificationTopic>>()
            for (key in mTopicsMap.keys) {
                map[key] = mTopicsMap[key] as List<NotificationTopic>
            }
            return map
        }
        return HashMap()
    }

}