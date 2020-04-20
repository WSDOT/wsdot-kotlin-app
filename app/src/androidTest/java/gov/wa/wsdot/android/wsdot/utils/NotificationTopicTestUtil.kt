package gov.wa.wsdot.android.wsdot.utils

import gov.wa.wsdot.android.wsdot.db.notificationtopic.NotificationTopic
import java.util.*

object NotificationTopicTestUtil {
    fun createNotificationTopic(
        topic: String = ""
    ) = NotificationTopic(
        topic = topic,
        title = "",
        category = "",
        subscribed = false,
        localCacheDate = Date(),
        remove = false
    )
}