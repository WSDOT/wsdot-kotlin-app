package gov.wa.wsdot.android.wsdot.db.notificationtopic

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.utils.NotificationTopicTestUtil
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class NotificationTopicDaoTest {

    private lateinit var notificationTopicDao: NotificationTopicDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        notificationTopicDao = db.notificationTopicDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val topicId = "test"
        val topic1 = NotificationTopicTestUtil.createNotificationTopic(topicId)

        notificationTopicDao.insertNewTopics(listOf(topic1))

        val topicFromDb = notificationTopicDao.loadTopics().getOrAwaitValue().first()

        assertThat(topicFromDb, notNullValue())
        assertThat(topicFromDb, equalTo(topic1))

    }

    @Test
    fun deleteAll() {

        notificationTopicDao.insertNewTopics(listOf(
            NotificationTopicTestUtil.createNotificationTopic("1"),
            NotificationTopicTestUtil.createNotificationTopic("2"),
            NotificationTopicTestUtil.createNotificationTopic("3")
        ))

        val topicsFromDb = notificationTopicDao.loadTopics().getOrAwaitValue()

        assertThat(topicsFromDb, notNullValue())
        assertThat(topicsFromDb.size, equalTo(3))

        notificationTopicDao.markTopicsForRemoval()
        notificationTopicDao.deleteOldTopics()

        val emptyTopics = notificationTopicDao.loadTopics().getOrAwaitValue()

        assertThat(emptyTopics.size, equalTo(0))

    }

    @Test
    fun updateSubscription() {

        val topicId = "test"
        val topic1 = NotificationTopicTestUtil.createNotificationTopic(topicId)

        notificationTopicDao.insertNewTopics(listOf(topic1))

        val topicFromDb = notificationTopicDao.loadTopics().getOrAwaitValue().first()

        assertThat(topicFromDb, notNullValue())
        assertThat(topicFromDb, equalTo(topic1))
        assertThat(topicFromDb.subscribed, equalTo(false))

        notificationTopicDao.updateSubscribed(topicId, true)

        val subbedTopic = notificationTopicDao.loadTopics().getOrAwaitValue().first()

        assertThat(subbedTopic.topic, equalTo(topic1.topic))
        assertThat(subbedTopic.subscribed, equalTo(true))

    }

    @Test
    fun keepSubAfterUpdate() {

        val topicId = "1"
        val topic1 = NotificationTopicTestUtil.createNotificationTopic(topicId)

        notificationTopicDao.insertNewTopics(listOf(
            topic1
        ))

        notificationTopicDao.updateSubscribed(topicId, true)

        notificationTopicDao.markTopicsForRemoval()
        notificationTopicDao.updateTopics(listOf(topic1))
        notificationTopicDao.deleteOldTopics()

        val subbedTopic = notificationTopicDao.loadTopics().getOrAwaitValue().first()

        assertThat(subbedTopic.topic, equalTo(topic1.topic))
        assertThat(subbedTopic.subscribed, equalTo(true))

    }
}