package gov.wa.wsdot.android.wsdot.db.travelerinfo

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.db.bordercrossing.BorderCrossingDao
import gov.wa.wsdot.android.wsdot.utils.BorderCrossingTestUtil
import gov.wa.wsdot.android.wsdot.utils.SocialMediaTestUtil
import gov.wa.wsdot.android.wsdot.utils.TrafficTestUtil
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class NewsReleaseDaoTest {

    private lateinit var newsReleaseDao: NewsReleaseDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        newsReleaseDao = db.newsReleaseDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val news1 = TrafficTestUtil.createNewsRelease(link = "test")

        newsReleaseDao.insertNewReleases(listOf(news1))

        val newsRelease = newsReleaseDao.loadNewsReleases().getOrAwaitValue().first()

        assertThat(newsRelease, notNullValue())
        assertThat(newsRelease, equalTo(news1))

    }

    @Test
    fun deleteAll() {

        val news1 = TrafficTestUtil.createNewsRelease(link = "test 1")
        val news2 = TrafficTestUtil.createNewsRelease(link = "test 2")
        val news3 = TrafficTestUtil.createNewsRelease(link = "test 3")

        newsReleaseDao.insertNewReleases(listOf(news1, news2, news3))

        val newsReleases = newsReleaseDao.loadNewsReleases().getOrAwaitValue()

        assertThat(newsReleases, notNullValue())
        assertThat(newsReleases.size, equalTo(3))

        newsReleaseDao.deleteOldNewsReleases()

        val emptyNewsReleases = newsReleaseDao.loadNewsReleases().getOrAwaitValue()

        assertThat(emptyNewsReleases.size, equalTo(0))

    }

}