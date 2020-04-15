package gov.wa.wsdot.android.wsdot.db.traveltimes

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.utils.TravelTimeTestUtil
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class TravelTimeDaoTest {

    private lateinit var travelTimeDao: TravelTimeDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        travelTimeDao = db.travelTimeDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val travelTimeId = 1

        val travelTime1 = TravelTimeTestUtil.createTravelTime(travelTimeId)
        val travelTime2 = TravelTimeTestUtil.createTravelTime(2)

        travelTimeDao.insertNewTravelTimes(listOf(travelTime1, travelTime2))

        val travelTimeById = travelTimeDao.loadTravelTime(travelTimeId).getOrAwaitValue()

        assertThat(travelTimeById, notNullValue())
        assertThat(travelTimeById, equalTo(travelTime1))

    }

    @Test
    fun insertAndReadAndDelete() {

        val travelTimeId = 1

        val travelTime1 = TravelTimeTestUtil.createTravelTime(travelTimeId)
        val travelTime2 = TravelTimeTestUtil.createTravelTime(2)

        travelTimeDao.insertNewTravelTimes(listOf(travelTime1, travelTime2))

        val travelTimeById = travelTimeDao.loadTravelTime(travelTimeId).getOrAwaitValue()

        assertThat(travelTimeById, notNullValue())
        assertThat(travelTimeById, equalTo(travelTime1))

        travelTimeDao.markTravelTimesForRemoval()
        travelTimeDao.deleteOldTravelTimes()

        val nullTime = travelTimeDao.loadTravelTime(travelTimeId).getOrAwaitValue()

        assertThat(nullTime, nullValue())

    }

    @Test
    fun updateFavorite() {

        val time1 = TravelTimeTestUtil.createTravelTime(1)
        val time2 = TravelTimeTestUtil.createTravelTime(2)
        val time3 = TravelTimeTestUtil.createTravelTime(3)

        travelTimeDao.insertNewTravelTimes(listOf(time1, time2, time3))

        travelTimeDao.updateFavorite(2, true)
        travelTimeDao.updateFavorite(3, true)

        val favoriteTravelTimes = travelTimeDao.loadFavoriteTravelTimes().getOrAwaitValue()

        assertThat(favoriteTravelTimes.first().favorite, equalTo(true))
        assertThat(favoriteTravelTimes.size, equalTo(2))

    }
}

