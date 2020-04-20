package gov.wa.wsdot.android.wsdot.db.tollrates

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollSignDao
import gov.wa.wsdot.android.wsdot.utils.TollRatesTestUtil
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class TollSignDaoTest {

    private lateinit var tollSignDao: TollSignDao
    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        tollSignDao = db.tollSignDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

        val route = 0
        val direction = "n"

        val tollSign = TollRatesTestUtil.createTollSign(route = route, direction = direction)

        tollSignDao.insertNewTollSigns(listOf(tollSign))

        val tollSignFromDb = tollSignDao.loadTollSignsOnRouteForDirection(route, direction).getOrAwaitValue().first()

        assertThat(tollSignFromDb, notNullValue())
        assertThat(tollSignFromDb, equalTo(tollSign))

    }

    @Test
    fun deleteAll() {

        val route = 0
        val direction = "n"

        tollSignDao.insertNewTollSigns(listOf(
            TollRatesTestUtil.createTollSign(id = "1", route = route, direction = direction),
            TollRatesTestUtil.createTollSign(id = "2", route = route, direction = direction),
            TollRatesTestUtil.createTollSign(id = "3", route = route, direction = direction)
        ))

        val tollSignsFromDb = tollSignDao.loadTollSignsOnRouteForDirection(0, "n").getOrAwaitValue()

        assertThat(tollSignsFromDb, notNullValue())
        assertThat(tollSignsFromDb.size, equalTo(3))

        tollSignDao.markTollSignsForRemoval()
        tollSignDao.deleteOldTollSigns()

        val emptyTollSigns = tollSignDao.loadTollSignsOnRouteForDirection(0, "n").getOrAwaitValue()

        assertThat(emptyTollSigns.size, equalTo(0))


    }

    @Test
    fun updateFavorite() {

        val id = "1"
        val route = 0
        val direction = "n"

        val tollSign = TollRatesTestUtil.createTollSign(id = id, route = route, direction = direction)

        tollSignDao.insertNewTollSigns(listOf(
            tollSign,
            TollRatesTestUtil.createTollSign(id = "2", route = route, direction = direction),
            TollRatesTestUtil.createTollSign(id = "3", route = route, direction = direction)
        ))

        val tollSignFromDb = tollSignDao.loadTollSignsOnRouteForDirection(route, direction).getOrAwaitValue().first()

        assertThat(tollSignFromDb, notNullValue())
        assertThat(tollSignFromDb, equalTo(tollSign))

        tollSignDao.updateFavorite(id, true)

        val favoriteTollSign = tollSignDao.loadFavoriteTollSigns().getOrAwaitValue().first()

        assertThat(favoriteTollSign.id, equalTo(tollSign.id))

    }

}

