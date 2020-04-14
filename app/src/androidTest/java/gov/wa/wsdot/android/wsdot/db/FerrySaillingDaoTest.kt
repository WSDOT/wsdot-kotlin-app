package gov.wa.wsdot.android.wsdot.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import gov.wa.wsdot.android.wsdot.FerriesTestUtil
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailingDao
import gov.wa.wsdot.android.wsdot.db.ferries.FerrySailingWithSpacesDao
import gov.wa.wsdot.android.wsdot.getOrAwaitValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4ClassRunner::class)
class FerrySailingDaoTest {

    private lateinit var ferrySailingDao: FerrySailingDao
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
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {

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

}