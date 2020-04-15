package gov.wa.wsdot.android.wsdot.db.ferries

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.utils.FerriesTestUtil
import gov.wa.wsdot.android.wsdot.db.WsdotDB
import gov.wa.wsdot.android.wsdot.utils.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4ClassRunner::class)
class FerrySailingWithSpacesDaoTest {

    private lateinit var ferrySailingDao: FerrySailingDao
    private lateinit var vesselDao: VesselDao
    private lateinit var spacesDao: FerrySpaceDao
    private lateinit var ferrySailingWithSailingDao: FerrySailingWithSpacesDao

    private lateinit var db: WsdotDB

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WsdotDB::class.java)
            .build()
        ferrySailingDao = db.ferrySailingDao()
        ferrySailingWithSailingDao = db.ferrySailingWithSpacesDao()
        vesselDao = db.vesselDao()
        spacesDao = db.ferrySpaceDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadSailing() {

        val routeId = 0
        val departingTerminalId = 1
        val arrivingTerminalId = 2
        val sailingDate = Date()
        val departingTime = Date()

        val sailing1 = FerriesTestUtil.createFerrySailing(
            route = routeId,
            departingTerminalId = departingTerminalId,
            arrivingTerminalId = arrivingTerminalId,
            sailingDate = sailingDate,
            departingTime = departingTime
        )

        ferrySailingDao.insertSailings(listOf(sailing1))

        val sailings = ferrySailingWithSailingDao.loadSailingsWithSpaces(
            routeId,
            departingTerminalId,
            arrivingTerminalId,
            sailingDate
        ).getOrAwaitValue()

        assertThat(sailings, notNullValue())
        assertThat(sailings.size, equalTo(1))
        assertThat(sailings.first().route, equalTo(routeId))
        assertThat(sailings.first().arrivingTerminalId, equalTo(arrivingTerminalId))
        assertThat(sailings.first().departingTerminalId, equalTo(departingTerminalId))
        assertThat(sailings.first().sailingDate, equalTo(sailingDate))
    }

    // test with vessels

    @Test
    fun insertAndReadSailingWithVessel() {

        val routeId = 0
        val departingTerminalId = 1
        val arrivingTerminalId = 2
        val sailingDate = Date()
        val departingTime = Date()

        val sailing1 = FerriesTestUtil.createFerrySailing(
            route = routeId,
            departingTerminalId = departingTerminalId,
            arrivingTerminalId = arrivingTerminalId,
            sailingDate = sailingDate,
            departingTime = departingTime
        )

        ferrySailingDao.insertSailings(listOf(sailing1))

        val vesselId = 4

        val vessel1 = FerriesTestUtil.createVessel(
            vesselId = vesselId,
            departingTerminalId = departingTerminalId,
            arrivingTerminalId = arrivingTerminalId,
            departingTime = departingTime
        )

        vesselDao.insertVessels(listOf(vessel1))

        val sailings = ferrySailingWithSailingDao.loadSailingsWithSpaces(
            routeId,
            departingTerminalId,
            arrivingTerminalId,
            sailingDate
        ).getOrAwaitValue()

        assertThat(sailings, notNullValue())
        assertThat(sailings.size, equalTo(1))
        assertThat(sailings.first().route, equalTo(routeId))
        assertThat(sailings.first().arrivingTerminalId, equalTo(arrivingTerminalId))
        assertThat(sailings.first().departingTerminalId, equalTo(departingTerminalId))
        assertThat(sailings.first().sailingDate, equalTo(sailingDate))
        assertThat(sailings.first().vesselId, equalTo(vesselId))

    }

    @Test
    fun insertAndReadSailingWithoutVessel() {

        val routeId = 0
        val departingTerminalId = 1
        val arrivingTerminalId = 2
        val sailingDate = Date()
        val departingTime = Date()

        val sailing1 = FerriesTestUtil.createFerrySailing(
            route = routeId,
            departingTerminalId = departingTerminalId,
            arrivingTerminalId = arrivingTerminalId,
            sailingDate = sailingDate,
            departingTime = departingTime
        )

        ferrySailingDao.insertSailings(listOf(sailing1))

        val vesselId = 4
        val vesselDepartTime = Date()

        val vessel1 = FerriesTestUtil.createVessel(
            vesselId = vesselId,
            departingTerminalId = departingTerminalId,
            arrivingTerminalId = arrivingTerminalId,
            departingTime = vesselDepartTime
        )

        vesselDao.insertVessels(listOf(vessel1))

        val sailings = ferrySailingWithSailingDao.loadSailingsWithSpaces(
            routeId,
            departingTerminalId,
            arrivingTerminalId,
            sailingDate
        ).getOrAwaitValue()

        assertThat(sailings, notNullValue())
        assertThat(sailings.size, equalTo(1))
        assertThat(sailings.first().route, equalTo(routeId))
        assertThat(sailings.first().arrivingTerminalId, equalTo(arrivingTerminalId))
        assertThat(sailings.first().departingTerminalId, equalTo(departingTerminalId))
        assertThat(sailings.first().sailingDate, equalTo(sailingDate))
        assertThat(sailings.first().vesselId, nullValue())

    }

    @Test
    fun insertAndReadSailingWithoutSpaces() {

        val routeId = 0
        val departingTerminalId = 1
        val arrivingTerminalId = 2
        val sailingDate = Date()
        val departingTime = Date()

        val sailing1 = FerriesTestUtil.createFerrySailing(
            route = routeId,
            departingTerminalId = departingTerminalId,
            arrivingTerminalId = arrivingTerminalId,
            sailingDate = sailingDate,
            departingTime = departingTime
        )

        ferrySailingDao.insertSailings(listOf(sailing1))

        val departingTerminalId2 = 2
        val arrivingTerminalId2 = 1
        val spaces = 47

        val space1 = FerriesTestUtil.createFerrySpace(
            departingTerminalId = departingTerminalId2,
            arrivingTerminalId = arrivingTerminalId2,
            departureTime = departingTime,
            currentSpaces = spaces
        )

        spacesDao.insertSpaces(listOf(space1))

        val sailings = ferrySailingWithSailingDao.loadSailingsWithSpaces(
            routeId,
            departingTerminalId,
            arrivingTerminalId,
            sailingDate
        ).getOrAwaitValue()

        assertThat(sailings, notNullValue())
        assertThat(sailings.size, equalTo(1))
        assertThat(sailings.first().route, equalTo(routeId))
        assertThat(sailings.first().arrivingTerminalId, equalTo(arrivingTerminalId))
        assertThat(sailings.first().departingTerminalId, equalTo(departingTerminalId))
        assertThat(sailings.first().sailingDate, equalTo(sailingDate))
        assertThat(sailings.first().spaces, nullValue())

    }



    @Test
    fun insertAndReadSailingWithSpaces() {

        val routeId = 0
        val departingTerminalId = 1
        val arrivingTerminalId = 2
        val sailingDate = Date()
        val departingTime = Date()

        val sailing1 = FerriesTestUtil.createFerrySailing(
            route = routeId,
            departingTerminalId = departingTerminalId,
            arrivingTerminalId = arrivingTerminalId,
            sailingDate = sailingDate,
            departingTime = departingTime
        )

        ferrySailingDao.insertSailings(listOf(sailing1))

        val spaces = 47

        val space1 = FerriesTestUtil.createFerrySpace(
            departingTerminalId = departingTerminalId,
            arrivingTerminalId = arrivingTerminalId,
            departureTime = departingTime,
            currentSpaces = spaces
        )

        spacesDao.insertSpaces(listOf(space1))

        val sailings = ferrySailingWithSailingDao.loadSailingsWithSpaces(
            routeId,
            departingTerminalId,
            arrivingTerminalId,
            sailingDate
        ).getOrAwaitValue()

        assertThat(sailings, notNullValue())
        assertThat(sailings.size, equalTo(1))
        assertThat(sailings.first().route, equalTo(routeId))
        assertThat(sailings.first().arrivingTerminalId, equalTo(arrivingTerminalId))
        assertThat(sailings.first().departingTerminalId, equalTo(departingTerminalId))
        assertThat(sailings.first().sailingDate, equalTo(sailingDate))
        assertThat(sailings.first().spaces, equalTo(spaces))

    }
}