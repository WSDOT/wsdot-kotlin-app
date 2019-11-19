package gov.wa.wsdot.android.wsdot.db.notificationtopic

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
abstract class NotificationTopicDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertNewTopics(topics: List<NotificationTopic>)

    @Query("SELECT * FROM NotificationTopic ORDER BY NotificationTopic.category ASC")
    abstract fun loadTopics(): LiveData<List<NotificationTopic>>

    @Query("UPDATE NotificationTopic SET subscribed = :isSubscribed WHERE topic = :topic")
    abstract fun updateSubscribed(topic: String, isSubscribed: Boolean)

    @Transaction
    open fun updateTopics(topics: List<NotificationTopic>) {
        markTopicsForRemoval()
        for (topic in topics) {
            updateTopics(
                topic.topic,
                topic.title,
                topic.category,
                Date(),
                false)
        }
        deleteOldTopics()
        insertNewTopics(topics)
    }

    @Query("UPDATE NotificationTopic SET remove = 1")
    abstract fun markTopicsForRemoval()

    @Query("""
        UPDATE NotificationTopic SET
        title = :title,
        category = :category,
        remove = :remove,
        localCacheDate = :localCacheDate
        WHERE topic = :topic
    """)
    abstract fun updateTopics(
        topic: String,
        title: String,
        category: String,
        localCacheDate: Date,
        remove: Boolean)

    @Query("DELETE FROM NotificationTopic WHERE remove = 1")
    abstract fun deleteOldTopics()

}