package gov.wa.wsdot.android.wsdot.db.socialmedia


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
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class TweetDaoTest {

    private lateinit var tweetDao: TweetDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        tweetDao = db.tweetDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val tweet1 = SocialMediaTestUtil.createTweet("1")

        tweetDao.insertNewTweets(listOf(tweet1))

        val tweet = tweetDao.loadTweets().getOrAwaitValue().first()

        assertThat(tweet, notNullValue())
        assertThat(tweet, equalTo(tweet1))

    }

    @Test
    fun insertAndReadAndDelete() {

        val tweet1 = SocialMediaTestUtil.createTweet("1")

        tweetDao.insertNewTweets(listOf(tweet1))

        val tweet = tweetDao.loadTweets().getOrAwaitValue().first()

        assertThat(tweet, notNullValue())
        assertThat(tweet, equalTo(tweet1))

        tweetDao.deleteOldTweets()

        val tweets = tweetDao.loadTweets().getOrAwaitValue()

        assertThat(tweets.count(), equalTo(0))

    }

}