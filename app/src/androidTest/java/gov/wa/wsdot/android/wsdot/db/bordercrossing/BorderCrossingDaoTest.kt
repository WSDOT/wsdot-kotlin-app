package gov.wa.wsdot.android.wsdot.db.bordercrossing

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.utils.BorderCrossingTestUtil
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class BorderCrossingDaoTest {

    private lateinit var borderCrossingDao: BorderCrossingDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        borderCrossingDao = db.borderCrossingDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val crossing1 = BorderCrossingTestUtil.createBorderCrossing(1)

        borderCrossingDao.insertNewCrossings(listOf(crossing1))

        val crossing = borderCrossingDao.loadCrossings().getOrAwaitValue().first()

        assertThat(crossing, notNullValue())
        assertThat(crossing, equalTo(crossing1))

    }

    @Test
    fun insertAndReadAndDelete() {

        val crossing1 = BorderCrossingTestUtil.createBorderCrossing(1)

        borderCrossingDao.insertNewCrossings(listOf(crossing1))

        val crossing = borderCrossingDao.loadCrossings().getOrAwaitValue().first()

        assertThat(crossing, notNullValue())
        assertThat(crossing, equalTo(crossing1))

        borderCrossingDao.markForRemoval()
        borderCrossingDao.deleteOldCrossings()

        val crossings = borderCrossingDao.loadCrossings().getOrAwaitValue()

        assertThat(crossings.count(), equalTo(0))

    }

    @Test
    fun updateFavorite() {

        val crossing1 = BorderCrossingTestUtil.createBorderCrossing(1)
        val crossing2 = BorderCrossingTestUtil.createBorderCrossing(2)
        val crossing3 = BorderCrossingTestUtil.createBorderCrossing(3)

        borderCrossingDao.insertNewCrossings(listOf(crossing1, crossing2, crossing3))

        borderCrossingDao.updateFavorite(1, true)
        borderCrossingDao.updateFavorite(2, true)

        val favoriteCrossings = borderCrossingDao.loadFavoriteBorderCrossings().getOrAwaitValue()

        assertThat(favoriteCrossings.first().favorite,  equalTo(true))
        assertThat(favoriteCrossings.size, equalTo(2))

    }

}

