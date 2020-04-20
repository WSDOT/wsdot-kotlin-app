package gov.wa.wsdot.android.wsdot.db.traffic

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.utils.TrafficTestUtil
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4ClassRunner::class)
class FavoriteLocationDaoTest {
    private lateinit var favoriteLocationDao: FavoriteLocationDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        favoriteLocationDao = db.favoriteLocationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val favLocation1 = TrafficTestUtil.createFavoriteLocation()

        favoriteLocationDao.insertNewFavoriteLocation(favLocation1)

        val favLocations = favoriteLocationDao.loadFavoriteLocations().getOrAwaitValue()

        assertThat(favLocations.size, equalTo(1))

        val favLocation = favLocations.first()

        assertThat(favLocation, notNullValue())
        assertThat(favLocation, equalTo(favLocation1))
    }

    @Test
    fun deleteAll() {

        favoriteLocationDao.insertNewFavoriteLocation(
            TrafficTestUtil.createFavoriteLocation()
        )

        favoriteLocationDao.insertNewFavoriteLocation(
            TrafficTestUtil.createFavoriteLocation()
        )

        favoriteLocationDao.insertNewFavoriteLocation(
            TrafficTestUtil.createFavoriteLocation()
        )

        val favLocations = favoriteLocationDao.loadFavoriteLocations().getOrAwaitValue()
        assertThat(favLocations.size, equalTo(3))

        favoriteLocationDao.deleteAllFavoriteLocations()

        val favLocations2 = favoriteLocationDao.loadFavoriteLocations().getOrAwaitValue()
        assertThat(favLocations2.size, equalTo(0))

    }

    @Test
    fun deleteOne() {

        favoriteLocationDao.insertNewFavoriteLocation(
            TrafficTestUtil.createFavoriteLocation()
        )

        val date = Date()
        val favLocation1 = TrafficTestUtil.createFavoriteLocation(creationDate = date)
        favoriteLocationDao.insertNewFavoriteLocation(
            favLocation1
        )

        favoriteLocationDao.insertNewFavoriteLocation(
            TrafficTestUtil.createFavoriteLocation()
        )

        val favLocations = favoriteLocationDao.loadFavoriteLocations().getOrAwaitValue()
        assertThat(favLocations.size, equalTo(3))

        favoriteLocationDao.deleteFavoriteLocation(date)

        val favLocations2 = favoriteLocationDao.loadFavoriteLocations().getOrAwaitValue()
        assertThat(favLocations2.size, equalTo(2))

        for (favLocation in favLocations2) {
            assertNotEquals(favLocation, favLocation1)
        }

    }


    @Test
    fun renameFavoriteLocation() {

        val date = Date()
        val title1 = "old title"
        val favLocation1 = TrafficTestUtil.createFavoriteLocation(creationDate = date, title = title1)

        favoriteLocationDao.insertNewFavoriteLocation(favLocation1)

        val favLocation = favoriteLocationDao.loadFavoriteLocations().getOrAwaitValue().first()

        assertThat(favLocation, notNullValue())
        assertThat(favLocation.title, equalTo(title1))
        assertThat(favLocation, equalTo(favLocation1))

        val title2 = "new title"
        favoriteLocationDao.updateFavoriteLocationTitle(creationDate = date, title = title2)
        val newTitleLocation = favoriteLocationDao.loadFavoriteLocations().getOrAwaitValue().first()

        assertThat(newTitleLocation.title, equalTo(title2))
        assertThat(newTitleLocation.creationDate, equalTo(date))

    }

}
